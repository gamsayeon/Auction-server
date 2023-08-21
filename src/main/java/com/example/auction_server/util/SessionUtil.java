package com.example.auction_server.util;

import com.example.auction_server.enums.UserType;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";

    private SessionUtil() {
    }

    public static void setLoginSession(HttpSession session, Long id, UserType userType) {
        if (userType != UserType.ADMIN) {
            session.setAttribute(USER, id);
        } else {
            session.setAttribute(ADMIN, id);
        }
        session.setAttribute("userType", userType);
    }

    public static Long getLoginId(HttpSession session) {
        Long id = (Long) session.getAttribute(USER);
        if (id == null) {
            return (Long) session.getAttribute(ADMIN);
        } else return id;
    }

    public static String getLoginUserType(HttpSession session) {
        return session.getAttribute("userType").toString();
    }


    public static void clear(HttpSession session) {
        session.invalidate();
    }
}
