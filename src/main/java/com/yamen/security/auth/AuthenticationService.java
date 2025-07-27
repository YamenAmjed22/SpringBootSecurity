package com.yamen.security.auth;

import com.yamen.security.config.JwtService;
import com.yamen.security.Enum.Role;
import com.yamen.security.Entity.User;
import com.yamen.security.Repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;


    public String generateOtp() {
        Random random = new Random();
        int otp = random.nextInt(999999) + 100000;
        return String.valueOf(otp);
    }

    public String validateOtp(OtpCheckReq otpCheckReq) {

        User user = userRepository.findByEmail(otpCheckReq.email).orElseThrow(RuntimeException::new);
        if (otpCheckReq.otp.equals(user.getOtp())  ){
            user.setValidOtp(true);
            user.setOtp(null);
            userRepository.save(user);
            return "The otp is valid";
        }
        else {
            user.setValidOtp(false);
            userRepository.save(user);
            return "The otp is invalid";
        }
    }

    public ResponseEntity<String> register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail())!=null) {
            return new ResponseEntity<>("Email already in use", HttpStatus.CONFLICT);
        }
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .Otp(generateOtp())
                .validOtp(false)
                .build();
        userRepository.save(user);

        return new ResponseEntity<>("User created", HttpStatus.CREATED);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        User loginUser = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new RuntimeException("user not founded"));
        if (loginUser.getValidOtp()){
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );
            } catch (AuthenticationException ex) {
                return AuthenticationResponse.builder().token(null).message("invalid credentials").build();
            }
        }
        else {
            return AuthenticationResponse.builder().token(null).message("OTP Validation failed !").build();
        }

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateTokenFromUserDetailes(user);
        return AuthenticationResponse.builder().token(jwtToken).message("Authentication Successful ").build();

    }
}
