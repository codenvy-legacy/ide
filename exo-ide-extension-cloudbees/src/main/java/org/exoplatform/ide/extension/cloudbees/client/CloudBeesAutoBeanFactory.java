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
package org.exoplatform.ide.extension.cloudbees.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;
import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount;
import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesUser;
import org.exoplatform.ide.extension.cloudbees.shared.Credentials;

/**
 * The interface for the AutoBean generator.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: CloudBeesAutoBeanFactory.java Mar 15, 2012 10:38:48 AM azatsarynnyy $
 * 
 */
public interface CloudBeesAutoBeanFactory extends AutoBeanFactory
{
   /**
    * A factory method for an application info bean.
    * 
    * @return an {@link AutoBean} of type {@link ApplicationInfo}
    */
   AutoBean<ApplicationInfo> applicationInfo();

   /**
    * A factory method for a credentials bean.
    * 
    * @return an {@link AutoBean} of type {@link Credentials}
    */
   AutoBean<Credentials> credentials();

   /**
    * A factory method for a CloudBees account bean.
    * 
    * @return an {@link AutoBean} of type {@link CloudBeesAccount}
    */
   AutoBean<CloudBeesAccount> account();

   /**
    * A factory method for a CloudBees user bean.
    * 
    * @return an {@link AutoBean} of type {@link CloudBeesUser}
    */
   AutoBean<CloudBeesUser> user();
}
