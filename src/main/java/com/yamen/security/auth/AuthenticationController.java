package com.yamen.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// TODO : This is my task ==> for this weak end Yamen i hope you do this
//  i need to Make OTP for my Project To check from email
//  also i need to make uer name and email unique
//  and change the login methode to use otp check
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) throws Exception {
       return authenticationService.register(request) ;
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request) throws Exception {
        return authenticationService.authenticate(request);

    }

    @PostMapping("/otpcheack")
    public ResponseEntity<String> otpCheck(@RequestBody OtpCheckReq otpCheckReq) throws Exception {
        return  ResponseEntity.ok(authenticationService.validateOtp(otpCheckReq));

    }
}
