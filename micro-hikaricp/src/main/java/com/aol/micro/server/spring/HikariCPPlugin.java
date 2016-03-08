package com.aol.micro.server.spring;

import com.aol.cyclops.data.collections.extensions.persistent.PSetX;
import com.aol.micro.server.Plugin;
import com.aol.micro.server.spring.datasource.HikariCPConfig;
import com.aol.micro.server.spring.datasource.HikariCPDataSourceBuilder;

/**
 * 
 * @author kewang
 *
 */
public class HikariCPPlugin implements Plugin {

	@Override
	public PSetX<Class> springClasses() {
		return PSetX.of(HikariCPConfig.class, HikariCPDataSourceBuilder.class);
	}

	

}
