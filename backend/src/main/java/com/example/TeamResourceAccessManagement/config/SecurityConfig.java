package com.example.TeamResourceAccessManagement.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.TeamResourceAccessManagement.service.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
            // Super Admin
            User.builder()
                .username("superadmin")
                .password(passwordEncoder.encode("super123"))
                .roles("SUPER_ADMIN")
                .build(),
            // Database Users - Managers
            User.builder()
                .username("priya.manager")
                .password(passwordEncoder.encode("priya123"))
                .roles("MANAGER")
                .build(),
            User.builder()
                .username("james.manager")
                .password(passwordEncoder.encode("james123"))
                .roles("MANAGER")
                .build(),
            User.builder()
                .username("sarah.manager")
                .password(passwordEncoder.encode("sarah123"))
                .roles("MANAGER")
                .build(),
            // Database Users - Team Leads
            User.builder()
                .username("adhnanjeff.teamlead")
                .password(passwordEncoder.encode("adhnanjeff123"))
                .roles("TEAMLEAD")
                .build(),
            User.builder()
                .username("swetha.teamlead")
                .password(passwordEncoder.encode("swetha123"))
                .roles("TEAMLEAD")
                .build(),
            User.builder()
                .username("hari.teamlead")
                .password(passwordEncoder.encode("hari123"))
                .roles("TEAMLEAD")
                .build(),
            User.builder()
                .username("sounder.teamlead")
                .password(passwordEncoder.encode("sounder123"))
                .roles("TEAMLEAD")
                .build(),
            User.builder()
                .username("tharanika.teamlead")
                .password(passwordEncoder.encode("tharanika123"))
                .roles("TEAMLEAD")
                .build(),
            User.builder()
                .username("pradeep.teamlead")
                .password(passwordEncoder.encode("pradeep123"))
                .roles("TEAMLEAD")
                .build(),
            User.builder()
                .username("adrin.teamlead")
                .password(passwordEncoder.encode("adrin123"))
                .roles("TEAMLEAD")
                .build(),
            User.builder()
                .username("lokesh.teamlead")
                .password(passwordEncoder.encode("lokesh123"))
                .roles("TEAMLEAD")
                .build(),
            User.builder()
                .username("jane.teamlead")
                .password(passwordEncoder.encode("jane123"))
                .roles("TEAMLEAD")
                .build(),
            // Database Users - Team Members
            User.builder()
                .username("arjun.dev")
                .password(passwordEncoder.encode("arjun123"))
                .roles("USER")
                .build(),
            User.builder()
                .username("emily.dev")
                .password(passwordEncoder.encode("emily123"))
                .roles("USER")
                .build(),
            User.builder()
                .username("vikram.dev")
                .password(passwordEncoder.encode("vikram123"))
                .roles("USER")
                .build(),
            User.builder()
                .username("michael.dev")
                .password(passwordEncoder.encode("michael123"))
                .roles("USER")
                .build(),
            User.builder()
                .username("jennifer.dev")
                .password(passwordEncoder.encode("jennifer123"))
                .roles("USER")
                .build(),
            User.builder()
                .username("robert.dev")
                .password(passwordEncoder.encode("robert123"))
                .roles("USER")
                .build(),
            User.builder()
                .username("sophia.test")
                .password(passwordEncoder.encode("sophia123"))
                .roles("USER")
                .build(),
            User.builder()
                .username("ravi.test")
                .password(passwordEncoder.encode("ravi123"))
                .roles("USER")
                .build(),
            User.builder()
                .username("amanda.test")
                .password(passwordEncoder.encode("amanda123"))
                .roles("USER")
                .build(),
            User.builder()
                .username("kevin.test")
                .password(passwordEncoder.encode("kevin123"))
                .roles("USER")
                .build(),
            User.builder()
                .username("rajesh.admin")
                .password(passwordEncoder.encode("rajesh123"))
                .roles("ADMIN")
                .build()
        );
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200", 
            "http://localhost:4201", 
            "http://localhost:4203",
            "http://localhost:4202"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // Swagger Documentation
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                
                // Authentication APIs
                .requestMatchers("/api/auth/**").permitAll()

                // User Management - Admin, Super Admin, and Manager can create users
                .requestMatchers("/api/users").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")
                .requestMatchers("/api/users/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")

                // Project Management - Admin, Manager, TeamLead access for management operations
                .requestMatchers("/api/projects/*/activate", "/api/projects/*/deactivate", "/api/projects/*/complete", "/api/projects/*/archive").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")
                .requestMatchers("/api/projects/my-projects").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD", "USER")
                .requestMatchers("/api/projects/*/users", "/api/projects/*/resources", "/api/projects/*").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD", "USER")
                .requestMatchers("/api/projects").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER")
                .requestMatchers("/api/projects/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD")

                // Resource Management
                .requestMatchers("/api/resources/global/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/resources/*/make-global").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/resources/*/download").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD", "USER")
                .requestMatchers("/api/resources/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD", "USER")

                // Access Request Management
                .requestMatchers("/api/access-requests/*/approve", "/api/access-requests/*/reject").hasAnyRole("ADMIN", "MANAGER", "TEAMLEAD")
                .requestMatchers("/api/access-requests/bulk-approve", "/api/access-requests/bulk-reject").hasAnyRole("ADMIN", "MANAGER", "TEAMLEAD")
                .requestMatchers("/api/access-requests/project/*", "/api/access-requests").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD", "USER")
                .requestMatchers("/api/access-requests/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD", "USER")

                // Notifications Management
                .requestMatchers("/api/notifications/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD", "USER")

                // Permission Management - Admin level access required for management operations
                .requestMatchers("/api/permissions/bulk-grant", "/api/permissions/bulk-revoke").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/api/permissions/resource/*", "/api/permissions/user/*").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD", "USER")
                .requestMatchers("/api/permissions/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD")

                // Audit Logs - Super Admin, Admin, Manager and TeamLead access
                .requestMatchers("/api/audit-logs/cleanup", "/api/audit-logs/delete-old").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/audit-logs/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEAMLEAD")

                // Health and monitoring endpoints
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").hasRole("SUPER_ADMIN")

                // Default - all authenticated users
                .anyRequest().authenticated()
            );
        return http.build();
    }
}