package com.rituraj.ecommerce.middleware;

import com.rituraj.ecommerce.service.admin.MainAdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private MainAdminService mainAdminService;

    public AdminInterceptor(MainAdminService mainAdminService) {
        this.mainAdminService = mainAdminService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Check if an admin already exists
        if (mainAdminService.adminExistsOrNot()) {
            // If admin exists and trying to access /register, redirect to login
            if (uri.contains("/admin/register")) {
                response.sendRedirect("/admin/login");
                return false;
            }
        } else {
            // If no admin exists, redirect any /admin/** access to /register
            if (!uri.contains("/admin/register")) {
                response.sendRedirect("/admin/register");
                return false;
            }
        }
        return true;
    }
}
