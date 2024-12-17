package me.lordierclaw.azfe;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import me.lordierclaw.azfe.model.HttpResponseMessageMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WelcomeFunctionTest {
    private AutoCloseable mocks;

    @Mock
    private HttpRequestMessage<Optional<String>> req;
    @Mock
    private ExecutionContext context;

    private WelcomeFunction function;

    @BeforeEach
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);

        // Mock HttpResponse builder
        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.Builder().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        // Mock ExecutionContext
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Initialize function
        function = new WelcomeFunction();
    }

    @AfterEach
    public void close() throws Exception {
        mocks.close();
    }

    @Test
    public void testWelcomeWithName() {
        // Mock request params
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", "HaiNN");
        doReturn(queryParams).when(req).getQueryParameters();
        // Mock request body
        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

        // Invoke
        final HttpResponseMessage ret = function.run(req, context);

        // Verify
        assertEquals(HttpStatus.OK, ret.getStatus());
        assertEquals("{\"code\":0,\"message\":\"Welcome, HaiNN\"}", ret.getBody());
    }

    @Test
    public void testWelcome() {
        // Mock request params
        final Map<String, String> queryParams = Map.of();
        doReturn(queryParams).when(req).getQueryParameters();
        // Mock request body
        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

        // Invoke
        final HttpResponseMessage ret = function.run(req, context);

        // Verify
        assertEquals(HttpStatus.OK, ret.getStatus());
        assertEquals("{\"code\":0,\"message\":\"Welcome\"}", ret.getBody());
    }
}
