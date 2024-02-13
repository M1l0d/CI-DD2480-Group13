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

import java.io.IOException;

public class CommitStatus {

    String owner = "M1l0d";
    String repo = "CI-DD2480-Group13";
    String BASEURL = "https://api.github.com/repos/";
    String sha;
    String targetUrl;

    public CommitStatus(String sha, String targetUrl) {
        this.sha = sha;
        this.targetUrl = targetUrl;
    }

    /**
     * Send pending commit status to github
     */
    public void setCommitStatusToPending() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String token = "";

        String url = BASEURL + owner + "/" + repo + "/statuses/" + sha;

        String description = "Commit validation is in progress.";
        String context = "CI server : Maven Build";

        String jsonBody = "{\"state\": \"" + "pending" + "\", \"target_url\": \"" + targetUrl
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
            System.out.println(response.getStatusLine()); // Optional: Print response for debugging
            HttpEntity entity = response.getEntity();
            // Consume response body to release connection
            EntityUtils.consume(entity);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send success commit status to github
     */
    public void setCommitStatusToSuccess() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String token = "";

        String url = BASEURL + owner + "/" + repo + "/statuses/" + sha;

        String description = "Build Successful: All tests passed.";
        String context = "CI server : Maven Build";

        String jsonBody = "{\"state\": \"" + "success" + "\", \"target_url\": \"" + targetUrl
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
            System.out.println(response.getStatusLine()); // Optional: Print response for debugging
            HttpEntity entity = response.getEntity();
            // Consume response body to release connection
            EntityUtils.consume(entity);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send failure commit status to github
     */
    public void setCommitStatusToFailure() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String token = "";

        String url = BASEURL + owner + "/" + repo + "/statuses/" + sha;

        String description = "Failure: Tests did not pass.";
        String context = "CI server : Maven Build";

        String jsonBody = "{\"state\": \"" + "failure" + "\", \"target_url\": \"" + targetUrl
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
            System.out.println(response.getStatusLine()); // Optional: Print response for debugging
            HttpEntity entity = response.getEntity();
            // Consume response body to release connection
            EntityUtils.consume(entity);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send error commit status to github
     */
    public void setCommitStatusToError() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String token = "";

        String url = BASEURL + owner + "/" + repo + "/statuses/" + sha;

        String description = "Error: Failed to build/test/verify the commit.";
        String context = "CI server : Maven Build";

        String jsonBody = "{\"state\": \"" + "error" + "\", \"target_url\": \"" + targetUrl
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
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 201) {
                System.out.println("Error commit status created successfully");
            } else {
                System.out.println("Error commit status creation failed : " + statusCode);
            }
            HttpEntity entity = response.getEntity();
            // Consume response body to release connection
            EntityUtils.consume(entity);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return current date and time in this format "yyyy/MM/dd HH:mm:ss"
     * 
     * @return current date and time
     */
    public String getDateandTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

}
