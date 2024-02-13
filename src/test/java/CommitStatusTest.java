import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommitStatusTest {

    @Mock
    CloseableHttpResponse response;

    private CommitStatus commitStatus;

    @BeforeEach
    void setUp() {
        response = mock(CloseableHttpResponse.class);
        commitStatus = new CommitStatus();
    }

    @Test
    void testSetCommitStatusToError() throws IOException {
        String url = "http://test";
        commitStatus.setCommitStatusToError(url);
        verify(response, times(0)).close();
    }

}
