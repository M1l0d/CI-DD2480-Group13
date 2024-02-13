import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.json.JSONObject;

public class temporaryTest {
    private ContinuousIntegrationServer CIServer;

    @Before
    public void setUp(){
        CIServer = new ContinuousIntegrationServer();
    }

    @Test
    public void testThatItClonesRepositorySuccessfully() {
        JSONObject jsonObject = new JSONObject();
        JSONObject repository = new JSONObject();

        repository.put("clone_url", "https://github.com/M1l0d/CI-DD2480-Group13");
        jsonObject.put("repository", repository);
        jsonObject.put("ref", "refs/heads/competent");
        File clonedRepoFile = new File("src/main/resources/");

        CIServer.cloneRepository(jsonObject, clonedRepoFile);

        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        assertEquals("Repository cloned successfully\n", outContent.toString());
    }

    //payloden innehåller nyckelorden

    /*Status checkar: pending när du klonat ett repo innan du bygger
    //Failure/success när du byggt
    success skickas till GitHub*/

    @Test
    public void deletesAllFilesIfTheyExist(){
        JSONObject jsonObject = new JSONObject();
        JSONObject repository = new JSONObject();

        repository.put("clone_url", "https://github.com/M1l0d/CI-DD2480-Group13");
        jsonObject.put("repository", repository);
        jsonObject.put("ref", "refs/heads/competent");
        File clonedRepoFile = new File("src/main/resources/");

        CIServer.cloneRepository(jsonObject, clonedRepoFile);

        CIServer.deleteDirectory(clonedRepoFile);

        assertEquals("Successfully deleted file", 4096, clonedRepoFile.length());
    }
}
