/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.settings;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.ideall.client.AbstractGwtTest;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextReceivedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextReceivedHandler;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextSavedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextSavedHandler;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: $
 */
public class GwtTestSettingsService extends AbstractGwtTest
{
   
   private HandlerManager eventbus;

   private final int DELAY_TEST = 5000;
   
   private ApplicationContext context;
   
   private static String TEST_URL;

   private static String TEST_URL_WRONG;
   
   private SettingsService settingsServise;
   
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventbus = new HandlerManager(null);
      context = new ApplicationContext();
      settingsServise = new SettingsServiceImpl(eventbus, new EmptyLoader());
      TEST_URL = "http://" + Window.Location.getHost()
            + "/ideall/rest/private/registry/repository/exo:users/DefaultUser/IDEall";
      TEST_URL_WRONG = "http://" + Window.Location.getHost()
      + "/ideall/rest/private/registry/repository/not-found";
   }
   
   @Override
   protected void gwtTearDown() throws Exception
   {
      super.gwtTearDown();
      eventbus = null;
      context = null;
      settingsServise = null;
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
   
   public void testGetSettingsWhenNoSettingsOnServer()
   {
      eventbus.addHandler(ApplicationContextReceivedEvent.TYPE, new ApplicationContextReceivedHandler()
      {
      
         public void onApplicationContextReceived(ApplicationContextReceivedEvent event)
         {
            assertNotNull(event.getException());
            assertNotNull(event.getException().getMessage());
            assertEquals(1, context.getDefaultEditors().size());
            assertEquals(1, context.getToolBarItems().size());
            assertEquals(0, context.getHotKeys().size());
            finishTest();
         }
      });
      
      context.getDefaultEditors().put("text/plain", "Code Editor");
      
      settingsServise.getSettings(context, TEST_URL + "/?nocache=" + Random.nextInt());
      delayTestFinish(DELAY_TEST);
   }
   
   public void testSaveAndGetSettings()
   {
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            assertNotNull(event.getError());
            fail(event.getErrorMessage());
            finishTest();
         }
      });
      eventbus.addHandler(ApplicationContextSavedEvent.TYPE, new ApplicationContextSavedHandler()
      {

         public void onApplicationContextSaved(ApplicationContextSavedEvent event)
         {
            assertEquals(2, event.getContext().getHotKeys().size());
            assertEquals(3, event.getContext().getDefaultEditors().size());
            assertEquals(6, event.getContext().getToolBarItems().size());
            
            //clear context before getting settings from server
            context = new ApplicationContext();
            settingsServise.getSettings(context, TEST_URL + "/?nocache=" + Random.nextInt());
         }
      });
      
      eventbus.addHandler(ApplicationContextReceivedEvent.TYPE, new ApplicationContextReceivedHandler()
      {

         public void onApplicationContextReceived(ApplicationContextReceivedEvent event)
         {
            assertEquals(2, event.getContext().getHotKeys().size());
            assertEquals(3, event.getContext().getDefaultEditors().size());
            assertEquals(6, event.getContext().getToolBarItems().size());
            finishTest();
         }
      });
      
      initApplicationContext();
      settingsServise.saveSettings(context, TEST_URL + "/?createIfNotExist=true");
      
      delayTestFinish(DELAY_TEST);
   }
   
   public void testSaveSettingsFail()
   {
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            assertNotNull(event.getError());
            finishTest();
         }
      });
      eventbus.addHandler(ApplicationContextSavedEvent.TYPE, new ApplicationContextSavedHandler()
      {

         public void onApplicationContextSaved(ApplicationContextSavedEvent event)
         {
            fail("Test URL is right");
            finishTest();
         }
      });
      
      initApplicationContext();
      settingsServise.saveSettings(context, TEST_URL_WRONG + "/test");
      
      delayTestFinish(DELAY_TEST);
   }
   
   private void initApplicationContext()
   {
      Map<String, String> hotKeys = new HashMap<String, String>();
      hotKeys.put("Save", "Ctrl+65");
      hotKeys.put("New File", "Ctrl+75");
      context.setHotKeys(hotKeys);
      
      context.getDefaultEditors().put("text/html", "WYSWYG Editor");
      context.getDefaultEditors().put("text/plain", "Code Editor");
      context.getDefaultEditors().put("application/javascript", "Bespin Editor");
      
      context.getToolBarItems().add("");
      context.getToolBarItems().add("New File");
      context.getToolBarItems().add("Save");
      context.getToolBarItems().add("---");
      context.getToolBarItems().add("Create Folder");
      
   }
   
}