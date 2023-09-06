package com.example.auction_server.exception;

import java.util.HashMap;
import java.util.Map;

public class ExceptionMessage {

    private static final Map<String, String> exceptionMessages = new HashMap<>();

    static {
        //Common
        exceptionMessages.put("COMMON_1", "매핑에 실패했습니다. 조회해서 확인해주세요.");
        exceptionMessages.put("COMMON_2", "User login required");
        exceptionMessages.put("COMMON_3", "권한 부족");
        exceptionMessages.put("COMMON_4", "삭제에 실패했습니다.");
        //User
        exceptionMessages.put("USER_1", "회원가입에 성공하지 못했습니다. 다시 시도해주세요.");
        exceptionMessages.put("USER_2", "중복된 ID 입니다. 다른 ID을 입력해주세요.");
        exceptionMessages.put("USER_3", "중복된 Email 입니다. 다른 Email을 입력해주세요.");
        exceptionMessages.put("USER_4", "ADMIN 으로 회원가입할 수 없습니다.");
        exceptionMessages.put("USER_5", "로그인에 실패 했습니다. 아이디 및 비밀번호 확인 후 재시도 해주세요.");
        exceptionMessages.put("USER_6", "해당 유저를 찾지 못했습니다. 재시도 해주세요.");
        exceptionMessages.put("USER_7", "이상 유저 입니다.");
        exceptionMessages.put("USER_8", "회원정보를 수정하지 못했습니다.");
        exceptionMessages.put("USER_9", "회원 삭제에 실패했습니다.");
        exceptionMessages.put("USER_10", "마지막 로그인 시간을 업데이트 하는데 실패하였습니다. 다시 로그인해주세요.");
        exceptionMessages.put("USER_11", "타입을 수정하지 못했습니다. 다시 인증해주세요.");
        exceptionMessages.put("USER_12", "로그아웃에 실패했습니다.");
        //Email
        exceptionMessages.put("EMAIL_1", "Email 전송에 실패했습니다.");
        exceptionMessages.put("EMAIL_2", "만료시간이 지났습니다.");
        //Category
        exceptionMessages.put("CATEGORY_1", "중복된 category 입니다.");
        exceptionMessages.put("CATEGORY_2", "category 등록 오류. 재시도 해주세요.");
        exceptionMessages.put("CATEGORY_3", "category 수정 오류. 재시도 해주세요.");
        exceptionMessages.put("CATEGORY_4", "해당 카테고리를 찾지 못했습니다.");
        exceptionMessages.put("CATEGORY_5", "해당 상품을 찾지 못했습니다.");
        exceptionMessages.put("CATEGORY_6", "category 금액을 잘못 입력하셨습니다.");
        //Product
        exceptionMessages.put("PRODUCT_1", "상품등록에 실패했습니다. 다시시도해주세요.");
        exceptionMessages.put("PRODUCT_2", "해당 상품은 경매가 시작되여 수정이 불가능합니다.");
        exceptionMessages.put("PRODUCT_3", "상품을 수정하지 못했습니다.");
        exceptionMessages.put("PRODUCT_4", "이상이 있는 유저입니다.(회원 삭제, 이상유저 제재 등의 이유로 삭제 불가)");
        exceptionMessages.put("PRODUCT_5", "경매 상태를 바꾸지 못했습니다.");
        exceptionMessages.put("PRODUCT_6", "권한이 없어 해당 상품을 수정하지 못합니다.");
        exceptionMessages.put("PRODUCT_7", "경매 시작시간을 잘못 입력하셨습니다. 다시 입력해주세요.");
        exceptionMessages.put("PRODUCT_8", "경매 시작가가 즉시구매가와 같거나 큽니다. 다시 입력해주세요.");
        //ProductImage
        exceptionMessages.put("PRODUCT_IMAGE_1", "이미지 등록에 실패 했습니다.");
        exceptionMessages.put("PRODUCT_IMAGE_2", "이미지 삭제에 실패했습니다.");
        //ProductComment
        exceptionMessages.put("PRODUCT_COMMENT_1", "댓글을 등록하지 못했습니다.");

    }

    public static String getExceptionMessage(String exceptionCode) {
        return exceptionMessages.getOrDefault(exceptionCode, "알 수 없는 예외입니다.");
    }
}
