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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import static org.junit.Assert.*;

import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemResources;
import org.junit.Test;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class AutoBeanGwtTest extends GWTTestCase
{

   interface MyFactory extends AutoBeanFactory
   {
      AutoBean<SystemInfo> systemInfo();
      AutoBean<SystemResources> systemResources();
   }

   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.extension.cloudfoundry.CloudFoundryExtension";
   }

   @Test
   public void testName() throws Exception
   {
      String json =
         "{\"limits\":null,\"support\":\"http://support.cloudfoundry.com\",\"description\":\"VMware's Cloud Application Platform\",\"name\":\"vcap\",\"usage\":null,\"user\":null,\"version\":\"0.999\"}";
      MyFactory myFactory = GWT.create(MyFactory.class);
      AutoBean<SystemInfo> bean = AutoBeanCodex.decode(myFactory, SystemInfo.class, json);
      SystemInfo info = bean.as();
      System.out.println(info.getClass().getName());
      assertEquals("http://support.cloudfoundry.com", info.getSupport());
   }
   
   public void testName2() throws Exception
   {
      MyFactory myFactory = GWT.create(MyFactory.class);
      SystemInfo info = myFactory.systemInfo().as();
//      ISystemInfo> bean = AutoBeanCodex.decode(myFactory, ISystemInfo.class, json);
//      ISystemInfo info = bean.as();
//      assertEquals("http://support.cloudfoundry.com", info.getSupport());
   }

}
