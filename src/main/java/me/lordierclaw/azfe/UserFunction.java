package me.lordierclaw.azfe;

import com.google.gson.Gson;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import me.lordierclaw.azfe.exception.UnauthorizedException;
import me.lordierclaw.azfe.model.CommonResponse;
import me.lordierclaw.azfe.model.User;
import me.lordierclaw.azfe.service.AuthService;
import me.lordierclaw.azfe.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserFunction {

    private final AuthService authService;

    private final UserService userService;

    public UserFunction() {
        this.authService = AuthService.getInstance();
        this.userService = UserService.getInstance();
    }

    @FunctionName("GetAllUser")
    public HttpResponseMessage getAllUser(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "user"
            ) HttpRequestMessage<Optional<String>> request, final ExecutionContext context) {
        context.getLogger().info("GetAllUser is triggered");

        User authUser;
        try {
            authUser = authService.validateToken(request);
        } catch (UnauthorizedException e) {
            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                    .body(new Gson().toJson(new CommonResponse(1, "Invalidated Token")))
                    .build();
        }
        context.getLogger().info("Validated: " + authUser.getId());

        List<User> userList = userService.findAll();

        return request.createResponseBuilder(HttpStatus.OK)
                .body(new Gson().toJson(new CommonResponse(0, userList)))
                .build();
    }

    @FunctionName("GetUserById")
    public HttpResponseMessage getUser(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "user/{id}"
            ) HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {
        context.getLogger().info("GetUserById is triggered");
        context.getLogger().info("Binding id: " + id);

        User authUser;
        try {
            authUser = authService.validateToken(request);
        } catch (UnauthorizedException e) {
            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                    .body(new Gson().toJson(new CommonResponse(1, "Invalidated Token")))
                    .build();
        }
        context.getLogger().info("Validated: " + authUser.getId());

        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .build();
        }
        return request.createResponseBuilder(HttpStatus.OK)
                .body(new Gson().toJson(new CommonResponse(0, userOptional.get())))
                .build();
    }

    @FunctionName("AddUser")
    public HttpResponseMessage addUser(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "user"
            ) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("AddUser is triggered");

        User authUser;
        try {
            authUser = authService.validateToken(request);
        } catch (UnauthorizedException e) {
            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED)
                    .body(new Gson().toJson(new CommonResponse(1, "Invalidated Token")))
                    .build();
        }
        context.getLogger().info("Validated: " + authUser.getId());

        User newUser = new Gson().fromJson(request.getBody().orElse(null), User.class);

        newUser = userService.add(newUser);

        return request.createResponseBuilder(HttpStatus.OK)
                .body(new Gson().toJson(new CommonResponse(0, newUser)))
                .build();
    }
}
