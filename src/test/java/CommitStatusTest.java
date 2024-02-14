import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommitStatusTest {

    @Mock
    CloseableHttpClient httpClient;

    @Mock
    CloseableHttpResponse response;

    @Mock
    StatusLine statusLine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); // Initialize mocks
    }

    @Test
    void testSetCommitStatusWhenRequestIsSuccessful() throws IOException {

        // Mocking behavior of response object
        when(statusLine.getStatusCode()).thenReturn(201);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any())).thenReturn(response);

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        CommitStatus status = new CommitStatus("1234", "http://", httpClient);
        status.sendCommitStatus("", "");

        System.setOut(System.out);

        String printedMessage = outputStreamCaptor.toString().trim();

        assertTrue(printedMessage.contains("201"));
    }

    @Test
    void testSetCommitStatusWhenRequestIsNotSuccessful() throws IOException {

        // Mocking behavior of response object
        when(statusLine.getStatusCode()).thenReturn(400);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any())).thenReturn(response);

        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        CommitStatus status = new CommitStatus("1234", "http://", httpClient);
        status.sendCommitStatus("", "");

        System.setOut(System.out);

        String printedMessage = outputStreamCaptor.toString().trim();

        assertTrue(printedMessage.contains("400"));
    }

}
