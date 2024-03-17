package com.bluewhaletech.Ourry.jwt;

import com.bluewhaletech.Ourry.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        /* 유효한 자격증명을 제공하지 않은 상태에서 접근 시 '401 Unauthorized' 반환 */
        Object attribute = request.getAttribute("exception");
        if(attribute != null) {
            String exception = attribute.toString();
            if(exception.isEmpty()) {
                setErrorResponse(response, ErrorCode.NOT_LOGGED_IN);
            } else if(exception.equals(ErrorCode.EMPTY_JWT.getCode())) {
                setErrorResponse(response, ErrorCode.EMPTY_JWT);
            } else if(exception.equals(ErrorCode.JWT_MALFORMED.getCode())) {
                setErrorResponse(response, ErrorCode.JWT_MALFORMED);
            } else if(exception.equals(ErrorCode.JWT_EXPIRED.getCode())) {
                setErrorResponse(response, ErrorCode.JWT_EXPIRED);
            } else if(exception.equals(ErrorCode.BAD_CREDENTIALS.getCode())) {
                setErrorResponse(response, ErrorCode.BAD_CREDENTIALS);
            } else if(exception.equals(ErrorCode.JWT_UNSUPPORTED.getCode())) {
                setErrorResponse(response, ErrorCode.JWT_UNSUPPORTED);
            } else if(exception.equals(ErrorCode.ILLEGAL_ARGUMENT.getCode())) {
                setErrorResponse(response, ErrorCode.ILLEGAL_ARGUMENT);
            }
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        JSONObject object = new JSONObject();
        object.put("code", errorCode.getCode());
        object.put("message", errorCode.getMessage());

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print(object);
    }
}