import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

public class ContinuousIntegrationServerTest {
    private ContinuousIntegrationServer CIServer;

    @Before
    public void setUp() {
        CIServer = new ContinuousIntegrationServer();
    }

    @Test
    public void testThatItClonesRepositorySuccessfully() {
        JSONObject jsonObject = new JSONObject();
        JSONObject repository = new JSONObject();

        repository.put("clone_url", "https://github.com/M1l0d/CI-DD2480-Group13");
        jsonObject.put("repository", repository);
        jsonObject.put("ref", "refs/heads/competent");
        File clonedRepoFile = new File("src/main/resources/clonedRepo");

        CIServer.cloneRepository(jsonObject, clonedRepoFile);

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
        File clonedRepoFile = new File("src/main/resources/clonedRepo");

        CIServer.cloneRepository(jsonObject, clonedRepoFile);

        TimeUnit.SECONDS.sleep(5);

        CIServer.deleteDirectory(clonedRepoFile);

        assertFalse("Repo is deleted successfully", clonedRepoFile.exists());
    }
}
