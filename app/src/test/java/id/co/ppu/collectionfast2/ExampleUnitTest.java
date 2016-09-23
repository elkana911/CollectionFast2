package id.co.ppu.collectionfast2;

import org.junit.Test;

import id.co.ppu.collectionfast2.pojo.MstSecUser;

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
        MstSecUser usr = new MstSecUser();

        String password = "elkana";
        if (usr == null || !usr.getUserPwd().equals(password)) {
            System.out.println("salah");

        } else {
            System.out.println("bener");
        }
    }
}