/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.netvibes.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.ide.extension.netvibes.server.service.NetvibesWidgetService;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 18, 2010 2:05:14 PM evgen $
 *
 */
public class TestNetvibesDeploy extends TestCase
{

   @Before
   public void setUp()
   {
      String containerConf = TestNetvibesDeploy.class.getResource("/conf/standalone/test-configuration.xml").toString();

      try
      {
         StandaloneContainer.addConfigurationURL(containerConf);
      }
      catch (MalformedURLException e)
      {
         fail();
         e.printStackTrace();
      }

   }

   @Test
   public void testDeploy()
   {
      NetvibesWidgetService service = new NetvibesWidgetService(null, null);
      File file = new File("src/test/java/org/exoplatform/ide/extension/netvibes/server/test.xml");
      StringBuilder content = new StringBuilder();
      try
      {
         BufferedReader input = new BufferedReader(new FileReader(file));
         try
         {
            String line = null;

            while ((line = input.readLine()) != null)
            {
               content.append(line);
               content.append('\n');
            }
         }
         finally
         {
            input.close();
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      System.out.println("TestNetvibesDeploy.testDeploy()" + content.toString());
      System.out.println(service.deployNetvibesWidget(content.toString(), "vitaly.parfonov@gmail.com", "1234qwer",
         "lofshagBicpoorelinha", "DoigFogIvtyejvavyinn"));
   }

}
