package com.study.SpringSecurityMybatis.config;

import com.study.SpringSecurityMybatis.security.filter.JwtAccessTokenFilter;
import com.study.SpringSecurityMybatis.security.handler.AuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity //WebSecurityConfigurerAdapter를 오버라이드 해서 내가 직접 설정한 WebSecurity 설정을 사용하겠다.
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAccessTokenFilter jwtAccessTokenFilter;

    @Autowired
    private AuthenticationHandler authenticationHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override // 메서드를 오버라이딩 했을때 리턴타입과 매개변수 그리고 오버로딩 여부 등을 확인하는게 좋다.
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().disable();
        http.httpBasic().disable();
        http.csrf().disable();
        http.headers().frameOptions().disable(); //h2 console 접속을 위한 설정
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //security에서 세션 상태를 유지하지 않겠다.
        http.cors(); //webmcvcofig 파일을 따라가서 설정을 적용시킨다.
        http.exceptionHandling().authenticationEntryPoint(authenticationHandler); // 인증 관련 예외가 터지면 authenticationHandler 로 처리한다.
        http.authorizeRequests()
                .antMatchers("/auth/**", "/h2-console/**")
                .permitAll()
                .anyRequest()
                .authenticated();

        http.addFilterBefore(jwtAccessTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
