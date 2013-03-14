/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.extension.cloudfoundry.client;

import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryServices;
import com.codenvy.ide.extension.cloudfoundry.shared.CreateApplicationRequest;
import com.codenvy.ide.extension.cloudfoundry.shared.Credentials;
import com.codenvy.ide.extension.cloudfoundry.shared.Framework;
import com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService;
import com.codenvy.ide.extension.cloudfoundry.shared.SystemInfo;
import com.codenvy.ide.extension.cloudfoundry.shared.SystemResources;
import com.codenvy.ide.extension.cloudfoundry.shared.SystemService;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * The interface for the AutoBean generator.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: CloudFoundryAutoBeanFactory.java Mar 16, 2012 12:27:47 PM azatsarynnyy $
 */
public interface CloudFoundryAutoBeanFactory extends AutoBeanFactory
{
   /**
    * A factory method for an application info bean.
    * 
    * @return an {@link AutoBean} of type {@link CloudFoundryApplication}
    */
   AutoBean<CloudFoundryApplication> cloudFoundryApplication();

   /**
    * A factory method for a system info bean.
    * 
    * @return an {@link AutoBean} of type {@link SystemInfo}
    */
   AutoBean<SystemInfo> systemInfo();

   /**
    * A factory method for a system resources bean.
    * 
    * @return an {@link AutoBean} of type {@link SystemResources}
    */
   AutoBean<SystemResources> systemResources();

   /**
    * A factory method for a system resources bean.
    * 
    * @return an {@link AutoBean} of type {@link Framework}
    */
   AutoBean<Framework> framework();

   /**
    * A factory method for a credentials bean.
    * 
    * @return an {@link AutoBean} of type {@link Credentials}
    */
   AutoBean<Credentials> credentials();

   /**
    * A factory method for a create application request bean.
    * 
    * @return an {@link AutoBean} of type {@link CreateApplicationRequest}
    */
   AutoBean<CreateApplicationRequest> createApplicationRequest();

   /**
    * A factory method for CloudFoundry services bean.
    * 
    * @return an {@link AutoBean} of type {@link CloudFoundryServices}
    */
   AutoBean<CloudFoundryServices> services();
   
   /**
    * A factory method for CloudFoundry system service bean.
    * 
    * @return an {@link AutoBean} of type {@link SystemService}
    */
   AutoBean<SystemService> systemService();

   /**
    * A factory method for CloudFoundry provisioned service bean.
    * 
    * @return an {@link AutoBean} of type {@link ProvisionedService}
    */
   AutoBean<ProvisionedService> provisionedService();
}