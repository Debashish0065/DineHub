package com.dinehub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers(

                                "/dashboard",

                                "/menu",

                                "/product/**",

                                "/cart/**",

                                "/checkout",

                                "/orders",

                                "/confirm-order",

                                "/add-to-cart/**",

                                "/increase/**",

                                "/decrease/**",

                                "/remove-from-cart/**",

                                "/profile",

                                "/profile/edit",

                                "/profile/update",

                                "/profile/change-password"

                        ).hasAnyRole("USER", "ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form

                        .loginPage("/login")

                        .successHandler(loginSuccessHandler)

                        .failureUrl("/login?error")

                        .permitAll()
                )

                .logout(logout -> logout

                        .logoutSuccessUrl("/login?logout")

                        .invalidateHttpSession(true)

                        .clearAuthentication(true)

                        .deleteCookies("JSESSIONID")

                        .permitAll()

                );

        return http.build();
    }
}