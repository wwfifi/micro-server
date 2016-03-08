package com.aol.micro.server.general.exception.mapper;

import java.util.HashSet;
import java.util.Set;

import com.aol.cyclops.data.collections.extensions.persistent.PSetX;
import com.aol.micro.server.Plugin;

/**
 * 
 * Collections of Spring configuration classes (Classes annotated with @Configuration)
 * that configure various useful pieces of functionality - such as property file loading,
 * datasources, scheduling etc
 * 
 * @author johnmcclean
 *
 */
public class ExceptionMapperPlugin implements Plugin{

	@Override
	public PSetX<String> jaxRsPackages() {
		return PSetX.of("com.aol.micro.server.general.exception.mapper");
		
	}

	@Override
	public PSetX<Class> springClasses() {
		return PSetX.of(MapOfExceptionsToErrorCodes.class);	
	}
	
	
	
	
	

}
