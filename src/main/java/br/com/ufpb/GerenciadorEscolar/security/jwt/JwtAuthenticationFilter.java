package br.com.ufpb.GerenciadorEscolar.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Filtro para autenticação via JWT.
     *
     * Este filtro é executado em todas as requisições HTTP para validar tokens JWT presentes
     * no cabeçalho "Authorization" e autenticar o usuário no contexto de segurança do Spring.
     *
     * @param request - Requisição HTTP recebida.
     * @param response - Resposta HTTP a ser enviada.
     * @param chain - Cadeia de filtros para processamento da requisição.
     * @throws ServletException - Se ocorrer um erro durante o processamento do filtro.
     * @throws IOException - Se ocorrer um erro de entrada/saída ao lidar com a requisição.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/auth/")) {
            logger.info("Requisição para o endpoint de autenticação, ignorando o filtro.");
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            try {
                String username = jwtUtil.extractUsername(jwt);
                logger.info("Token JWT encontrado para o usuário: {}", username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Usuário autenticado com sucesso: {}", username);
                }

            } catch (Exception e) {
                logger.error("Erro ao autenticar JWT: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido ou expirado!");
                response.getWriter().flush();
                return;
            }
        } else {
            logger.warn("Nenhum JWT encontrado no cabeçalho Authorization.");
        }

        chain.doFilter(request, response);
    }
}
