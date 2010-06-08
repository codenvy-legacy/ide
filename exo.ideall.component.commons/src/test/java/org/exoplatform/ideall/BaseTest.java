/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ideall;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.ProviderBinder;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

public abstract class BaseTest extends TestCase
{

   protected StandaloneContainer container;

   protected ProviderBinder providers;

   protected ResourceBinder binder;

   protected RequestHandlerImpl requestHandler;

   public void setUp() throws Exception
   {
      StandaloneContainer.setConfigurationPath("src/test/resources/conf/standalone/test-configuration.xml");
      container = StandaloneContainer.getInstance();
      binder = (ResourceBinder)container.getComponentInstanceOfType(ResourceBinder.class);
      requestHandler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      // reset providers to be sure it is clean
      ProviderBinder.setInstance(new ProviderBinder());
      providers = ProviderBinder.getInstance();
      //    System.out.println("##########################"+providers);
      ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, providers));
      binder.clear();
   }

   public void tearDown() throws Exception
   {
   }

   public boolean registry(Object resource) throws Exception
   {
      //    container.registerComponentInstance(resource);
      return binder.bind(resource);
   }

   public boolean registry(Class<?> resourceClass) throws Exception
   {
      //    container.registerComponentImplementation(resourceClass.getName(), resourceClass);
      return binder.bind(resourceClass);
   }

   public boolean unregistry(Object resource)
   {
      //    container.unregisterComponentByInstance(resource);
      return binder.unbind(resource.getClass());
   }

   public boolean unregistry(Class<?> resourceClass)
   {
      //    container.unregisterComponent(resourceClass.getName());
      return binder.unbind(resourceClass);
   }

}
