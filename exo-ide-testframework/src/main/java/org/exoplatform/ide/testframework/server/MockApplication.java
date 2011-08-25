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
package org.exoplatform.ide.testframework.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.exoplatform.ide.testframework.server.cloudbees.CloudbeesExceptionMapper;
import org.exoplatform.ide.testframework.server.cloudbees.MockCloudbeesService;
import org.exoplatform.ide.testframework.server.git.MockGitRepoService;
import org.exoplatform.ide.testframework.server.heroku.HerokuExceptionMapper;
import org.exoplatform.ide.testframework.server.heroku.MockHerokuService;
import org.exoplatform.ide.testframework.server.jenkins.MockJenkinsService;
import org.exoplatform.ide.testframework.server.openshift.MockExpressService;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 6, 2011 11:45:58 AM anya $
 *
 */
public class MockApplication extends Application
{
   private Set<Class<?>> classes;
   private Set<Object> singletons;

   public MockApplication()
   {
      classes = new HashSet<Class<?>>(1);
      classes.add(MockHerokuService.class);
//      classes.add(MockExpressService.class);
//      classes.add(MockGitRepoService.class);
      classes.add(MockCloudbeesService.class);
      classes.add(MockJenkinsService.class);
      singletons = new HashSet<Object>(1);
      singletons.add(new HerokuExceptionMapper());
      singletons.add(new CloudbeesExceptionMapper());
   }

   /**
    * @see javax.ws.rs.core.Application#getClasses()
    */
   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   /**
    * @see javax.ws.rs.core.Application#getSingletons()
    */
   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
}
