package com.anton.book_network.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final JWTFilter jwtFilter;

    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity

                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer ::disable)
                .authorizeHttpRequests(req -> req.requestMatchers("\"/auth/**\",\n" +
                                "                                        \"/v2/api-docs\",\n" +
                                "                                        \"/v3/api-docs\",\n" +
                                "                                        \"/v3/api-docs/**\",\n" +
                                "                                        \"/swagger-resources\",\n" +
                                "                                        \"/swagger-resources/**\",\n" +
                                "                                        \"/configuration/ui\",\n" +
                                "                                        \"/configuration/security\",\n" +
                                "                                        \"/swagger-ui/**\",\n" +
                                "                                        \"/webjars/**\",\n" +
                                "                                        \"/swagger-ui.html\"")
                        .permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
