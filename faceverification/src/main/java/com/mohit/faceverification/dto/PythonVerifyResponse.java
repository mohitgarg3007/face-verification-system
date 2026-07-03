package com.mohit.faceverification.dto;

public class PythonVerifyResponse {

    private boolean verified;
    private double score;

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}