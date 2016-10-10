package id.co.ppu.collectionfast2;

import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<MyApplication> {

    MyApplication myApplication;

    public ApplicationTest() {
        super(MyApplication.class);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();

        myApplication = getApplication();
    }
}