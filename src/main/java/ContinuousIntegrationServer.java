
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jetty.util.log.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.File;


/**
 * Skeleton of a ContinuousIntegrationServer which acts as webhook
 * See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler {
    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        //TODO: Implement if-check to only do code below(cloning and deleting) if the event is a push

        String jsonRequest = IOUtils.toString(request.getReader());
        JSONObject jsonObject = new JSONObject(jsonRequest);

        // Get clone url and branch name
        JSONObject repository = jsonObject.getJSONObject("repository");
        String cloneUrl = repository.getString("clone_url");
        String branchName = jsonObject.getString("ref").replace("refs/heads/", "");

        File localPath = new File("src/main/resources/");

        try {
            System.out.println("Cloning repository...");
            Git.cloneRepository()
                    .setURI(cloneUrl)
                    .setDirectory(localPath)
                    .setBranch(branchName)
                    .call();
            System.out.println("Repository cloned successfully");
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        //TODO: Compile directory using maven commands


        // delete repository after compilation
        try {
            // Delete the repository directory
            deleteDirectory(localPath);

            System.out.println("Repository deleted successfully!");
        } catch (Exception e) {
            // Handle deletion exception
            e.printStackTrace();
        }


        response.getWriter().println("CI job done");
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception {
        Server server = new Server(8013);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
