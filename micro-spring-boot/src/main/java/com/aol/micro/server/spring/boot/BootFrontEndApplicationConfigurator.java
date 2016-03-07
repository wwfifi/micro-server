package com.aol.micro.server.spring.boot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import com.aol.micro.server.config.Config;
import com.aol.micro.server.spring.SpringBuilder;

public class BootFrontEndApplicationConfigurator  extends SpringBootServletInitializer  implements SpringBuilder {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ConfigurableApplicationContext createSpringApp(Config config, Class...classes)  {
		
		
		List<Class> classList = new ArrayList<Class>();
		classList.addAll(Arrays.asList(classes));
		classList.add(JerseySpringBootFrontEndApplication.class);
		System.out.println(classList);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(classList.toArray(new Class[0]));
		new JerseySpringBootFrontEndApplication(classList).config(builder);
		
		return builder.application().run();
	}
	

}
