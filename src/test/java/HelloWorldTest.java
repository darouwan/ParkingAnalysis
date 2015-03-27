/**
 * Created by Junfeng on 2015/3/26.
 */
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class HelloWorldTest {
    @Test
    public void testSayHello()
    {
        HelloWorld helloWorld = new HelloWorld();

        String result = helloWorld.sayHello();

        assertEquals( "Hello Maven", result );

        //assertEquals("1","0");
    }
}
