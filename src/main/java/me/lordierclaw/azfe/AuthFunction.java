package me.lordierclaw.azfe;

import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import me.lordierclaw.azfe.exception.LoginException;
import me.lordierclaw.azfe.model.CommonResponse;
import me.lordierclaw.azfe.model.LoginRequest;
import me.lordierclaw.azfe.model.User;
import me.lordierclaw.azfe.service.AuthService;
import me.lordierclaw.azfe.util.JwtUtil;

import java.util.HashMap;
import java.util.Optional;

public class AuthFunction {

    private final AuthService authService;

    public AuthFunction() {
        this.authService = AuthService.getInstance();
    }

    @FunctionName("Login")
    public HttpResponseMessage login(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS
            ) HttpRequestMessage<Optional<LoginRequest>> request, final ExecutionContext context) {
        context.getLogger().info("Login is triggered");

        User authUser = null;
        try {
            authUser = authService.validateLogin(request.getBody().orElse(new LoginRequest()));
        } catch (LoginException e) {
            return request.createResponseBuilder(HttpStatus.NOT_ACCEPTABLE)
                    .body(new Gson().toJson(new CommonResponse(2, "Login failed")))
                    .build();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("token", JwtUtil.generateToken(authUser));
        return request.createResponseBuilder(HttpStatus.OK)
                .body(new Gson().toJson(new CommonResponse(0, map)))
                .build();
    }
}
