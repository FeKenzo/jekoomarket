package com.jekoomarket.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        if (statusCode != null) {
            if (statusCode == 404) {
                return "404";  // → templates/404.html
            } else if (statusCode == 403) {
                return "access-refused"; // → templates/access-refused.html
            }
        }
        return "general"; // → templates/general.html
    }
}
