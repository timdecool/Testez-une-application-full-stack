package com.openclassrooms.starterjwt.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthEntryPointJwtTest {

    @InjectMocks
    AuthEntryPointJwt authEntryPointJwt;

    @Test
    @DisplayName("commence: should return 401 with error body")
    void commence_shouldReturn401WithErrorBody() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        when(request.getServletPath()).thenReturn("/api/session");
        when(authException.getMessage()).thenReturn("Unauthorized");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) { outputStream.write(b); }
            @Override
            public boolean isReady() { return true; }
            @Override
            public void setWriteListener(WriteListener writeListener) {}
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        authEntryPointJwt.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = mapper.readValue(
                outputStream.toString(),
                new TypeReference<Map<String, Object>>() {}
        );
        assertThat(body.get("status")).isEqualTo(401);
        assertThat(body.get("error")).isEqualTo("Unauthorized");
        assertThat(body.get("message")).isEqualTo("Unauthorized");
        assertThat(body.get("path")).isEqualTo("/api/session");
    }
}