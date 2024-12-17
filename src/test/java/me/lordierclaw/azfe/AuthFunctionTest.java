package me.lordierclaw.azfe;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import me.lordierclaw.azfe.exception.LoginException;
import me.lordierclaw.azfe.model.HttpResponseMessageMock;
import me.lordierclaw.azfe.model.LoginRequest;
import me.lordierclaw.azfe.model.User;
import me.lordierclaw.azfe.service.AuthService;
import me.lordierclaw.azfe.util.JwtUtil;
import me.lordierclaw.azfe.util.MockUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthFunctionTest {
    private AutoCloseable mocks;

    @Mock
    private HttpRequestMessage<Optional<LoginRequest>> req;
    @Mock
    private ExecutionContext context;
    @Mock
    private AuthService authService;

    private AuthFunction function;

    private User mockUser;

    private MockedStatic<JwtUtil> jwtUtil;

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

        // Mock service
        mockUser = new User();
        mockUser.setId(UUID.randomUUID().toString());
        mockUser.setEmail("mock-user@mock.com");
        mockUser.setName("mock-user");

        // Mock static
        jwtUtil = Mockito.mockStatic(JwtUtil.class);

        // Initialize function
        function = new AuthFunction();
        MockUtil.setPrivateField(function, "authService", authService);
    }

    @AfterEach
    public void close() throws Exception {
        mocks.close();
        jwtUtil.close();
    }

    @Test
    public void testLogin() throws LoginException {
        doReturn(mockUser).when(authService).validateLogin(any());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(mockUser.getEmail());

        doReturn(Optional.of(loginRequest)).when(req).getBody();

        when(JwtUtil.generateToken(any())).thenReturn("test_token");

        // Invoke
        final HttpResponseMessage ret = function.login(req, context);

        // Verify
        verify(authService).validateLogin(any());
        assertEquals(HttpStatus.OK, ret.getStatus());
        assertFalse(ret.getBody().toString().isEmpty());
    }

    @Test
    public void testLoginFailed() throws LoginException {
        doThrow(LoginException.class).when(authService).validateLogin(any());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(mockUser.getEmail());

        doReturn(Optional.of(loginRequest)).when(req).getBody();

        // Invoke
        final HttpResponseMessage ret = function.login(req, context);

        // Verify
        verify(authService).validateLogin(any());
        jwtUtil.verifyNoInteractions();

        assertEquals(HttpStatus.NOT_ACCEPTABLE, ret.getStatus());
        assertFalse(ret.getBody().toString().isEmpty());
    }
}
