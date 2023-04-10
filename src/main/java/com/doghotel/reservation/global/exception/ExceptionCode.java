package com.doghotel.reservation.global.exception;

import lombok.Getter;

public enum ExceptionCode {

    INVALID_EMAIL(404,"존재하지 않는 이메일"),
    DUPLICATE_EMAIL(404, "중복되는 이메일"),
    COMPANY_NOT_FOUND(404,"존재하지 않는 회사"),
    COMPANY_ID_NOT_MATCH(400, "회사 식별자가 다름"),
    CUSTOMER_NOT_FOUND(404, "존재하지 않는 사용자"),
    CUSTOMER_ID_NOT_MATCH(400, "사용자 식별자 다름"),
    DOG_NOT_FOUND(404,"존재하지 않는 강아지"),
    ONLY_EDIT_SELECT_DELETE_YOUR_PUPPY(400, "본인의 강아지만 수정/삭제 가능합니다."),
    POST_NOT_FOUND(404, "존재하지 않는 게시물"),
    ONLY_YOUR_OWN_POST_CAN_BE_EDITED(400, "본인의 게시물만 수정 가능"),
    ONLY_YOUR_COMPANYS_RESERVATION_CAN_BE_EDIT(400, "다른 회사의 예약은 건들 수 없습니다."),
    RESERVATION_NOT_FOUND(404,"존재하지 않는 예약"),
    IMAGE_COUNT_LIMIT_3(400, "이미지는 3장 이하로"),
    REVIEW_NOT_FOUND(404, "존재하지 않는 리뷰"),
    TAG_COUNT_LIMIT_10(404, "태그는 10개 이하로 작성해주세요");


    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
