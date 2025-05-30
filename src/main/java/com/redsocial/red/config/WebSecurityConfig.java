package com.redsocial.red.config;

import com.redsocial.red.security.AuthEntryPointJwt;
import com.redsocial.red.security.AuthTokenFilter;
import com.redsocial.red.security.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

        private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

        @Autowired
        private UserDetailsServiceImpl userDetailsService;

        @Autowired
        private AuthEntryPointJwt unauthorizedHandler;

        @Autowired
        private AuthTokenFilter authTokenFilter;

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        @Order(1)
        public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
                logger.info("Configurando seguridad para API endpoints");

                http
                                .securityMatcher("/api/**")
                                .cors(cors -> {
                                        CorsConfiguration configuration = new CorsConfiguration();
                                        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
                                        configuration.setAllowedMethods(
                                                        Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                                        configuration.setAllowedHeaders(Arrays.asList("*"));
                                        configuration.setAllowCredentials(true);
                                        configuration.setMaxAge(3600L);

                                        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                                        source.registerCorsConfiguration("/**", configuration);
                                        cors.configurationSource(source);
                                })
                                .csrf(csrf -> csrf.disable())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        logger.error("Error de autenticación: {} en la ruta: {} método: {}",
                                                                        authException.getMessage(),
                                                                        request.getRequestURI(), request.getMethod());
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.setStatus(HttpStatus.FORBIDDEN.value());
                                                        String jsonResponse = String.format(
                                                                        "{\"error\": \"Acceso denegado\", \"message\": \"%s\", \"path\": \"%s\", \"method\": \"%s\"}",
                                                                        authException.getMessage().replace("\"",
                                                                                        "\\\""),
                                                                        request.getRequestURI(),
                                                                        request.getMethod());
                                                        response.getWriter().write(jsonResponse);
                                                }))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> {
                                        auth
                                                        .requestMatchers(HttpMethod.POST, "/api/auth/registro")
                                                        .permitAll()
                                                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                                                        .requestMatchers("/api/auth/**").permitAll()
                                                        .requestMatchers("/api/test/**").permitAll()
                                                        .requestMatchers("/api/diagnostic/**").permitAll()
                                                        .anyRequest().authenticated();
                                        logger.info("Configuración de autorización completada");
                                })
                                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain formLoginFilterChain(HttpSecurity http) throws Exception {
                logger.info("Configurando seguridad para endpoints web");

                http
                                .securityMatcher(AntPathRequestMatcher.antMatcher("/**"))
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                                .ignoringRequestMatchers("/api/**"))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/",
                                                                "/home",
                                                                "/index",
                                                                "/login",
                                                                "/registro",
                                                                "/debug",
                                                                "/debug_auth.html",
                                                                "/error",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/images/**",
                                                                "/webjars/**",
                                                                "/fragments/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .failureUrl("/login?error")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                                .logoutSuccessUrl("/")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll());

                return http.build();
        }
}