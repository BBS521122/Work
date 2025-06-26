package com.work.work.config;

import com.work.work.fliter.JwtFliter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private JwtFliter jwtFilter;

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
        return http.cors(withDefaults()).
                csrf(csrf -> csrf.disable()).
                formLogin(formLogin -> formLogin.disable()).
                sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).
                authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/user/login", "/user/register").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")// 允许匿名访问登录接口
                                .anyRequest().authenticated() // 其他请求需要认证
                ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
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
        // 支持通配符，允许所有来源
        config.setAllowedOriginPatterns(List.of("*"));
        // 允许的 HTTP 方法
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许的请求头
        config.setAllowedHeaders(List.of("*"));
        // 如果前端需要携带 cookie，允许携带凭据
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 将配置应用于所有路径
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
