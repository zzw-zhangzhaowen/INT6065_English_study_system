package com.english.learning.aspect;

import com.english.learning.annotation.Log;
import com.english.learning.dto.LoginRequest;
import com.english.learning.dto.RegisterRequest;
import com.english.learning.entity.SysLog;
import com.english.learning.service.SysLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    private final SysLogService sysLogService;
    private final ObjectMapper objectMapper;
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    @Pointcut(value="@annotation(com.english.learning.annotation.Log)")
    public void logPointcut() {
    }

    @Before(value="logPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        START_TIME.set(System.currentTimeMillis());
    }

    @AfterReturning(pointcut="logPointcut()", returning="result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        this.handleLog(joinPoint, null);
    }

    @AfterThrowing(pointcut="logPointcut()", throwing="e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        this.handleLog(joinPoint, e);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void handleLog(JoinPoint joinPoint, Exception e) {
        try {
            Long startTime;
            ServletRequestAttributes attributes;
            String extracted;
            MethodSignature signature = (MethodSignature)joinPoint.getSignature();
            Method method = signature.getMethod();
            Log logAnnotation = method.getAnnotation(Log.class);
            if (logAnnotation == null) {
                return;
            }
            SysLog sysLog = new SysLog();
            String operation = logAnnotation.value();
            if (operation.isEmpty()) {
                operation = logAnnotation.type().getDescription();
            }
            sysLog.setOperation(operation);
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = method.getName();
            sysLog.setMethod(className + "." + methodName + "()");
            if (logAnnotation.saveParams()) {
                try {
                    Object[] args = joinPoint.getArgs();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    String[] paramNames = signature.getParameterNames();
                    for (int i = 0; i < args.length; ++i) {
                        if (args[i] == null || this.isFilteredParam(args[i])) continue;
                        params.put(paramNames[i], args[i]);
                    }
                    if (!params.isEmpty()) {
                        Object paramsJson = this.objectMapper.writeValueAsString(params);
                        if (((String)paramsJson).length() > 2000) {
                            paramsJson = ((String)paramsJson).substring(0, 2000) + "...";
                        }
                        sysLog.setParams((String)paramsJson);
                    }
                }
                catch (Exception ex) {
                    log.warn("\u5e8f\u5217\u5316\u8bf7\u6c42\u53c2\u6570\u5931\u8d25: {}",ex.getMessage());
                }
            }
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    String name = authentication.getName();
                    sysLog.setUsername(name);
                    Object details = authentication.getDetails();
                    if (details instanceof Long) {
                        sysLog.setUserId((Long)details);
                    }
                }
            }
            catch (Exception ex) {
                log.warn("\u83b7\u53d6\u7528\u6237\u4fe1\u606f\u5931\u8d25: {}",ex.getMessage());
            }
            if ((sysLog.getUsername() == null || "anonymousUser".equals(sysLog.getUsername())) && (extracted = this.extractUsernameFromArgs(joinPoint.getArgs())) != null && !extracted.isEmpty()) {
                sysLog.setUsername(extracted);
            }
            if ((attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes()) != null) {
                HttpServletRequest request = attributes.getRequest();
                sysLog.setIpAddress(this.getClientIp(request));
                sysLog.setBrowser(this.getBrowser(request));
                sysLog.setOs(this.getOs(request));
            }
            if ((startTime = (Long)START_TIME.get()) != null) {
                sysLog.setExecuteTime(Integer.valueOf((int)(System.currentTimeMillis() - startTime)));
            }
            if (e != null) {
                sysLog.setStatus(0);
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.length() > 2000) {
                    errorMsg = errorMsg.substring(0, 2000);
                }
                sysLog.setErrorMsg(errorMsg);
            } else {
                sysLog.setStatus(1);
            }
            sysLog.setCreateTime(LocalDateTime.now());
            this.sysLogService.saveLogAsync(sysLog);
        }
        catch (Exception ex) {
            log.error("\u8bb0\u5f55\u64cd\u4f5c\u65e5\u5fd7\u5931\u8d25", (Throwable)ex);
        }
        finally {
            START_TIME.remove();
        }
    }

    private String extractUsernameFromArgs(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof LoginRequest) {
                return ((LoginRequest)arg).getUsername();
            }
            if (arg instanceof RegisterRequest) {
                return ((RegisterRequest)arg).getUsername();
            }
            if (!(arg instanceof Map)) continue;
            Map<?, ?> map = (Map<?, ?>)arg;
            if (map.containsKey("email")) {
                return String.valueOf(map.get("email"));
            }
            if (!map.containsKey("username")) continue;
            return String.valueOf(map.get("username"));
        }
        return null;
    }

    private boolean isFilteredParam(Object arg) {
        return arg instanceof HttpServletRequest || arg instanceof Authentication || arg instanceof MultipartFile || arg instanceof byte[];
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        if (userAgent.contains("Edge")) {
            return "Edge";
        }
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        }
        if (userAgent.contains("Firefox")) {
            return "Firefox";
        }
        if (userAgent.contains("Safari")) {
            return "Safari";
        }
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "IE";
        }
        return "Other";
    }

    private String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        if (userAgent.contains("Windows")) {
            return "Windows";
        }
        if (userAgent.contains("Mac")) {
            return "MacOS";
        }
        if (userAgent.contains("Linux")) {
            return "Linux";
        }
        if (userAgent.contains("Android")) {
            return "Android";
        }
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        }
        return "Other";
    }

    public LogAspect(SysLogService sysLogService, ObjectMapper objectMapper) {
        this.sysLogService = sysLogService;
        this.objectMapper = objectMapper;
    }
}

