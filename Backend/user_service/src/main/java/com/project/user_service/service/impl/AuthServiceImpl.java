package com.project.user_service.service.impl;

import com.project.user_service.dto.LogInDto;
import com.project.user_service.dto.UserDto;
import com.project.user_service.entities.UserEntity;
import com.project.user_service.exception.ExceptionType.AuthenticationException;
import com.project.user_service.exception.ExceptionType.InvalidTokenException;
import com.project.user_service.exception.ExceptionType.ResourceNotFoundException;
import com.project.user_service.exception.ExceptionType.UserOperationException;
import com.project.user_service.repositories.UserRepository;
import com.project.user_service.security.JwtService;
import com.project.user_service.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SessionService sessionService;


    @Override
    @Transactional
    public String[] logInRequest(LogInDto logInDto) {
        logger.info("Attempting login for user: {}", logInDto.getEmail());
        try {
            UserEntity user1 = userRepository.findByEmail(logInDto.getEmail()).orElse(null);
            logger.debug("user get : {} ", user1.toString());
            if(user1 == null || !user1.isEmail_verified()){
                throw new UserOperationException("Invalid credential");
            }
            logger.info("authentication ");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(logInDto.getEmail(), logInDto.getPassword())
            );
            
            if (!authentication.isAuthenticated()) {
                logger.warn("Authentication failed for user: {}", logInDto.getEmail());
                throw new UserOperationException("Authentication failed for user: " + logInDto.getEmail());
            }
            
            UserEntity user = (UserEntity) authentication.getPrincipal();
            logger.debug("User authenticated successfully: {}", user.getEmail());
            
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            logger.debug("Generated access token: {}", accessToken);
            logger.debug("Generated refresh token: {}", refreshToken);
            sessionService.generateNewSession(user, refreshToken);
            logger.info("Successfully generated tokens for user: {}", user.getEmail());
            return new String[]{accessToken, refreshToken};
        } catch (Exception e) {
            logger.error("Error during login for user: {}", logInDto.getEmail(), e);
            throw e;
        }
    }


    @Override
    public String refreshToken(String refreshToken) {
        logger.debug("Refreshing token");
        try {
            String userId = jwtService.getUserIdFromToken(refreshToken);
            sessionService.validateSession(refreshToken);
            logger.debug("Extracted user ID from refresh token: {}", userId);
            
            UserEntity user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", userId);
                        return new ResourceNotFoundException("User not found with id: " + userId);
                    });
            
            logger.info("Successfully generated new access token for user ID: {}", userId);
            return jwtService.generateAccessToken(user);
        } catch (Exception e) {
            logger.error("Error refreshing token", e);
            throw e;
        }
    }

    /**
     * Logs out user by blacklisting their JWT token
     * @param request HTTP request containing the authorization header
     * @throws InvalidTokenException If token is missing or invalid
     */
    @Transactional
    @Override
    public void logout(HttpServletRequest request , HttpServletResponse response) {
        try {
            final String requestTokenHeader = request.getHeader("Authorization");

            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
                throw new InvalidTokenException("Authorization header is missing or invalid");
            }

//            String token = requestTokenHeader.split("Bearer ")[1];
            Cookie accessTokenCookie = new Cookie("refreshToken", null);
             response.addCookie(accessTokenCookie);
            SecurityContextHolder.clearContext();

            logger.info("User logged out successfully");

        } catch (InvalidTokenException e) {
            logger.error("Invalid token during logout", e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during logout", e);
            throw new AuthenticationException("Logout failed due to unexpected error");
        }
    }

    @Override
    public UserDto getUser(HttpServletRequest req) {
        String authorizedToken = req.getHeader("Authorization");

        if (authorizedToken == null || !authorizedToken.startsWith("Bearer ")) {
            throw new AuthenticationException("User not authenticate");
        }
        String token = authorizedToken.split("Bearer ")[1];

        String id = jwtService.getUserIdFromToken(token);

        UserEntity user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });;

        return modelMapper.map(user, UserDto.class);

    }
}
