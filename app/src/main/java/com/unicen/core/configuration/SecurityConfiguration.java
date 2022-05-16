package com.unicen.core.configuration;

import com.unicen.core.security.AuthenticationServiceFilter;
import com.unicen.core.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @Order(2)
    public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthenticationService authenticationService;

        @Autowired(required = false)
        private EnsolversCoreSecurityConfiguration securityConfiguration;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();

            List<String> extraPaths = new ArrayList<>();
            if (securityConfiguration != null) {
                extraPaths = securityConfiguration.additionalPublicPaths();
            }

            http.addFilterAfter(new AuthenticationServiceFilter(authenticationService, extraPaths), BasicAuthenticationFilter.class);
        }

        // TODO remove domains of example, and take from configuration, properties and environment variables
        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }
    }

    @Configuration
    @Order(1)
    public class ApiDocumentationSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Autowired(required = false)
        private EnsolversCoreApiDocumentationConfiguration apiDocumentationConfiguration;

        @Bean
        public UserDetailsService userDetailsService() {
            User.UserBuilder users = User.builder().passwordEncoder(encoder()::encode);
            InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
            if (apiDocumentationConfiguration != null) {
                manager.createUser(users.username(apiDocumentationConfiguration.swaggerUsername()).password(apiDocumentationConfiguration.swaggerPassword())
                        .roles("USER").build());
            }
            return manager;
        }

        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers()
                    .antMatchers("/swagger-ui/**", "/v2/api-docs", "/webjars/springfox-swagger-ui/**", "/swagger-resources/**", "/swagger-resources/").and()
                    .authorizeRequests(authorize -> authorize.anyRequest().hasRole("USER")).httpBasic(withDefaults());
        }
    }

    /**
     * This bean is invoked to remove the prefix 'ROLE_'.
     * And in this way avoid problems with role-based authorization.
     */
    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
// @formatter:on
