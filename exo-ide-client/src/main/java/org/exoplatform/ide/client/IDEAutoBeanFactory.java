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
package org.exoplatform.ide.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.client.framework.discovery.RestServicesList;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.vfs.shared.LockToken;

/**
 * The interface for the {@link AutoBean} generator.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: IDEAutoBeanFactory.java Mar 27, 2012 9:57:25 AM azatsarynnyy $
 *
 */
public interface IDEAutoBeanFactory extends AutoBeanFactory
{
   /**
    * A factory method for a REST-services list bean.
    * 
    * @return an {@link AutoBean} of type {@link RestServicesList}
    */
   AutoBean<RestServicesList> restServicesList();

   /**
    * A factory method for a lock token bean.
    * 
    * @return an {@link AutoBean} of type {@link LockToken}
    */
   AutoBean<LockToken> lockToken();

   /**
    * A factory method for a information about user bean.
    * 
    * @return an {@link AutoBean} of type {@link UserInfo}
    */
   AutoBean<UserInfo> userInfo();
}
