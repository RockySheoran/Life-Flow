package com.project.user_service.controller;

import com.project.user_service.dto.*;
import com.project.user_service.service.ForgetAndResetPassService;
import com.project.user_service.service.impl.AuthServiceImpl;
import com.project.user_service.service.impl.UserServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;
//
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Controller handling authentication related endpoints including signup, login, verification,
 * password reset, and token refresh operations.
 */
@Slf4j
@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class authController {

    // Deployment environment (e.g., production, development)
    @Value("${deploy.env}")
    private String deployEnv;

    @Value("${backend.url}")
    private String backendUrl;

    // Service dependencies
    private final UserServiceImpl userService;
    private final AuthServiceImpl authService;
    private final AuthenticationManager authenticationManager;
    private final ForgetAndResetPassService forgetAndResetPassService;

    @GetMapping(path = "/demo")
    public ResponseEntity<Map<String, String>> demo() {
        return new ResponseEntity<>(new HashMap<>(Map.of("message", "gfkjg")), HttpStatus.OK);
     }

    /**
     * Handles user registration requests.
     *
     * @param signupDto The DTO containing user registration details
     * @return ResponseEntity containing the created user's DTO and HTTP status
     */
    @PostMapping(path="/signup")
    public ResponseEntity<ResponseStringDto> signUpRequest(@RequestBody @Valid SignupDto signupDto) {
        log.info("Received signup request for email: {}", signupDto.getEmail());
//        System.out.println("dsjkfsd");
       userService.signUpRequest(signupDto);
        log.debug("User registered successfully");
        String message = "User registered successfully";
        return new ResponseEntity<>(new ResponseStringDto(message), HttpStatus.CREATED);
    }
    /**
     * Handles user login requests and sets refresh token in HTTP-only cookie.
     *
     * @param logInDto The DTO containing login credentials
     * @param request  The HTTP servlet request
     * @param response The HTTP servlet response for setting cookies
     * @return ResponseEntity containing the access token
     */
    @PostMapping(path="/login")
    public ResponseEntity<LoginResponseDto> logInRequest(
            @RequestBody LogInDto logInDto, 
            HttpServletRequest request, 
            HttpServletResponse response) {
        log.debug("Login attempt for user: {}", logInDto.getEmail());
        
        // Authenticate and generate tokens
        String[] tokens = authService.logInRequest(logInDto);
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        // Set refresh token in HTTP-only cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        boolean isProduction = "production".equals(deployEnv);
        cookie.setSecure(isProduction);
        response.addCookie(cookie);
        
        log.info("User logged in successfully: {}", logInDto.getEmail());
        return new ResponseEntity<>(new LoginResponseDto(accessToken), HttpStatus.OK);

    }
    // Get Google OAuth URL (if needed)
    @GetMapping("/google")
    public void GoogleAuthLogin(HttpServletResponse response) throws IOException {
        String url = backendUrl + "/oauth2/authorization/google";
        response.sendRedirect(url);
    }
    /**
     * Verifies a user's email using the verification token.
     *
     * @param token The verification token sent to the user's email
     * @param email The email of the user to verify
     * @return ResponseEntity with success message
     */
    @GetMapping(path="/verify")
    public ResponseEntity<ResponseStringDto> verifyUser(
            @RequestParam String token, 
            @RequestParam String email) {
        log.info("Verification request for email: {}", email);
        userService.verifyUser(token, email);
        log.info("User verified successfully: {}", email);
        String message = "User verified successfully";
        return new ResponseEntity<>(new ResponseStringDto(message), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseStringDto> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request,response);

        String message = "logout successfully";
        return new ResponseEntity<>(new ResponseStringDto(message), HttpStatus.OK);
    }

    /**
     * Refreshes the access token using a valid refresh token from cookies.
     *
     * @param request The HTTP servlet request containing the refresh token cookie
     * @return ResponseEntity containing the new access token
     * @throws AuthenticationServiceException if refresh token is not found in cookies
     */
    @PostMapping(path = "/refresh-token")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request) {
        log.debug("Processing token refresh request",request.getCookies());
        
        // Extract refresh token from cookies
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found in cookies");
                    return new AuthenticationServiceException("Refresh token not found inside the Cookies");
                });
        log.debug("Refresh token extracted successfully",refreshToken);

        // Generate new access token
        String accessToken = authService.refreshToken(refreshToken);
        log.debug("Access token refreshed successfully");
        
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }


    @PostMapping("/password-update")
    public ResponseEntity<ResponseStringDto> passwordUpdateAfterGoogleLogin(@RequestBody @Valid passwordUpdateAfterGoogleLoginDto passwordUpdateAfterGoogleLoginDto) {
        log.info("Password update requested for email: {}", passwordUpdateAfterGoogleLoginDto.getEmail());
        userService.passwordUpdateAfterGoogleLogin(passwordUpdateAfterGoogleLoginDto);
        log.debug("Password updated successfully for email: {}", passwordUpdateAfterGoogleLoginDto.getEmail());
        String message = "password update successfully";
        return new ResponseEntity<>(new ResponseStringDto(message), HttpStatus.OK);
    }


    /**
     * Initiates the password reset process by sending a reset link to the user's email.
     *
     * @RequestBody email The email address of the user requesting password reset
     * @return ResponseEntity with success message
     */
    @PostMapping(path = "/forget-password")
    public ResponseEntity<ResponseStringDto> forgetPasswordRequest(@RequestBody ForgetPasswordRequestDto forgetPasswordRequestDto) {
        log.info("Password reset requested for email: {}", forgetPasswordRequestDto.getEmail());
        forgetAndResetPassService.forgetPasswordRequest(forgetPasswordRequestDto.getEmail());
        log.debug("Password reset email sent to: {}", forgetPasswordRequestDto.getEmail());

        String message = "Password reset email sent successfully";
        return new ResponseEntity<>(new ResponseStringDto(message), HttpStatus.OK);
    }

    /**
     * Handles the password reset request with a valid reset token.
     *
     * @param resetPasswordDto DTO containing the new password
     * @return ResponseEntity with success message
     */
    @PostMapping(path="/reset-password")
    public ResponseEntity<ResponseStringDto> resetPasswordRequest(
            @RequestBody ResetPasswordDto resetPasswordDto) {
        log.debug("Processing password reset request with token");
        forgetAndResetPassService.resetPasswordRequest(resetPasswordDto.getToken(), resetPasswordDto);
        log.info("Password reset successfully");

        String message = "Password reset successfully";
        return new ResponseEntity<>(new ResponseStringDto(message), HttpStatus.OK);
    }




}
