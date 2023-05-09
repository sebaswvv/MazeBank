package w.mazebank.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import w.mazebank.services.JwtService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {

        // get authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String email;

        // If the Authorization header is missing or doesn't start with "Bearer ", return
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // if request was made from /auth/** OR /h2, continue the filter chain
            if (request.getRequestURI().startsWith("/auth/") || request.getRequestURI().startsWith("/h2")){
                filterChain.doFilter(request, response);
                return;
            }

            respondUnauthorized(response);
            return;
        }

        try {
            // Extract the JWT from the Authorization header and get the email from the JWT
            jwt = authHeader.substring(7);
            email = jwtService.extractEmail(jwt);

            // If the JWT is valid, set the authentication context
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // update the security context with the new authentication token
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            // continue the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // If there was an error verifying the JWT, return that the jwt is invalid
            respondUnauthorized(response);
        }
    }

    private void respondUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"Unauthorized\"}");
    }
}