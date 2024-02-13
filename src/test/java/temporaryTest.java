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
    public void test() {
        assertTrue(true);
    }

    @Test
    public void repoExists() {
        JSONObject jsonObject = new JSONObject();
        JSONObject repository = new JSONObject();
        jsonObject.put("repository", repository);
        repository.put("clone_url", "https://test.se");
        jsonObject.put("branch", "testbranch");
        File clonedRepoFile = new File("src/main/resources/");

        //assertTrue(ContinuousIntegrationServer.cloneRepository(jsonObject, clonedRepoFile));
    }
    
    @Test
    public void okIfPush() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventType", "push");

        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
 
        System.setOut(new PrintStream(outContent));
        CIServer.handlePushEvent(jsonObject);
        assertEquals("Event is 'push' - Proceeding with CI job\n", outContent.toString());
    }

    @Test
    public void notOkIfNotPush() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventType", "pull");

        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
 
        System.setOut(new PrintStream(outContent));
        CIServer.handlePushEvent(jsonObject);
        assertEquals("Not performing CI job - Event is not 'push'\n", outContent.toString());
    }
    //payloden innehåller nyckelorden

    /*Status checkar: pending när du klonat ett repo innan du bygger
    //Failure/success när du byggt
    success skickas till GitHub*/

    @Test
    public void deletesAllFilesIfTheyExist(){
        File f = new File("testFile");

        CIServer.deleteDirectory(f);

        assertNull("Successfully deleted file", f);
    }
}
