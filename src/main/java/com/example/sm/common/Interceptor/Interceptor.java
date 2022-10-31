package com.example.sm.common.Interceptor;

import com.example.sm.common.constant.MessageConstant;
import com.example.sm.common.decorator.DataResponse;
import com.example.sm.common.decorator.RequestSession;
import com.example.sm.common.decorator.Response;
import com.example.sm.common.enums.CustomHTTPHeaders;
import com.example.sm.common.enums.Role;
import com.example.sm.common.model.JWTUser;
import com.example.sm.common.service.RestAPIService;
import com.example.sm.common.utils.JwtTokenUtil;
import com.example.sm.common.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Component
@Slf4j
public class Interceptor extends HandlerInterceptorAdapter implements  HandlerInterceptor {

    @Value("${trs.defaults.timezone}")
    String timezoneValue;

    @Autowired
    RestAPIService restAPIService;

    @Autowired
    JwtTokenUtil tokenUtil;

    @Autowired
    RequestSession requestSession;

    @Autowired
    Response response1;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        /*logger.info("PathInfo : {} ",request.getPathInfo());
        logger.info("PathInfo : {} ",request.getPathTranslated());
        logger.info("PathInfo : {} ",request.getServletPath());
        logger.info("Headers : {} ",Collections.list(request.getHeaderNames()).stream().map(header-> header+":"+request.getHeader(header)+"\n").collect(Collectors.toSet()));
        logger.info("Get Context Path : {} ",request.getContextPath());*/
        // If This is Resource Request then always return true
        if (handler instanceof HttpRequestHandler) {
            return true;
        }
        /*String timezone = request.getHeader("timezone");
        if(StringUtils.isEmpty(timezone)){
            timezone = timezoneValue;
        }
        requestSession.setTimezone(timezone);*/
        HandlerMethod method = (HandlerMethod) handler;
        RequestMapping rm = method.getMethodAnnotation(RequestMapping.class);
        String jwtToken = request.getHeader(CustomHTTPHeaders.TOKEN.toString());
        // IF ANONYMOUS Role then Pass the role
        if (restAPIService.hasAccess(Collections.singletonList(Role.ANONYMOUS.toString()),method.getMethod().getName())) {
            try {
                if (jwtToken != null) {
                    JWTUser user = tokenUtil.getJwtUserFromToken(jwtToken);
                    if (user != null) {
                        requestSession.setJwtUser(user);
                    }
                }
            } catch (Exception ignored) {
            }
            return checkTokenIsExpired(jwtToken, response);
        }


        if (jwtToken == null) {
            log.error("Authentication not present in the request");
            Response errorResponse = response1.getResponse(HttpStatus.UNAUTHORIZED,
                    MessageConstant.AUTHORIZATION_IS_NOT_PRESENT_IN_REQUEST,
                    MessageConstant.AUTHORIZATION_IS_NOT_PRESENT_IN_REQUEST);
            // Token is required if api is not ANONYMOUS
            sendJSONResponse(errorResponse, response, HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        JWTUser user;
        try {
            user = tokenUtil.getJwtUserFromToken(jwtToken);
            log.info("user"+user);
            if (!restAPIService.hasAccess(user.getRole(),(method.getMethod().getName()))) {
                log.error("Role is not allowed");
                Response errorResponse = response1.getResponse(HttpStatus.FORBIDDEN,
                        MessageConstant.ROLE_NOT_ALLOWED, MessageConstant.ROLE_NOT_ALLOWED);
                // if Role is not allowed
                sendJSONResponse(errorResponse, response, HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }
        catch (Exception e) {
            log.error("Invalid Token Signature!! ");
            Response errorResponse = response1.getResponse(HttpStatus.UNAUTHORIZED,
                    MessageConstant.INVALID_TOKEN_SIGNATURE, MessageConstant.INVALID_TOKEN_SIGNATURE);
            // if Token is invalid or signature is invalid
            sendJSONResponse(errorResponse, response, HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        requestSession.setJwtUser(user);
        return checkTokenIsExpired(jwtToken, response);
    }

    private boolean checkTokenIsExpired(String jwtToken, HttpServletResponse response) throws IOException {
        if (!StringUtils.isEmpty(jwtToken)) {
            boolean expired;
            try {
                expired = tokenUtil.isTokenExpired(jwtToken);
            } catch (Exception e) {
                expired = true;
            }
            if (expired) {
                log.error("Token Expired!!");
                Response errorResponse = response1.getResponse(HttpStatus.UPGRADE_REQUIRED,
                        MessageConstant.TOKEN_EXPIRED, MessageConstant.TOKEN_EXPIRED);
                // if Token is invalid or signature is invalid
                sendJSONResponse(errorResponse, response, HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }
        return true;
    }

    /**
     * @param base64Credentials
     * @return
     */
    private String[] decodeAuthorizationHeader(String base64Credentials) {
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:apiKey:auth_type
        return credentials.split(":", 3);
    }

    private void sendJSONResponse(Response modal, HttpServletResponse response, int status) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(new DataResponse<>(null, modal)));
    }


}


