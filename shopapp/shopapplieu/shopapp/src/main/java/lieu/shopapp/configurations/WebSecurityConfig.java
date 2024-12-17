package lieu.shopapp.configurations;

import lieu.shopapp.filters.JwtTokenFilter;
import lieu.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests(request -> {
                    request
                            .requestMatchers(
                                    String.format("%s/users/register",apiPrefix),
                                    String.format("%s/users/login",apiPrefix)
                            )
                            .permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/users/{id}**",apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .requestMatchers(GET,
                                    String.format("%s/categories**",apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .requestMatchers(POST,
                                    String.format("%s/categories**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/categories**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/categories**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/products**",apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .requestMatchers(POST,
                                    String.format("%s/products**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/products**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/products**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST,
                                    String.format("%s/orders/**",apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(GET,
                                    String.format("%s/orders/**",apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/orders/**",apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/orders/**",apiPrefix)).hasRole(Role.ADMIN)
                            .anyRequest().authenticated();

                });
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.setAllowedOrigins(List.of("*"));
                corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","PATH","OPTIONS"));
                corsConfiguration.setAllowedHeaders(Arrays.asList("authorization","content-type","x-auth-token"));
                corsConfiguration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", corsConfiguration);
                httpSecurityCorsConfigurer.configurationSource(source);

            }
        });
        return http.build();
    }
}