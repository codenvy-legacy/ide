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
package org.exoplatform.ideall.client.model.discovery;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.ideall.client.model.discovery.event.EntryPointsReceivedEvent;
import org.exoplatform.ideall.client.model.discovery.event.EntryPointsReceivedHandler;
import org.exoplatform.ideall.client.model.discovery.marshal.EntryPoint;
import org.exoplatform.ideall.client.model.discovery.marshal.EntryPointList;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class GwtTestDiscoveryService extends GWTTestCase
{
   
   private HandlerManager eventbus;

   private final int DELAY_TEST = 5000;
   
   private static String TEST_URL;

   private static String TEST_URL_WRONG;
   
   private DiscoveryService discovertyService;
   
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventbus = new HandlerManager(null);
      discovertyService = new DiscoveryServiceImpl(eventbus, new EmptyLoader());
      TEST_URL = "http://" + Window.Location.getHost()
            + "/ideall/rest/private/services/discovery/entrypoints";
      TEST_URL_WRONG = "http://" + Window.Location.getHost()
      + "/ideall/rest/private/services/discovery/entrypoints/not-found";
   }
   
   @Override
   protected void gwtTearDown() throws Exception
   {
      super.gwtTearDown();
      eventbus = null;
      discovertyService = null;
      TEST_URL = null;
      TEST_URL_WRONG = null;
   }

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
    */
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ideall.IDEGwtTest";
   }
   
   public void testGetEntryPoints()
   {
      eventbus.addHandler(EntryPointsReceivedEvent.TYPE, new EntryPointsReceivedHandler()
      {
         final String entryPoint1 = "http://" + Window.Location.getHost() + "/ideall/rest/private/jcr/repository/production/";
         
         final String entryPoint2 = "http://" + Window.Location.getHost() + "/ideall/rest/private/jcr/repository/dev-monit/";
      
         public void onEntryPointsReceived(EntryPointsReceivedEvent event)
         {
            EntryPointList entryPointList = event.getEntryPointList();
            
            assertEquals(2, entryPointList.getEntryPoints().length());
            for (int i = 0; i < entryPointList.getEntryPoints().length(); i++)
            {
               EntryPoint entryPoint = entryPointList.getEntryPoints().get(i);
               assertEquals(Scheme.WEBDAV, entryPoint.getScheme());
               if (entryPoint.getHref().endsWith("production/"))
               {
                  assertEquals(entryPoint1, entryPoint.getHref());
               }
               else
               {
                  assertEquals(entryPoint2, entryPoint.getHref());
               }
            }
            finishTest();
         }
      });
      
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
      
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getErrorMessage());
            finishTest();
         }
      });
      
      discovertyService.getEntryPoints(TEST_URL);
      delayTestFinish(DELAY_TEST);
   }
   
   public void testGetEntryPointsFail()
   {
      eventbus.addHandler(EntryPointsReceivedEvent.TYPE, new EntryPointsReceivedHandler()
      {
         public void onEntryPointsReceived(EntryPointsReceivedEvent event)
         {
            fail("EntryPointsReceivedHandler handles EntryPointsReceivedEvent with incorrect URL");
            finishTest();
         }
      });
      
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
      
         public void onError(ExceptionThrownEvent event)
         {
            assertNotNull(event.getError());
            assertNotNull(event.getErrorMessage());
            finishTest();
         }
      });
      
      discovertyService.getEntryPoints(TEST_URL_WRONG);
      delayTestFinish(DELAY_TEST);
   }
   
}
