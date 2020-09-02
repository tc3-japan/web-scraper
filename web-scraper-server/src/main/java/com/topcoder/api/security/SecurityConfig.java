package com.topcoder.api.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * The web application security configuration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * The authentication entry point.
     */
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Remove the ROLE_ prefix.
     */
    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }


    /**
     * Configure the password encoder bean.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure web MVC.
     *
     * @return the web MVC configurer
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("*");
            }
        };
    }

    /**
     * Configure the HTTP security.
     *
     * @param httpSecurity the HTTP security
     * @throws Exception if any error occurs
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.exceptionHandling() //
                .authenticationEntryPoint(authenticationEntryPoint);

        // Authorization
        httpSecurity.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").anonymous()
                .antMatchers(HttpMethod.GET, "/**").anonymous()
                .antMatchers(HttpMethod.POST, "/**").anonymous()
                .antMatchers(HttpMethod.DELETE, "/**").anonymous()
                .antMatchers(HttpMethod.PUT, "/**").anonymous()
                .anyRequest().authenticated();

    }
}
