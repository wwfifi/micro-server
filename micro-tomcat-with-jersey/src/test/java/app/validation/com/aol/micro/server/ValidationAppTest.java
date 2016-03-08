package app.validation.com.aol.micro.server;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.BadRequestException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aol.cyclops.control.SimpleReact;
import com.aol.cyclops.types.futurestream.SimpleReactStream;
import com.aol.micro.server.MicroserverApp;
import com.aol.micro.server.testing.RestAgent;

//@Microserver(basePackages = { "app.guava.com.aol.micro.server" })
public class ValidationAppTest {

	RestAgent rest = new RestAgent();

	MicroserverApp server;

	ImmutableEntity entity;
	

	SimpleReact simpleReact = new SimpleReact();
	SimpleReactStream stream;

	@Before
	public void startServer() throws InterruptedException {
		 server = new MicroserverApp(() -> "guava-app");
		 Thread.sleep(1000);
		 server.start();
		

		entity = ImmutableEntity.builder().value("value").build();

		
	}

	@After
	public void stopServer() {
		server.stop();
	}

	
	@Test(expected=BadRequestException.class)
	public void confirmError() throws InterruptedException,
			ExecutionException {

		
		//stream.block();
		
		rest.post(
				"http://localhost:8080/guava-app/status/ping", null,
				ImmutableEntity.class);
		

	}
	@Test
	public void confirmNoError() throws InterruptedException,
			ExecutionException {

		//stream.block();
		rest.post(
				"http://localhost:8080/guava-app/status/ping", entity,
				ImmutableEntity.class);
		

	}

	

}
