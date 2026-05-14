package org.example.common.global.config

import org.example.common.global.auth.filter.JwtAuthenticationFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties::class)
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class SecurityConfig(
    @param:Lazy
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {



    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { Customizer.withDefaults<CorsConfigurer<HttpSecurity>>() }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests({ auth ->
                auth
                    .requestMatchers(
                        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",  // Swagger 문서 관련
                        "/api/auth/**", "/api/user/signup", "/api/user/verify",  // 로그인, 회원가입 관련
                        "/api/user/reset-password", "/api/groq/**"
                    ).permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/category").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/product/**").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/auction/**").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/order/*/auction").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/rating/**").permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/category").hasRole("ADMIN")
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/product").hasRole("SELLER")
                    .anyRequest().authenticated()
            }
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): org.springframework.web.cors.CorsConfigurationSource {
        val configuration: CorsConfiguration = CorsConfiguration()
        configuration.addAllowedOriginPattern("*") // 로컬/운영에 따라 조절
        configuration.addAllowedMethod("*") // GET, POST, OPTIONS 등을 모두 허용
        configuration.addAllowedHeader("*")
        configuration.allowCredentials = true

        val source = org.springframework.web.cors.UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager {
        return configuration.authenticationManager
    }
}
