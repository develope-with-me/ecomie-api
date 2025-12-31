package org.csbf.ecomie.config;

import lombok.RequiredArgsConstructor;
import org.csbf.ecomie.constant.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Enable CORS
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                // Allow access to unauthenticated users
                .requestMatchers("/api/v1/auth/**", "/api/v1/users/**", "/api/v1/sessions/**", "/api/v1/challenges/**", "/api/v1/subscriptions/**", "/api/v1/reports/**","/api/v1/demo-controller**").permitAll()
                // Allow swagger routes
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs", "/v3/api-docs/**", "/webjars/**").permitAll()
                // permit all authenticated users
                .requestMatchers("/api/v1/secure/user/**").hasAnyAuthority(Role.USER.name(), Role.ECOMIEST.name(), Role.COACH.name(), Role.SPONSOR.name(), Role.PRAYER_WARRIOR.name(), Role.ADMIN.name(), Role.SUPER_ADMIN.name(), Role.MISSIONARY.name())
                // permit users with roles, ECOMIEST, ADMIN, SUPER_ADMIN
                .requestMatchers("/api/v1/secure/ecomiest/**").hasAnyAuthority(Role.ECOMIEST.name(), Role.ADMIN.name(), Role.SUPER_ADMIN.name())
                // Permit only ADMIN, SUPER_ADMIN
                .requestMatchers("/api/v1/secure/admin/**").hasAnyAuthority(Role.ADMIN.name(), Role.SUPER_ADMIN.name())
                .anyRequest()
                .authenticated()
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint))
                .exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
