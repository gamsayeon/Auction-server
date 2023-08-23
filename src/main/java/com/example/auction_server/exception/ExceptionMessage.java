package com.example.auction_server.exception;

import java.util.HashMap;
import java.util.Map;

public class ExceptionMessage {

    private static final Map<String, String> exceptionMessages = new HashMap<>();

    static {
        //AddException
        exceptionMessages.put("ERR_1000", "회원가입에 성공하지 못했습니다. 다시 시도해주세요.");
        exceptionMessages.put("ERR_1001", "매핑에 실패했습니다. 조회해서 확인해주세요.");
        exceptionMessages.put("ERR_1002", "category 등록 오류. 재시도 해주세요.");
        exceptionMessages.put("ERR_1003", "category 수정 오류. 재시도 해주세요.");
        exceptionMessages.put("ERR_1004", "상품등록에 실패했습니다. 다시시도해주세요.");
        exceptionMessages.put("ERR_1005", "이미지 등록에 실패 했습니다.");
        //DuplicateException
        exceptionMessages.put("ERR_2000", "Id와 Email이 중복되었습니다.");
        exceptionMessages.put("ERR_2001", "중복된 ID 입니다. 다른 ID을 입력해주세요.");
        exceptionMessages.put("ERR_2002", "중복된 Email 입니다. 다른 Email을 입력해주세요.");
        exceptionMessages.put("ERR_2003", "중복된 category 입니다.");
        //UserAccessDeniedException
        exceptionMessages.put("ERR_3000", "ADMIN 으로 회원가입할 수 없습니다.");
        //NotMatchingException
        exceptionMessages.put("ERR_4000", "로그인에 실패 했습니다. 아이디 및 비밀번호 확인 후 재시도 해주세요.");
        exceptionMessages.put("ERR_4001", "해당 유저를 찾지 못했습니다. 재시도 해주세요.");
        exceptionMessages.put("ERR_4002", "회원 조회에 실패 했습니다. 재시도 해주세요.");
        exceptionMessages.put("ERR_4003", "해당 카테고리를 찾지 못했습니다.");
        exceptionMessages.put("ERR_4004", "해당 상품을 찾지 못했습니다.");
        //UpdateException
        exceptionMessages.put("ERR_5000", "회원정보를 수정하지 못했습니다.");
        exceptionMessages.put("ERR_5001", "회원 삭제에 실패했습니다.");
        exceptionMessages.put("ERR_5002", "마지막 로그인 시간을 업데이트 하는데 실패하였습니다. 다시 로그인해주세요.");
        exceptionMessages.put("ERR_5003", "타입을 수정하지 못했습니다. 다시 인증해주세요.");
        //LogoutFailedException
        exceptionMessages.put("ERR_6000", "로그아웃에 실패했습니다.");
        //EmailSendException
        exceptionMessages.put("ERR_7000", "Email 전송에 실패했습니다.");
        //CacheTTLOutException
        exceptionMessages.put("ERR_8000", "만료시간이 지났습니다.");
        //LoginRequiredException
        exceptionMessages.put("ERR_9000", "User login required");
        //UserAccessDeniedException
        exceptionMessages.put("ERR_9001", "권한 부족");
        //InputSettingException
        exceptionMessages.put("ERR_10000", "category 금액을 잘못 입력하셨습니다.");
        exceptionMessages.put("ERR_10001", "경매 시작시간을 잘못 입력하셨습니다. 다시 입력해주세요.");
        exceptionMessages.put("ERR_10002", "경매 시작가가 즉시구매가와 같거나 큽니다. 다시 입력해주세요.");
        //DeleteException
        exceptionMessages.put("ERR_11000", "삭제에 실패했습니다.");

    }

    public static String getExceptionMessage(String exceptionCode) {
        return exceptionMessages.getOrDefault(exceptionCode, "알 수 없는 예외입니다.");
    }
}
