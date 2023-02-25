package com.spammayo.spam.exception;

import lombok.Getter;

public enum ExceptionCode {

    //common
    UNAUTHORIZED(401, "Unauthorized"),
    ACCESS_FORBIDDEN(403, "Access forbidden"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTATION(501, "Not Implementation"),
    INVALID_VALUES(400, "Invalid Values"),

    //auth
    INVALID_EMAIL_AUTH_NUMBER(400, "Invalid email authNumber"),
    INVALID_EMAIL_AUTH(400, "Invalid email auth"),
    INVALID_REFRESH_TOKEN(400, "Invalid refresh token"),
    EXPIRED_JWT_TOKEN(421, "Expired jwt token"),
    EMAIL_AUTH_REQUIRED(403, "Email auth required"),

    //member
    INVALID_MEMBER_STATUS(400, "Invalid member status"),
    MAX_FILE_SIZE_2MB(400, "Max file size 2MB"),
    MEMBER_EXISTS(409, "Member exists"),
    MEMBER_NOT_FOUND(404, "Member not found"),
    INVALID_PASSWORD (400, "Invalid Password"),
    MEMBER_NOT_LOGIN(400, "Member not login"),
    ID_NOT_EXIST(404, "Id not exist"),
    MEMBER_NAME_EXISTS(409, "Member name exists"),

    //study
    BOARD_NOT_PATCHED(403, "Study not patched"),
    BOARD_NOT_FOUND(404, "Study Not Found"),
    BOARD_CHECK_EXISTS(409, "Study Check exists"),
    BOARD_EXISTS(409, "Study exists"),

    //offer
    OFFER_NOT_PATCHED(403, "Offer not patched"),
    OFFER_NOT_FOUND(404, "Offer Not Found"),
    OFFER_CHECK_EXISTS(409, "Offer Check exists"),
    OFFER_EXISTS(409, "Offer exists"),

    //comment
    COMMENT_NOT_PATCHED(403, "Comment not patched"),
    COMMENT_NOT_FOUND(404, "Comment Not Found"),
    COMMENT_CHECK_EXISTS(409, "Comment Check exists");



    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
