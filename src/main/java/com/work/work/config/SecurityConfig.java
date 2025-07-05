package com.work.work.config;

import com.work.work.fliter.CorsPreflightFilter;
import com.work.work.fliter.JwtFliter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * SecurityConfig 类配置了 Spring Security 的安全设置。
 * 它定义了密码编码器、过滤器链和认证管理器的 Bean
 * 处于Springboot启动的入口位置，由springboot自动配置
 *
 * @author 32358
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 定义一个密码编码器 Bean，用于对用户密码进行加密。
     *
     * @return PasswordEncoder 实例，使用 BCrypt 算法进行加密。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtFliter jwtFliter;

    @Autowired
    private CorsPreflightFilter corsPreflightFilter;

    /**
     * 配置安全过滤器链JwtFilter
     * - 处理跨域请求
     * - 禁用 CSRF 保护。
     * - 禁用表单登录
     * - 设置会话管理策略为无状态（STATELESS）。
     * - 配置请求授权规则
     * - 允许匿名访问 "/user/login" 接口。
     * - 其他请求需要认证。
     * -在UsernamePasswordAuthenticationFilter前加入JwtFilter
     * ---------------------------------------------<br>
     * 该配置login请求参数名必须为username和password,如果要修改，自定义类继承UsernamePasswordAuthenticationFilter，
     * 并使用addFilterAt替换默认UsernamePasswordAuthenticationFilter
     *
     * @param http HttpSecurity 对象，用于配置安全设置。
     * @return 配置完成的 SecurityFilterChain 实例。
     * @throws Exception 如果配置过程中出现错误。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable()) // 重要：这避免了默认的认证重定向
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 预检请求放行
                                .requestMatchers("/user/login", "/user/register", "/conference/wxGet","/hello","/api/news/wxGet","conference/get","receipt/submit","conference/get-info").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                // 关键：自定义认证入口点，返回 JSON 而不是重定向
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                            response.setHeader("Access-Control-Allow-Credentials", "true");
                            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                        })
                )
                .addFilterBefore(jwtFliter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 定义一个认证管理器 Bean，用于处理用户认证。
     * 暴露AuthenticationManager，使在登录接口处可以使用
     *
     * @param authenticationConfiguration AuthenticationConfiguration 对象，用于获取认证管理器。
     * @return AuthenticationManager 实例。
     * @throws Exception 如果获取认证管理器时出现错误。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置跨域资源共享（CORS）的源。
     * 该方法定义了允许的跨域请求来源、方法、头信息以及是否支持携带凭据。
     *
     * @return CorsConfigurationSource 实例，包含跨域配置。
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 明确指定允许的源，而不是使用通配符
        config.setAllowedOriginPatterns(List.of(
                "https://localhost:*",
                "https://127.0.0.1:*",
                "http://localhost:*",  // 如果前端也可能是 HTTP
                "http://127.0.0.1:*"
        ));

        // 允许的方法
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));

        // 允许的头部
        config.setAllowedHeaders(List.of("*"));

        // 允许携带凭据
        config.setAllowCredentials(true);

        // 预检请求缓存时间
        config.setMaxAge(3600L);

        // 暴露的响应头
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
