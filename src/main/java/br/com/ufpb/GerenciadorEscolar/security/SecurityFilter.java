package br.com.ufpb.GerenciadorEscolar.security;

import br.com.ufpb.GerenciadorEscolar.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import br.com.ufpb.GerenciadorEscolar.security.TokenProvider; // Correct import

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityFilter(TokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        // Se a URL iniciar com /auth, pula a autenticação JWT
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer "
            try {
                String email = tokenProvider.getSubjectByToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                logger.error("Erro na autenticação do token: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }


}

