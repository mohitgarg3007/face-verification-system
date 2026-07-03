package com.mohit.faceverification.dto;


public class VerifyResponse {

    private Long userId;
    private boolean verified;
    private double score;

    public VerifyResponse(Long userId,
                          boolean verified,
                          double score) {
        this.userId = userId;
        this.verified = verified;
        this.score = score;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isVerified() {
        return verified;
    }

    public double getScore() {
        return score;
    }
}