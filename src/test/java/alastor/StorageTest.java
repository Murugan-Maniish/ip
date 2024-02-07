package alastor;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class StorageTest {

    @Test
    public void load_fileExists() {
        Storage storage = new Storage("./data/tasks.txt");
        try {
            storage.load();
        } catch (AlastorException e) {
            fail();
        }
    }

    @Test
    public void load_fileDoesNotExist() {
        String filePath = "./data/test.txt";
        File file = new File(filePath);
        assertEquals(false, file.exists());
        Storage storage = new Storage("./data/test.txt");
        try {
            storage.load();
            assertEquals(true, file.exists());
        } catch (AlastorException e) {
            fail();
        } finally {
            file.delete();
        }
    }

    @Test
    public void load_directoryDoesNotExist() {
        String filePath = "./test_data/test.txt";
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        assertEquals(false, parentDir.exists());
        Storage storage = new Storage("test_data/test.txt");
        try {
            storage.load();
            assertEquals(true, parentDir.exists());
            assertEquals(true, file.exists());
        } catch (AlastorException e) {
            fail();
        } finally {
            file.delete();
            parentDir.delete();
        }
    }
}
