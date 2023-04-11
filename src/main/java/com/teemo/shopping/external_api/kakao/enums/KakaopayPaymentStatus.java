package com.teemo.shopping.external_api.kakao.enums;
public enum KakaopayPaymentStatus {
    READY("결제 요청"),
    SEND_TMS("결제 요청 메시지(TMS) 발송 완료"),
    OPEN_PAYMENT("사용자가 카카오페이 결제 화면 진입"),
    SELECT_METHOD("결제 수단 선택, 인증 완료"),
    ARS_WAITING("ARS 인증 진행 중"),
    AUTH_PASSWORD("비밀번호 인증 완료"),
    ISSUED_SID("SID 발급 완료"),
    SUCCESS_PAYMENT("결제 완료"),
    PART_CANCEL_PAYMENT("부분 취소"),
    CANCEL_PAYMENT("결제된 금액 모두 취소"),
    FAIL_AUTH_PASSWORD("사용자 비밀번호 인증 실패"),
    QUIT_PAYMENT("사용자가 결제 중단"),
    FAIL_PAYMENT("결제 승인 실패");

    private final String description;
    KakaopayPaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}