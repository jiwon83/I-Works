package com.example.iworks.global.config.jwt;


import com.example.iworks.global.config.auth.PrincipalDetails;
import com.example.iworks.global.model.entity.JWToken;
import com.example.iworks.user.model.entity.User;
import com.example.iworks.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtProvider jwtProvider) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이나 권한이 필요한 주소 요청!");

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader : " + jwtHeader);

        //header 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        //JWT 검증
        String jwtToken = jwtHeader.replace("Bearer ", "");

        if (jwtProvider.validateAccessToken(jwtToken)) {
            //access라면
            System.out.println("ACCESS TOKEN!!");
            User userEntity = userRepository.findByUserEid(jwtProvider.getUserEid(jwtToken));
            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            //JWT 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            System.out.println(authentication);
            // 강제로 시큐리티의 세션에 접근하여 Authentication 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request,response);

        } else if (jwtProvider.validateRefreshToken(jwtToken)) {
            //refresh라면
            System.out.println("REFRESH TOKEN!!");
            String accessToken = jwtProvider.reCreateAccessToken(jwtToken);
            JWToken token = JWToken.builder().grantType("Bearer ").accessToken(accessToken).refreshToken(jwtToken).build();
            String json = new ObjectMapper().writeValueAsString(token);
            System.out.println("json");
            System.out.println(json);
            response.getWriter().write(json);
        } else {
            return;
        }

        // 서명이 정상적으로 됨
        System.out.println("tok"+jwtToken);

    }
}