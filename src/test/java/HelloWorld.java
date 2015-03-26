/**
 * Created by Junfeng on 2015/3/26.
 */
public class HelloWorld {


    public String sayHello()
    {
        return "Hello Maven";
    }

    public static void main(String[] args)
    {
        System.out.print( new HelloWorld().sayHello() );
    }
}
