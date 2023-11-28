package com.example.userservice1.config;

import com.example.userservice1.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecuirtyConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

//        httpSecurity.csrf().disable()
//                .authorizeHttpRequests((requests) -> {
//                            requests.requestMatchers("/api/**").permitAll();
//
//                            requests.anyRequest().authenticated()
//                                    .and()
//                                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
////                                    .logout()
////                                    .logoutUrl("/api/logout")
////                                    .addLogoutHandler(logoutHandler)
////                                    .logoutSuccessHandler(((request, response, authentication) ->
////                                            SecurityContextHolder.clearContext()));
//                        }
//                ).cors()
//                .disable();
////                .authenticationProvider(authenticationProvider)
//                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        httpSecurity
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(((request, response, authentication) ->
                        SecurityContextHolder.clearContext()));


        return httpSecurity.build();
    }
}
