package com.aol.micro.server.module;

import static com.aol.micro.server.utility.UsefulStaticMethods.concat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestListener;

import lombok.AllArgsConstructor;
import lombok.experimental.Builder;
import lombok.experimental.Wither;

import org.pcollections.ConsPStack;
import org.pcollections.HashTreePSet;

import com.aol.micro.server.auto.discovery.CommonRestResource;
import com.aol.micro.server.servers.model.ServerData;
import com.aol.micro.server.utility.HashMapBuilder;


@Builder
@AllArgsConstructor
@Wither
public class ConfigurableModule implements Module{
	
	private final List<Class> restResourceClasses;
	private final List<Class> restAnnotationClasses;
	private final List<Class> defaultResources;
	private final List<ServletContextListener> listeners;
	private final List<ServletRequestListener> requestListeners;
	private final Map<String, Filter> filters;
	private final Map<String, Servlet> servlets;
	private final String jaxWsRsApplication;
	private final String providers;
	private final String context;
	private final Set<Class> springConfigurationClasses;
	private final Map<String,String> propertyOverrides;
	private final List<String> defaultJaxRsPackages;
	private final Consumer<WebServerProvider<?>> serverConfigManager;
	private final Consumer<JaxRsProvider<?>> resourceConfigManager;
	private final Map<String, Object> serverProperties;
	final boolean resetAll;
	
	
	public <T> ConfigurableModule withResourceConfigManager(Consumer<JaxRsProvider<T>> resourceConfigManager){
		return new ConfigurableModule(restResourceClasses,restAnnotationClasses, defaultResources,
				listeners, requestListeners,filters,servlets, jaxWsRsApplication,providers,
				context, springConfigurationClasses, propertyOverrides,defaultJaxRsPackages,serverConfigManager,
				(Consumer)resourceConfigManager,serverProperties, resetAll);
	}
	public <T> ConfigurableModule withServerConfigManager(Consumer<WebServerProvider<?>> serverConfigManager){
		return new ConfigurableModule(restResourceClasses,restAnnotationClasses, defaultResources,
				listeners, requestListeners,filters,servlets, jaxWsRsApplication,providers,
				context, springConfigurationClasses, propertyOverrides,defaultJaxRsPackages,
				(Consumer)serverConfigManager,resourceConfigManager, serverProperties, resetAll);
	}
	
	@Override
	public <T> Consumer<WebServerProvider<T>> getServerConfigManager(){
		if(serverConfigManager!=null)
			return (Consumer)serverConfigManager;
		
		return Module.super.getServerConfigManager();
	}
	@Override
	public <T> Consumer<JaxRsProvider<T>> getResourceConfigManager(){
		if(resourceConfigManager!=null)
			return (Consumer)resourceConfigManager;
		
		return Module.super.getResourceConfigManager();
	}
	
	@Override
	public List<String> getDefaultJaxRsPackages() {
		if(defaultJaxRsPackages!=null)
			return ConsPStack.from(concat(defaultJaxRsPackages,extract(()->Module.super.getDefaultJaxRsPackages())));
		
		return Module.super.getDefaultJaxRsPackages();
	}
	private <T> Collection<T> extract(Supplier<Collection<T>> s) {
		if(!resetAll)
			return s.get();
		return Arrays.asList();
	}
	private <K,V> Map<K,V> extractMap(Supplier<Map<K,V>> s) {
		if(!resetAll)
			return s.get();
		return HashMapBuilder.of();
	}
	@Override
	public List<Class> getRestResourceClasses() {
		if(restResourceClasses!=null)
			return  ConsPStack.from(concat(restResourceClasses, extract(() -> Collections.singletonList(CommonRestResource.class))));
		
		return Module.super.getRestResourceClasses();
	}
	
	@Override
	public List<Class> getRestAnnotationClasses() {
		if(restAnnotationClasses!=null)
			return  new ArrayList<>(concat(restAnnotationClasses, extract(() -> Module.super.getRestAnnotationClasses())));
		
		return Module.super.getRestAnnotationClasses();
	}
	
	@Override
	public List<Class> getDefaultResources() {
		if(this.defaultResources!=null){
			return ConsPStack.from(concat(this.defaultResources,extract(()->Module.super.getDefaultResources())));
		}
			
		return Module.super.getDefaultResources();
	}

	@Override
	public List<ServletContextListener> getListeners(ServerData data) {
		if(listeners!=null)
			return  ConsPStack.from(concat(this.listeners, extract(()->Module.super.getListeners(data))));
		
		return Module.super.getListeners(data);
	}

	@Override
	public List<ServletRequestListener> getRequestListeners(ServerData data) {
		if(requestListeners!=null)
			return  ConsPStack.from(concat(this.requestListeners,
					                                      extract(()->Module.super.getRequestListeners(data))));

		return Module.super.getRequestListeners(data);
	}

	@Override
	public Map<String, Filter> getFilters(ServerData data) {
		if(filters!=null)
			return  HashMapBuilder.from(filters).putAll(extractMap(()->Module.super.getFilters(data))).build();
			
		return Module.super.getFilters(data);
	}

	@Override
	public Map<String, Servlet> getServlets(ServerData data) {
		if(servlets!=null)
			return  HashMapBuilder.from(servlets).putAll(extractMap(()->Module.super.getServlets(data))).build();
			
		return Module.super.getServlets(data);
	}

	@Override
	public String getJaxWsRsApplication() {
		if(this.jaxWsRsApplication!=null)
			return jaxWsRsApplication;
		return Module.super.getJaxWsRsApplication();
	}

	@Override
	public String getProviders() {
		if(providers!=null)
			return providers;
		return Module.super.getProviders();
	}

	@Override
	public String getContext() {
		
		return context;
	}

	@Override
	public Set<Class> getSpringConfigurationClasses() {
		if(this.springConfigurationClasses!=null)
			return HashTreePSet.from(concat(this.springConfigurationClasses, extract(()->Module.super.getSpringConfigurationClasses())));
			
		return Module.super.getSpringConfigurationClasses();
	}

	@Override
	public Map<String, Object> getServerProperties() {	
		if(serverProperties != null) {
			return HashMapBuilder.from(serverProperties).putAll(extractMap(() -> Module.super.getServerProperties())).build();
		} else {
			return Module.super.getServerProperties();
		}
	}

	
}
