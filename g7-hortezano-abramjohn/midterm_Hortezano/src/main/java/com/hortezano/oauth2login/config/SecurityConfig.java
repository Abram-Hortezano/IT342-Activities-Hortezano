package com.hortezano.oauth2login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/user-info", true)
//                        oauth2
//                        .successHandler((request, response, authentication) -> {
//                            response.sendRedirect("/home"); // Redirect after login
//                        })
                )
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .formLogin(formLogin -> formLogin.defaultSuccessUrl("/user-info", true));
                return http.build();
    }

    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService();
    }
}
