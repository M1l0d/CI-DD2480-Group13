import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class CommitStatus {

    private String owner = "M1l0d";
    private String repo = "CI-DD2480-Group13";
    private String BASEURL = "https://api.github.com/repos/";
    private String sha;
    private String targetUrl;
    private CloseableHttpClient httpClient;
    private String token;

    public CommitStatus(String sha, String targetUrl) {
        this.sha = sha;
        this.targetUrl = targetUrl;
        this.token = "";
        this.httpClient = HttpClients.createDefault();
    }

    public CommitStatus(String sha, String targetUrl, CloseableHttpClient httpClient) {
        this.sha = sha;
        this.targetUrl = targetUrl;
        this.token = "";
        this.httpClient = httpClient;
    }

    public void sendCommitStatus(String state, String description) {
        String url = BASEURL + owner + "/" + repo + "/statuses/" + sha;

        String context = "CI server : Maven Build";

        String jsonBody = "{\"state\": \"" + state + "\", \"target_url\": \"" + targetUrl
                + "\", \"description\": \""
                + description + "\", \"context\": \"" + context + "\"}";

        HttpPost request = new HttpPost(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "token " + token);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");
        request.setHeader("X-GitHub-Api-Version", getDateandTime());
        request.setEntity(new StringEntity(jsonBody, "UTF-8"));

        try {
            CloseableHttpResponse response = httpClient.execute(request);
            System.out.println(response.getStatusLine().getStatusCode()); // Optional: Print response for debugging
            HttpEntity entity = response.getEntity();
            // Consume response body to release connection
            EntityUtils.consume(entity);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCommitStatusToPending() {
        sendCommitStatus("pending", "Commit validation is in progress.");
    }

    public void setCommitStatusToSuccess() {
        sendCommitStatus("success", "Build Successful: All tests passed.");
    }

    public void setCommitStatusToFailure() {
        sendCommitStatus("failure", "Failure: Tests did not pass.");
    }

    public void setCommitStatusToError() {
        sendCommitStatus("error", "Error: Failed to build/test/verify the commit.");
    }

    private String getDateandTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}