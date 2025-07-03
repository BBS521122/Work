package com.work.work.fliter;

import com.auth0.jwt.interfaces.Claim;
import com.work.work.constant.JwtClaimsConstant;
import com.work.work.context.UserContext;
import com.work.work.enums.RoleEnum;
import com.work.work.mapper.RedisTokenMapper;
import com.work.work.mapper.sql.UserMapper;
import com.work.work.properties.JwtProperties;
import com.work.work.security.UserDetailsImpl;
import com.work.work.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * JwtFilter 类是一个自定义的过滤器，继承自 OncePerRequestFilter。
 * 它用于拦截 HTTP 请求并验证 JWT的合法性。
 * 过滤器位于UsernamePasswordAuthenticationFilter之前，见SecurityConfig类中
 */
@Component
public class JwtFliter extends OncePerRequestFilter {
    private static final List<String> EXCLUDE_URLS = Arrays.asList("/user/login");

    /**
     * 注入 JwtProperties，用于获取 JWT 配置属性。
     */
    @Autowired
    JwtProperties jwtProperties;

    // @Autowired
    // StringRedisTemplate stringRedisTemplate;

    /**
     * 注入 UserMapper，用于从数据库中获取用户信息。
     */
    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisTokenMapper  redisTokenMapper;

    /**
     * 重写 doFilterInternal 方法，处理 JWT 验证逻辑。
     * 从请求头中获取jwt，如果jwt为空，则放行
     *
     * @param request     HttpServletRequest 对象，表示当前的 HTTP 请求。
     * @param response    HttpServletResponse 对象，表示当前的 HTTP 响应。
     * @param filterChain FilterChain 对象，用于执行过滤器链。
     * @throws ServletException 如果发生 Servlet 异常。
     * @throws IOException      如果发生 I/O 异常。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        // 获取当前请求路径
        String path = request.getRequestURI();

        // 判断是否为排除路径
        if (EXCLUDE_URLS.stream().anyMatch(path::equals)) {
            // 如果是排除路径，直接放行，不进行JWT验证
            filterChain.doFilter(request, response);
            return;
        }

        // 从请求头中获取 JWT，头部名称来源于jwtProperties
        String token = request.getHeader(jwtProperties.getTokenName());
        // 如果没有 token，直接放行
        if (token == null || token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析 token
        try {
            Map<String, Claim> claims = JwtUtils.parseJWT(jwtProperties.getSecretKey(), token);

            // 从 Redis 中获取 token
            String redisToken = redisTokenMapper.getToken(token);
            if (redisToken == null) {
                throw new RuntimeException();
            }

            // 获取自定义的 claims 数据
            Claim claim = claims.get("claims");
            Map<String, Object> map = claim.asMap();
            String username = (String) map.get(JwtClaimsConstant.USERNAME);
            String roleName = (String) map.get(JwtClaimsConstant.ROLE);
            RoleEnum role = RoleEnum.valueOf(roleName);
            // 存入ThreadLocal
            long id = ((Number) map.get(JwtClaimsConstant.ID)).longValue();
            UserContext.setUserId(id);

            // 根据用户名创建 UserDetailsImp 对象
            UserDetailsImpl userDetailsImp =  new UserDetailsImpl(userMapper.getUserByUsername(username),role);

            // 创建 UsernamePasswordAuthenticationToken 对象
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetailsImp, null, userDetailsImp.getAuthorities());

            // 将认证信息设置到 SecurityContextHolder 中
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 放行请求
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            // 清理ThreadLocal
            UserContext.clear();
        }
    }
}