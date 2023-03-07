package com.doghotel.reservation.domain.role;

public enum Roles {

    CUSTOMER("CUSTOMER"),
    COMPANY("COMPANY"),
    ANONYMOUS("ANONYMOUS"),
    ADMIN("ADMIN");

    private String role;

    Roles(String role) {
        this.role = role;
    }
}
