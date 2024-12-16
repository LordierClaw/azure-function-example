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
import me.lordierclaw.azfe.model.CommonResponse;

import java.util.Optional;

public class WelcomeFunction {
    @FunctionName("WelcomePublic")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS
            ) HttpRequestMessage<Optional<String>> request, final ExecutionContext context) {
        context.getLogger().info("WelcomeFunction is triggered");

        String query = request.getQueryParameters().get("name");

        String body = "Welcome";
        if (query != null && !query.trim().isEmpty()) {
            body += ", " + query;
        }
        return request.createResponseBuilder(HttpStatus.OK)
                .body(new Gson().toJson(new CommonResponse(0, body)))
                .build();
    }
}
