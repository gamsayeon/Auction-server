package com.example.auction_server.aop;

import com.example.auction_server.enums.UserType;
import com.example.auction_server.exception.LoginRequiredException;
import com.example.auction_server.exception.UserAccessDeniedException;
import com.example.auction_server.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SessionCheckAspect {
    private final HttpServletRequest request;

    public SessionCheckAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Around("@annotation(com.example.auction_server.aop.LoginCheck) && @annotation(loginCheck)")
    public Object checkUserLogin(ProceedingJoinPoint joinPoint, LoginCheck loginCheck) throws Throwable {
        // 세션 객체 가져오기
        HttpSession session = request.getSession();

        // 세션에서 값을 읽어옴
        Long id = SessionUtil.getLoginId(session);

        // 값이 없으면 로그인이 필요한 예외를 던짐
        if (id == null) {
            throw new LoginRequiredException("User login required");
        }

        boolean isPresent = false;
        String loginType = SessionUtil.getLoginUserType(session);
        for (int i = 0; i < loginCheck.types().length; i++) {
            switch (loginCheck.types()[i].toString()) {
                case "LOGIN_SELLER":
                    if (loginType == UserType.SELLER.name())
                        isPresent = true;
                    break;
                case "LOGIN_BUYER":
                    if (loginType == UserType.BUYER.name())
                        break;
                case "LOGIN_ADMIN":
                    if (loginType == UserType.ADMIN.name())
                        isPresent = true;
                    break;
                default:
                    if (loginType == UserType.WAITING_EMAIL.name())
                        isPresent = true;
                    break;
            }
        }
        if (isPresent == false) {
            throw new UserAccessDeniedException("권한 부족");
        }
        Object[] args = joinPoint.getArgs();
        args[0] = id;
        // 값이 있으면 해당 ID 값을 리턴
        return joinPoint.proceed(args);
    }

}
