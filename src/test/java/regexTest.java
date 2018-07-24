import static org.junit.Assert.*;

import org.junit.Test;

public class regexTest {

	@Test
	public void test() {
		String test = "int i :5";
		String test1 = "((\\w+)(\\.))*(\\w+)";
		String test2 = "((([a-zA-z]+)([a-zA-z0-9])*)(<*|\\.)*)*((([a-zA-z]+)([a-zA-z0-9])*)(>*|\\.)*)*";
		String test3 = "((([a-zA-z]+)([a-zA-z0-9])*),)*(([a-zA-z]+)([a-zA-z0-9])*)";
		String test4 ="(([a-zA-z])+([a-zA-z0-9])*)\\s(([a-zA-z])+([a-zA-z0-9])*)\\s(=*\\s([a-zA-z0-9]|')*)";
		System.out.println(test.matches(test4));
	    
	}

}
