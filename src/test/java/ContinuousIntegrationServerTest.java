import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

public class ContinuousIntegrationServerTest {
    private ContinuousIntegrationServer CIServer;
    BuildAttempt buildAttemptWithoutIdToNotBeSaved;

    @Before
    public void setUp() {
        CIServer = new ContinuousIntegrationServer();
        buildAttemptWithoutIdToNotBeSaved = new BuildAttempt();
    }

    @Test
    public void testThatItClonesRepositorySuccessfully() {
        JSONObject jsonObject = new JSONObject();
        JSONObject repository = new JSONObject();

        repository.put("clone_url", "https://github.com/M1l0d/CI-DD2480-Group13");
        jsonObject.put("repository", repository);
        jsonObject.put("ref", "refs/heads/competent");

        JSONObject head_commit = new JSONObject();
        head_commit.put("timestamp", "20240107");
        head_commit.put("id", "");
        jsonObject.put("head_commit", head_commit);
        JSONObject pusher = new JSONObject();
        pusher.put("name","steffe");
        jsonObject.put("pusher", pusher);

        File clonedRepoFile = new File("src/main/resources/clonedRepo");

        CIServer.cloneRepository(jsonObject, clonedRepoFile, buildAttemptWithoutIdToNotBeSaved);

        assertTrue("Repo is cloned successfully", clonedRepoFile.exists());
        CIServer.deleteDirectory(clonedRepoFile);

    }

    @Test
    public void testThatRepoIsDeletedSuccesfully() throws InterruptedException {
        JSONObject jsonObject = new JSONObject();
        JSONObject repository = new JSONObject();

        repository.put("clone_url", "https://github.com/M1l0d/CI-DD2480-Group13");
        jsonObject.put("repository", repository);
        jsonObject.put("ref", "refs/heads/competent");

        JSONObject head_commit = new JSONObject();
        head_commit.put("timestamp", "20240107");
        head_commit.put("id", "");
        jsonObject.put("head_commit", head_commit);
        JSONObject pusher = new JSONObject();
        pusher.put("name","steffe");
        jsonObject.put("pusher", pusher);

        File clonedRepoFile = new File("src/main/resources/clonedRepo");

        CIServer.cloneRepository(jsonObject, clonedRepoFile, buildAttemptWithoutIdToNotBeSaved);

        TimeUnit.SECONDS.sleep(5);

        CIServer.deleteDirectory(clonedRepoFile);

        assertFalse("Repo is deleted successfully", clonedRepoFile.exists());
    }

    @Test
    public void serverDoesNotSaveBuildHistoryToJsonIfIdIsEmptyString() throws InterruptedException, IOException {
        JSONObject jsonObject = new JSONObject();
        JSONObject repository = new JSONObject();

        repository.put("clone_url", "https://github.com/M1l0d/CI-DD2480-Group13");
        jsonObject.put("repository", repository);
        jsonObject.put("ref", "refs/heads/competent");

        JSONObject head_commit = new JSONObject();
        head_commit.put("timestamp", "20240107");
        head_commit.put("id", "");
        jsonObject.put("head_commit", head_commit);
        JSONObject pusher = new JSONObject();
        pusher.put("name","steffe");
        jsonObject.put("pusher", pusher);

        String clonedRepoPath = "src/main/resources/clonedRepo";
        File clonedRepoFile = new File(clonedRepoPath);

        Path jsonFilePath = Paths.get("src/main/resources/buildHistory.JSON");
        String existingJsonContent = new String(Files.readAllBytes(jsonFilePath));
        List<BuildAttempt> existingBuildAttempts = new Gson().fromJson(existingJsonContent, new TypeToken<List<BuildAttempt>>() {}.getType());

        double buildHistorySize = existingBuildAttempts.size();

        CIServer.cloneRepository(jsonObject, clonedRepoFile, buildAttemptWithoutIdToNotBeSaved);
        CIServer.compileRepository(clonedRepoPath, clonedRepoFile, buildAttemptWithoutIdToNotBeSaved);
        CIServer.deleteDirectory(clonedRepoFile);

        existingJsonContent = new String(Files.readAllBytes(jsonFilePath));
        existingBuildAttempts = new Gson().fromJson(existingJsonContent, new TypeToken<List<BuildAttempt>>() {}.getType());

        double buildHistorySizeAfterCloneCompileDelete = existingBuildAttempts.size();
        assertEquals("Commit with no id is saved to json", buildHistorySize, buildHistorySizeAfterCloneCompileDelete, 0);
    }
}
