import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CompilerTest {
    @Before
    public void setup()
    {
        System.out.println( "before" );
    }
    @Test
    public void firstTest()
    {
        System.out.println( "test step1" );
    }

    @Test
    public void secondTest()
    {
        System.out.println("test step2");
        System.out.println();
    }
    @After
    public void tearDown()
    {
        System.out.println( "after" );
    }
}
