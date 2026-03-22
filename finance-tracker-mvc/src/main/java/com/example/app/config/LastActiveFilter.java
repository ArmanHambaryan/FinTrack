package com.example.app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
https://github.com/ArmanHambaryan/FinTrack/pull/13/conflict?name=finance-tracker-mvc%252Fsrc%252Fmain%252Fjava%252Fcom%252Fexample%252Fapp%252Fconfig%252FLastActiveFilter.java&base_oid=d4a6a2524f9492ce5ecd9fc99a3544e515597261&head_oid=7c74f4a662b09e1142201bd95b7b04e6586c84b1import org.springframework.web.filter.OncePerRequestFilter;
import service.UserService;

import java.io.IOException;

@Component
public class LastActiveFilter extends OncePerRequestFilter {

    private final UserService userService;

    public LastActiveFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            userService.updateLastActive(authentication.getName());
        }

        filterChain.doFilter(request, response);
    }
}
}
