package ru.tokarev.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.tokarev.dto.ApiErrorDto;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final UserDetailsService userDetailsService;

    private final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {

            logger.debug(Arrays.toString(e.getStackTrace()));

            ApiErrorDto apiErrorDto = new ApiErrorDto(401, e.getMessage(), List.of("Unauthorized"),
                    request.getRequestURI(), new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

            response.getWriter().write(convertObjectToJson(apiErrorDto));

        } catch (MalformedJwtException e) {

            logger.debug(Arrays.toString(e.getStackTrace()));

            ApiErrorDto apiErrorDto = new ApiErrorDto(401, "Jwt token is not valid",
                    List.of("Unauthorized"), request.getRequestURI(),
                    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

            response.getWriter().write(convertObjectToJson(apiErrorDto));

        } catch (RuntimeException e) {

            logger.debug(Arrays.toString(e.getStackTrace()));

            ApiErrorDto apiErrorDto = new ApiErrorDto(500, e.getMessage(),
                    List.of("Internal Server Error"), request.getRequestURI(),
                    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

            response.getWriter().write(convertObjectToJson(apiErrorDto));
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper.writeValueAsString(object);
    }
}
