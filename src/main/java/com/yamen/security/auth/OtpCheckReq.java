package com.yamen.security.auth;


public class OtpCheckReq {
    String otp;
    String email;

    // getter and setter constructor

    public OtpCheckReq(String otp, String email) {
        this.otp = otp;
        this.email = email;
    }
    public String getOtp() {
        return otp;
    }
    public void setOtp(String otp) {
        this.otp = otp;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
