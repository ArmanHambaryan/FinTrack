package com.example.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import service.CustomUserDetailsService;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/",
                                        "/login",
                                        "/loginPage",
                                        "/registerPage",
                                        "/register",
                                        "/css/**",
                                        "/js/**",
                                        "/images/**").permitAll()
                                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                                .requestMatchers("/user/**").hasAnyAuthority("USER", "ADMIN")
                                .anyRequest().authenticated()

                )
                .formLogin(form ->
                        form
                                .loginPage("/loginPage")
                                .loginProcessingUrl("/login")
                                .failureHandler((request, response, exception) -> {
                                    if (exception instanceof DisabledException) {
                                        response.sendRedirect("/loginPage?blocked");
                                    } else {
                                        response.sendRedirect("/loginPage?error");
                                    }
                                })
                                .defaultSuccessUrl("/successLogin", true)
                                .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll()
                        .logoutSuccessUrl("/loginPage")


                );

        return http.build();
    }


    @Bean
    DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

}
