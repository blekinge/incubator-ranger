
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ranger.authorization.yarn.authorizer;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.security.AccessType;
import org.apache.hadoop.yarn.security.PrivilegedEntity;
import org.apache.hadoop.yarn.security.YarnAuthorizationProvider;
import org.apache.ranger.plugin.classloader.RangerPluginClassLoader;



public class RangerYarnAuthorizer extends YarnAuthorizationProvider {
	private static final Log LOG  = LogFactory.getLog(RangerYarnAuthorizer.class);

	private static final String   RANGER_PLUGIN_TYPE                      = "yarn";
	private static final String   RANGER_YARN_AUTHORIZER_IMPL_CLASSNAME   = "org.apache.ranger.authorization.yarn.authorizer.RangerYarnAuthorizer";

	private YarnAuthorizationProvider 	 	yarnAuthorizationProviderImpl = null;
	private static RangerPluginClassLoader  rangerPluginClassLoader  	  = null;
	
	public RangerYarnAuthorizer() {
		if(LOG.isDebugEnabled()) {
			LOG.debug("==> RangerYarnAuthorizer.RangerYarnAuthorizer()");
		}

		this.init();

		if(LOG.isDebugEnabled()) {
			LOG.debug("<== RangerYarnAuthorizer.RangerYarnAuthorizer()");
		}
	}

	private void init(){
		if(LOG.isDebugEnabled()) {
			LOG.debug("==> RangerYarnAuthorizer.init()");
		}

		try {
			
			rangerPluginClassLoader = RangerPluginClassLoader.getInstance(RANGER_PLUGIN_TYPE, this.getClass());
			
			@SuppressWarnings("unchecked")
			Class<YarnAuthorizationProvider> cls = (Class<YarnAuthorizationProvider>) Class.forName(RANGER_YARN_AUTHORIZER_IMPL_CLASSNAME, true, rangerPluginClassLoader);

			activatePluginClassLoader();

			yarnAuthorizationProviderImpl = cls.newInstance();
		} catch (Exception e) {
			// check what need to be done
			LOG.error("Error Enabling RangerYarnPluing", e);
		} finally {
			deactivatePluginClassLoader();
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("<== RangerYarnAuthorizer.init()");
		}
	}

	@Override
	public void init(Configuration conf) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("==> RangerYarnAuthorizer.init()");
		}

		try {
			activatePluginClassLoader();

			yarnAuthorizationProviderImpl.init(conf);
		} finally {
			deactivatePluginClassLoader();
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("<== RangerYarnAuthorizer.start()");
		}
	}

	@Override
	public boolean checkPermission(AccessType accessType, PrivilegedEntity target, UserGroupInformation user) {
		
		boolean ret = false;
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("==> RangerYarnAuthorizer.checkPermission()");
		}

		try {
			activatePluginClassLoader();

			ret = yarnAuthorizationProviderImpl.checkPermission(accessType, target, user);
		} finally {
			deactivatePluginClassLoader();
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("<== RangerYarnAuthorizer.checkPermission()");
		}
		
		return ret;
	}

	@Override
	public void setPermission(PrivilegedEntity target,	Map<AccessType, AccessControlList> acls, UserGroupInformation ugi) {
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("==> RangerYarnAuthorizer.setPermission()");
		}

		try {
			activatePluginClassLoader();

			yarnAuthorizationProviderImpl.setPermission(target, acls, ugi);
		} finally {
			deactivatePluginClassLoader();
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("<== RangerYarnAuthorizer.setPermission()");
		}
	}

	@Override
	public void setAdmins(AccessControlList acls, UserGroupInformation ugi) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("==> RangerYarnAuthorizer.setAdmins()");
		}

		try {
			activatePluginClassLoader();

			yarnAuthorizationProviderImpl.setAdmins(acls, ugi);
		} finally {
			deactivatePluginClassLoader();
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("<== RangerYarnAuthorizer.setAdmins()");
		}
	}

	@Override
	public boolean isAdmin(UserGroupInformation ugi) {
		
		boolean ret = false;
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("==> RangerYarnAuthorizer.setAdmins()");
		}

		try {
			activatePluginClassLoader();

			ret = yarnAuthorizationProviderImpl.isAdmin(ugi);
		} finally {
			deactivatePluginClassLoader();
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("<== RangerYarnAuthorizer.setAdmins()");
		}
		
		return ret;
	}
	
	private void activatePluginClassLoader() {
		if(rangerPluginClassLoader != null) {
			rangerPluginClassLoader.activate();
		}
	}

	private void deactivatePluginClassLoader() {
		if(rangerPluginClassLoader != null) {
			rangerPluginClassLoader.deactivate();
		}
	}

	
}
