package com.sda.BusApp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/index*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/add*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/bus*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/delete*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/edit*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/filter*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/info*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/main*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/price*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/repairs")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/score*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/trips*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/empty*")
                .hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/addnewuser")
                .hasAnyAuthority("ROLE_ADMIN")
                .antMatchers("/users")
                .hasAnyAuthority("ROLE_ADMIN")
                .antMatchers("/retrieve*")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .antMatchers("/drivers")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .anyRequest().permitAll()
                .and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("login")
                .passwordParameter("password")
                .loginProcessingUrl("/login-process")
                .defaultSuccessUrl("/main", true)
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login");
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("user")
//                .password(bCryptPasswordEncoder.encode("password"))
//                .roles("USER")
//                .and()
//                .withUser("admin")
//                .password(bCryptPasswordEncoder.encode("admin"))
//                .roles("ADMIN");
        auth.jdbcAuthentication()
                .usersByUsernameQuery("select u.login, u.password, 1 from user_credentials u where u.login=?") //1 oznacza aktywny user
                .authoritiesByUsernameQuery("select u.login, u.role, 1 from user_credentials u where u.login=?")
                .dataSource(jdbcTemplate.getDataSource())
                .passwordEncoder(bCryptPasswordEncoder);
    }


}
