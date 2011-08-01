/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.paas.openshift.core;

import org.exoplatform.ide.core.AbstractTestModule;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenShift extends AbstractTestModule
{

   public final CreateDomain CREATE_DOMAIN = new CreateDomain();

   public final CreateApplication CREATE_APPLICATION = new CreateApplication();
   
   public final DeleteApplication DELETE_APPLICATION = new DeleteApplication();
   
   public final ApplicationInfo APPLICATION_INFO = new ApplicationInfo();
   
   public final UserInfo USER_INFO = new UserInfo();
   
   /**
    * Creates new OpenShift domain
    * 
    * @param domainName
    * @throws Exception
    */
   public void createDomain(String domainName) throws Exception {
      CREATE_DOMAIN.openCreateDomainWindow();
      CREATE_DOMAIN.typeDomainName(domainName);
      CREATE_DOMAIN.clickCreateButton();
      CREATE_DOMAIN.waitForCreateDomainWindowNotPresent();      
   }
   
   public void createApplication(String applicationName) throws Exception {
      CREATE_APPLICATION.openCreateApplicationWindow();
      CREATE_APPLICATION.typeApplicationName(applicationName);
      CREATE_APPLICATION.clickCreateButton();
      CREATE_APPLICATION.waitForCreateApplicationWindowNotPresent();      
   }
   
}
