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

import java.util.Optional;
import java.util.Random;

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

    public ResponseEntity<RegisterResponse> register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ResponseEntity<>(new RegisterResponse("Email already in use"), HttpStatus.CREATED);

        }
        if (!request.getConfirmPassword().equals(request.getPassword())) {
            return new ResponseEntity<>(new RegisterResponse("Passwords do not match"), HttpStatus.CREATED);

        }
        else {
            var user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .confirmPassword(passwordEncoder.encode(request.getConfirmPassword()))
                    .role(Role.USER)
                    .Otp(generateOtp())
                    .validOtp(true)
                    .build();

            userRepository.save(user);

            return new ResponseEntity<>(new RegisterResponse("User created"), HttpStatus.CREATED);

        }
    }

    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .token(null)
                    .message("User not found")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // Or HttpStatus.CONFLICT if you prefer
        }

        User loginUser = optionalUser.get();

        if (loginUser.getValidOtp()) {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );
            } catch (AuthenticationException ex) {
                AuthenticationResponse response = AuthenticationResponse.builder()
                        .token(null)
                        .message("Invalid credentials")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
        } else {
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .token(null)
                    .message("You need to check OTP")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        // Successful authentication
        var jwtToken = jwtService.generateTokenFromUserDetailes(loginUser);
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Authentication Successful")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
