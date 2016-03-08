package com.aol.micro.server.log4j;

import com.aol.cyclops.data.collections.extensions.persistent.PSetX;
import com.aol.micro.server.Plugin;
import com.aol.micro.server.log4j.rest.Log4jLoggerResource;
import com.aol.micro.server.log4j.rest.Log4jRootLoggerResource;
import com.aol.micro.server.log4j.service.Log4jRootLoggerChecker;

/**
 * 
 * @author Ke Wang
 *
 */
public class Log4jPlugin implements Plugin {
	@Override
	public PSetX<Class> springClasses() {
		return PSetX.of(Log4jRootLoggerResource.class, Log4jLoggerResource.class, Log4jRootLoggerChecker.class);
	}

}
