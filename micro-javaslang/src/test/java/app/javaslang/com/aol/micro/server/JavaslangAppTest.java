package app.javaslang.com.aol.micro.server;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.aol.cyclops.control.SimpleReact;
import com.aol.cyclops.types.futurestream.SimpleReactStream;
import com.aol.micro.server.MicroserverApp;
import com.aol.micro.server.config.Microserver;
import com.aol.micro.server.rest.jackson.JacksonUtil;
import com.aol.micro.server.testing.RestAgent;

import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Set;
import javaslang.control.Option;

@Microserver(basePackages = { "app.guava.com.aol.micro.server" })
public class JavaslangAppTest {

	RestAgent rest = new RestAgent();

	MicroserverApp server;

	ImmutableJavaslangEntity entity;
	JavaslangEntity present;
	JavaslangEntity absent;

	SimpleReact simpleReact = new SimpleReact();
	SimpleReactStream stream;

	@Before
	public void startServer() {
		stream = simpleReact.ofAsync(
				() -> server = new MicroserverApp(JavaslangAppTest.class,
						() -> "guava-app")).then(server -> server.start());
		
		
		entity = ImmutableJavaslangEntity.builder().value("value")
				.list(List.of("hello", "world"))
				.mapOfSets(HashMap.<String,Set>empty().put("key1",HashSet.ofAll(Arrays.asList(1, 2, 3))))
				.build();

		JacksonUtil.convertFromJson(JacksonUtil.serializeToJson(entity),
				ImmutableJavaslangEntity.class);

		present = JavaslangEntity.builder().name(Option.of("test")).build();

		
		JacksonUtil.convertFromJson(JacksonUtil.serializeToJson(present),
				Option.class);
		absent = JavaslangEntity.builder().name(Option.none()).build();
	}

	@After
	public void stopServer() {
		server.stop();
	}

	@Test @Ignore //option not supported yet
	public void confirmExpectedUrlsPresentTest() throws InterruptedException,
			ExecutionException {

		stream.block();
		
		assertThat((List<String>) rest.post(
				"http://localhost:8080/guava-app/status/ping", entity,
				List.class), hasItem("hello"));

	}

	@Test @Ignore //option not supported yet
	public void confirmOptionalConversionWorking() throws InterruptedException,
			ExecutionException {

		stream.block();

		assertThat(rest.post("http://localhost:8080/guava-app/status/optional",
				present, String.class), is("\"test\""));

		assertThat(rest.post("http://localhost:8080/guava-app/status/optional",
				absent, String.class), is("null"));

	}

}
