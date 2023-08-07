package com.example.auction_server.util;

import com.example.auction_server.enums.UserType;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    private static final String LOGIN_USER = "LOGIN_MEMBER";
    private static final String LOGIN_ADMIN = "LOGIN_ADMIN";

    private SessionUtil() {
    }

    public static void setLoginSession(HttpSession session, Long id, UserType userType) {
        if(userType != UserType.ADMIN){
            session.setAttribute(LOGIN_USER, id);
        }
        else{
            session.setAttribute(LOGIN_ADMIN, id);
        }
        session.setAttribute("userType", userType);
    }

    public static Long getLoginId(HttpSession session) {
        Long id = (Long) session.getAttribute(LOGIN_USER);
        if (id == null) {
            return (Long) session.getAttribute(LOGIN_ADMIN);
        } else return id;
    }

    public static String getLoginUserType(HttpSession session) {
        return session.getAttribute("userType").toString();
    }


    public static void clear(HttpSession session){
        session.invalidate();
    }
}
