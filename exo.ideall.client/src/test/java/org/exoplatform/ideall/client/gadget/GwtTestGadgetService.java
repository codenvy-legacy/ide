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
package org.exoplatform.ideall.client.gadget;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.ideall.client.model.gadget.event.SecurityTokenRecievedEvent;
import org.exoplatform.ideall.client.model.gadget.event.SecurityTokenRecievedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class GwtTestGadgetService extends GWTTestCase
{

   private HandlerManager eventBus;

   private Loader loader = new EmptyLoader();

   private final int DELAY_TEST = 6000;

   private String url;

   private String wrongUrl;

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#gwtSetUp()
    */
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventBus = new HandlerManager(null);
      //  url = "http://" + Window.Location.getHost() + "/ideall/rest/private/registry/repository/exo:applications/IDEall";
      //  wrongUrl = "http://" + Window.Location.getHost() + "/ideall/private/registry/repository/exo:applications/IDEall";
   }

   public void testGetGadgetMetadata()
   {
     // new GadgetServiceImpl(eventBus, loader);
    //  GadgetService.getInstance().getGadgetMetadata(tokenResponse);
      
      
   }
   
   public void testGetSecurityToken()
   {
     /* String owner = "root";
      String viewer = "root";
      Long moduleId = 0L;
      String container = "default";
      String domain = null;

      String href = context.getActiveFile().getHref();
      href = href.replace(Configuration.getInstance().getContext(), Configuration.getInstance().getPublicContext());
*/
     // TokenRequest tokenRequest = new TokenRequest(URL.encode(href), owner, viewer, moduleId, container, domain);
    //  GadgetService.getInstance().getSecurityToken(tokenRequest);
      
      eventBus.addHandler(SecurityTokenRecievedEvent.TYPE, new SecurityTokenRecievedHandler(){

         public void onSecurityTokenRecieved(SecurityTokenRecievedEvent securityTokenRecievedEvent)
         {
            
         }
         
      });
      
   }

   public void testDeployGadget()
   {
   }

   public void testUndeployGadget()
   {
   
   }
   
   

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
    */
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ideall.IDEGwtTest";
   }

}
