package me.lordierclaw.azfe.service;

import com.microsoft.azure.functions.HttpRequestMessage;
import me.lordierclaw.azfe.exception.LoginException;
import me.lordierclaw.azfe.exception.UnauthorizedException;
import me.lordierclaw.azfe.model.LoginRequest;
import me.lordierclaw.azfe.model.User;
import me.lordierclaw.azfe.util.JwtUtil;
import me.lordierclaw.azfe.util.PasswordUtil;

import java.util.Optional;

public class AuthService {

    private static AuthService instance = null;

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService(UserService.getInstance());
        }
        return instance;
    }

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public User validateToken(HttpRequestMessage<Optional<String>> request) throws UnauthorizedException {
        String token = request.getHeaders()
                .getOrDefault("authorization", "Bearer ")
                .substring(7).trim();
        if (token.isEmpty()) {
           throw new UnauthorizedException();
        }

        String email = JwtUtil.extractEmail(token);

        if (email != null && !JwtUtil.isTokenExpired(token)) {
            return userService.findByEmail(email).orElseThrow(UnauthorizedException::new);
        }
        throw new UnauthorizedException();
    }

    public User validateLogin(LoginRequest loginRequest) throws LoginException {
        String encodedPassword = PasswordUtil.encodePassword(loginRequest.getPassword());
        return userService.findByEmailAndPassword(loginRequest.getEmail(), encodedPassword)
                .orElseThrow(LoginException::new);
    }
}
