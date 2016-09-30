package id.co.ppu.collectionfast2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void checkNull() throws Exception {

        String password = "0505";
        long penerimaan = Long.valueOf(password);

        System.out.println(password);
        System.out.println(penerimaan);
    }
}