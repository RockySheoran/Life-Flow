package com.project.user_service.config;

import com.project.user_service.handler.OAuth2SuccessHandler;
import com.project.user_service.security.JwtAuthFilter;
import com.project.user_service.security.CustomAccessDeniedHandler;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private static final String[] publicRoutes = {
            "/error",
            "/auth/**",
            "/users/swagger-ui/**",
            "/users/v3/api-docs/**",
            "/users/swagger-ui.html",
            "/matching/swagger-ui/**",
            "/matching/v3/api-docs/**",
            "/matching/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/health"
    };

       @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        try {
            logger.info("Configuring security filter chain");
            
            httpSecurity.authorizeHttpRequests(auth -> {
                        logger.debug("Configuring request matchers");
                        auth.requestMatchers(publicRoutes).permitAll()
                                .requestMatchers("/auth/get-me").authenticated()
                                .anyRequest().authenticated();
                    })
                    .csrf(csrf -> {
                        logger.debug("Disabling CSRF");
                        csrf.disable();
                    })
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement(sessionConfig -> {
                        logger.debug("Setting session creation policy to STATELESS");
                        sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    })
                    .oauth2Login(oauth2Config -> {
                        logger.debug("Configuring OAuth2 login");
                        oauth2Config.failureUrl("/auth/login?error=true")
                                .successHandler(oAuth2SuccessHandler);
                    })
                    .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler))

            ;

            logger.info("Security filter chain configuration completed successfully");
            return httpSecurity.build();
        } catch (Exception e) {
            logger.error("Error configuring security filter chain", e);
            throw new RuntimeException("Failed to configure security", e);
        }
    }

}
