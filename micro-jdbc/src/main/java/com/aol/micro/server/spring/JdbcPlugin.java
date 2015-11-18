package com.aol.micro.server.spring;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.servlet.ServletContextListener;

import com.aol.micro.server.Plugin;
import com.aol.micro.server.servers.model.ServerData;
import com.aol.micro.server.spring.datasource.DataDataSourceBuilder;
import com.aol.micro.server.spring.datasource.JdbcConfig;
import com.aol.micro.server.spring.datasource.jdbc.SQL;

/**
 * 
 * Collections of Spring configuration classes (Classes annotated with @Configuration)
 * that configure various useful pieces of functionality - such as property file loading,
 * datasources, scheduling etc
 * 
 * @author johnmcclean
 *
 */
public class JdbcPlugin implements Plugin {

	@Override
	public Optional<SpringDBConfig> springDbConfigurer() {
		return Optional.of(new SpringConfigurer());
	}

	@Override
	public Set<Class> springClasses() {
		return new HashSet<>(Arrays.asList(JdbcConfig.class, DataDataSourceBuilder.class, SQL.class));
	}

	@Override
	public Set<Function<ServerData, ServletContextListener>> servletContextListeners() {
		return null;
	}

	@Override
	public Set<Class> jaxRsResources() {
		return null;
	}

	@Override
	public Set<String> jaxRsPackages() {
		return null;
	}

}
