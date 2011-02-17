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
package org.exoplatform.ide.client.model.discovery;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.ide.client.AbstractGwtTest;
import org.exoplatform.ide.client.Const;
import org.exoplatform.ide.client.framework.discovery.DiscoveryCallback;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class GwtTestDiscoveryService extends AbstractGwtTest
{
   
   private HandlerManager eventbus;

   private final int DELAY_TEST = 5000;
   
   private static String testUrl;

   private static String testUrlWrong;
   
   private DiscoveryService discovertyService;
   
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventbus = new HandlerManager(null);
      discovertyService = new DiscoveryServiceImpl(eventbus, new EmptyLoader(), Const.REST_SERVICE_CONTEXT);
      testUrl = "http://" + Window.Location.getHost()
            + "/ideall/rest/private/services/discovery/entrypoints";
      testUrlWrong = "http://" + Window.Location.getHost()
      + "/ideall/rest/private/services/discovery/entrypoints/not-found";
   }
   
   @Override
   protected void gwtTearDown() throws Exception
   {
      super.gwtTearDown();
      eventbus = null;
      discovertyService = null;
      testUrl = null;
      testUrlWrong = null;
   }

   public void testGetEntryPoints()
   {
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
      
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getErrorMessage());
            finishTest();
         }
      });
      
      discovertyService.getEntryPoints(testUrl, new DiscoveryCallback(eventbus)
      {
         
         public void onResponseReceived(Request request, Response response)
         {
            final String entryPoint1 = "http://" + Window.Location.getHost() + "/ideall/rest/private/jcr/repository/production/";
            
            final String entryPoint2 = "http://" + Window.Location.getHost() + "/ideall/rest/private/jcr/repository/dev-monit/";
            
            List<EntryPoint> entryPoints = this.getEntryPointList();
            
            assertEquals(2, entryPoints.size());
            for (EntryPoint entryPoint : entryPoints)
            {
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
      delayTestFinish(DELAY_TEST);
   }
   
   public void testGetEntryPointsFail()
   {
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
      
         public void onError(ExceptionThrownEvent event)
         {
            assertNotNull(event.getError());
            assertNotNull(event.getErrorMessage());
            finishTest();
         }
      });
      
      discovertyService.getEntryPoints(testUrlWrong, new DiscoveryCallback(eventbus)
      {
         public void onResponseReceived(Request request, Response response)
         {
            fail("EntryPointsReceivedHandler handles EntryPointsReceivedEvent with incorrect URL");
            finishTest();
         }
      });
      delayTestFinish(DELAY_TEST);
   }
   
}
