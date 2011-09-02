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
package org.exoplatform.ide.grid;

import com.thoughtworks.selenium.grid.configuration.EnvironmentConfiguration;
import com.thoughtworks.selenium.grid.hub.HubRegistry;
import com.thoughtworks.selenium.grid.hub.HubServer;

import org.everrest.http.client.HTTPConnection;
import org.everrest.http.client.HTTPResponse;
import org.everrest.http.client.ModuleException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Launcher for selenium Hub.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 11, 2011 10:56:20 AM anya $
 *
 */
public class HubLauncher
{
   public static final int HUB_PORT = 4400;

   public static final int TIMEOUT_HUB_CONNECTION = 40000;

   /**
    * @param args
    */
   public static void main(String[] args) throws Exception
   {
      HubRegistry.registry().gridConfiguration().getHub().setPort(HUB_PORT);
      List<EnvironmentConfiguration> envs = new ArrayList<EnvironmentConfiguration>();
      envs.add(new EnvironmentConfiguration("*chrome", "*chrome"));
      envs.add(new EnvironmentConfiguration("*googlechrome", "*googlechrome"));
      envs.add(new EnvironmentConfiguration("*firefox", "*firefox"));
      envs.add(new EnvironmentConfiguration("*iexploreproxy", "*iexploreproxy"));
      HubRegistry.registry().gridConfiguration().getHub()
         .setEnvironments(envs.toArray(new EnvironmentConfiguration[envs.size()]));

      Thread thread = new Thread()
      {
         public void run()
         {
            try
            {
               HubServer.main(new String[0]);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         };
      };
      thread.setDaemon(true);
      thread.start();
      
      waitForHubService();
      System.out.println("Selenium Hub has started at port " + HUB_PORT);
   }

   /**
    * Waits got Hub Service to start.
    * 
    * @return {@link Boolean} if <code>true</code> - Hub is ready
    * @throws InterruptedException
    * @throws IOException
    * @throws ModuleException
    */
   public static boolean waitForHubService() throws InterruptedException, IOException, ModuleException
   {
      long startTime = System.currentTimeMillis();
      while (true)
      {
         try
         {
            URL url = new URL("http://localhost:" + HUB_PORT + "/console");
            HTTPConnection connection = new HTTPConnection(url);
            HTTPResponse response = connection.Get(url.getFile());
            if (200 == response.getStatusCode())
            {
               return true;
            }
         }
         catch (Exception e)
         {
         }

         long time = System.currentTimeMillis() - startTime;
         if (time > TIMEOUT_HUB_CONNECTION)
         {
            System.err.println("Timeout connecting to Hub :" + "http://localhost:" + HUB_PORT + "/console");
            return false;
         }
         Thread.sleep(1000);
      }
   }
}
