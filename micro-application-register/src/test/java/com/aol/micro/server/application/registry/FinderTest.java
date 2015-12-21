package com.aol.micro.server.application.registry;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class FinderTest {

	Register writer;
	RegisterEntry entry;
	Finder finder;
	RegisterConfig registerConfig;
	@Before
	public void setUp() throws Exception {
		try{
			new File(System.getProperty("java.io.tmpdir"),"service-reg-finder").delete();
		}catch(Exception e){
		}

		new File(System.getProperty("java.io.tmpdir"),"service-reg-finder").mkdirs();
		registerConfig = new RegisterConfig(new File(System.getProperty("java.io.tmpdir"),"service-reg-finder")
					.getAbsolutePath());
		writer = new Register(registerConfig);
		finder = new Finder(registerConfig);

		entry= new RegisterEntry(8080,"host","module","context",new Date(),null);
	}

	@Test
	public void testFind() {
		writer.register(entry);
		List<RegisterEntry> list = finder.find();
		assertThat(list.size(),greaterThan(0));
		assertThat(list.get(0).getContext(),equalTo("context"));
	}
}
