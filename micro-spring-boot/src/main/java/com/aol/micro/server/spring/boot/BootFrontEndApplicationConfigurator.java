package com.aol.micro.server.spring.boot;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestListener;

import org.pcollections.PStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import com.aol.cyclops.data.collections.extensions.persistent.PStackX;
import com.aol.micro.server.config.Config;
import com.aol.micro.server.module.Environment;
import com.aol.micro.server.module.Module;
import com.aol.micro.server.module.ModuleDataExtractor;
import com.aol.micro.server.servers.FilterConfigurer;
import com.aol.micro.server.servers.ServletConfigurer;
import com.aol.micro.server.servers.ServletContextListenerConfigurer;
import com.aol.micro.server.servers.model.FilterData;
import com.aol.micro.server.servers.model.ServerData;
import com.aol.micro.server.servers.model.ServletData;
import com.aol.micro.server.spring.SpringBuilder;

public class BootFrontEndApplicationConfigurator  extends SpringBootServletInitializer  implements SpringBuilder {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ConfigurableApplicationContext createSpringApp(Config config, Class...classes)  {
		
		
		List<Class> classList = new ArrayList<Class>();
		classList.addAll(Arrays.asList(classes));
		classList.add(JerseySpringBootFrontEndApplication.class);
		classList.add(MyWebAppInitializer.class);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(classList.toArray(new Class[0]));
		
		new JerseySpringBootFrontEndApplication(classList).config(builder);
		

		return builder.application().run();
	}
	
	@Component
	static class MyWebAppInitializer implements ServletContextInitializer{
	
		private final Environment environment;
		private final Module module;
		private final ApplicationContext rootContext;
		@Autowired(required=false)
		public MyWebAppInitializer(Environment env,ApplicationContext rootContext,Module m){
			this.environment = env;
			this.rootContext = rootContext;
			this.module = m;
		}
		@Autowired(required=false)
		public MyWebAppInitializer(Environment env,ApplicationContext rootContext){
			this(env,rootContext,()->"");
		}
		
		@Override
		public void onStartup(ServletContext webappContext) throws ServletException {
			
			ModuleDataExtractor extractor = new ModuleDataExtractor(module);
			environment.assureModule(module);
			String fullRestResource = "/" + module.getContext() + "/*";

			ServerData serverData = new ServerData(environment.getModuleBean(module).getPort(), 
					Arrays.asList(),
					rootContext, fullRestResource, module);
			List<FilterData> filterDataList = extractor.createFilteredDataList(serverData);
			List<ServletData> servletDataList = extractor.createServletDataList(serverData);
			new ServletConfigurer(serverData, PStackX.fromIterable(servletDataList)).addServlets(webappContext);

			new FilterConfigurer(serverData, PStackX.fromIterable(filterDataList)).addFilters(webappContext);
			PStack<ServletContextListener> servletContextListenerData = PStackX.fromCollection(module.getListeners(serverData)).filter(i->!(i instanceof ContextLoader));
		    PStack<ServletRequestListener> servletRequestListenerData =	PStackX.fromCollection(module.getRequestListeners(serverData));
			
			new ServletContextListenerConfigurer(serverData, servletContextListenerData, servletRequestListenerData).addListeners(webappContext);
			
		}
		
	}
	

}
