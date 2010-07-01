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

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ideall.client.AbstractGwtTest;
import org.exoplatform.ideall.client.TestResponse;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.settings.marshal.ApplicationContextMarshaller;
import org.exoplatform.ideall.client.model.settings.marshal.ApplicationContextUnmarshaller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: $
 */
public class GwtTestApplicationContextTransformer extends AbstractGwtTest
{
   
   private ApplicationContext context;
   
   private ApplicationContextMarshaller marshaller;
   
   private ApplicationContextUnmarshaller unmarshaller;
   
   private HandlerManager eventBus;
   
   private final static String SETTINGS_XML = 
      "<settings>"
         + "<toolbar>"
            + "<toolbar-item></toolbar-item>"
            + "<toolbar-item></toolbar-item>"
            + "<toolbar-item>New File</toolbar-item>"
            + "<toolbar-item>Save</toolbar-item>"
            + "<toolbar-item>---</toolbar-item>"
            + "<toolbar-item>Create Folder</toolbar-item>"
         + "</toolbar>"
         + "<editors>"
            + "<editor>"
               + "<mimetype>text/plain</mimetype>"
               + "<editordescription>Code Editor</editordescription>"
            + "</editor>"
         + "</editors>"
         + "<hot-keys>"
            + "<hot-key>"
               + "<shortcut>Save</shortcut>"
               + "<control-id>Ctrl+65</control-id>"
            + "</hot-key>"
            + "<hot-key>"
               + "<shortcut>New File</shortcut>"
               + "<control-id>Ctrl+75</control-id>"
            + "</hot-key>"
         + "</hot-keys>"
      + "</settings>";
   
   private final static String WRONG_XML = 
      "<settings>"
      + "<hot-keys>"
         + "<hot-key>"
            + "<shortcut>Save</shortcut>"
            + "<control-id>Ctrl+65</control-id>"
         + "</hot-key>"
         + "<hot-key>"
            + "<shortcut>New File</shortcut>"
            + "<control-id>Ctrl+75</control-id>"
         + "</hot-key>"
      + "</hot-keys>"
      + "<menu>"
         + "<menu-item>Save</menu-item>"
      + "</menu>"
   + "</settings>";
   
   private final static String EMPTY_XML = "<settings><toolbar><toolbar-item></toolbar-item>"
      + "</toolbar><editors></editors><hot-keys></hot-keys></settings>";
   
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventBus = new HandlerManager(null);
      context = new ApplicationContext();
   }
   
   @Override
   protected void gwtTearDown() throws Exception
   {
      super.gwtTearDown();
      context = null;
      marshaller = null;
      unmarshaller = null;
      eventBus = null;
   }

   public void testMarshaller()
   {
      initApplicationContext();
      marshaller = new ApplicationContextMarshaller(context);
      String xml = marshaller.marshal();
      assertEquals(SETTINGS_XML, xml);
   }
   
   public void testMarshallerWithEmptyData()
   {
      marshaller = new ApplicationContextMarshaller(context);
      String xml = marshaller.marshal();
      assertEquals(EMPTY_XML, xml);
   }
   
   public void testUnmarshaller()
   {
      unmarshaller = new ApplicationContextUnmarshaller(eventBus, context);
      Header[] headers = {};
      int statusCode = 200;
      String statusText = "OK";
      String text = SETTINGS_XML;
      Response response = new TestResponse(headers, statusCode, statusText, text);
      try
      {
         unmarshaller.unmarshal(response);
      }
      catch (UnmarshallerException e)
      {
         e.printStackTrace();
         fail();
      }
      
      assertEquals(2, context.getHotKeys().size());
      assertEquals(1, context.getDefaultEditors().size());
      assertEquals(6, context.getToolBarItems().size());
      
      assertEquals("Ctrl+65", context.getHotKeys().get("Save"));
      assertEquals("Code Editor", context.getDefaultEditors().get("text/plain"));
      assertEquals("New File", context.getToolBarItems().get(2));
   }
   
   public void testUnmarshallerWithNullXml()
   {
      unmarshaller = new ApplicationContextUnmarshaller(eventBus, context);
      Header[] headers = {};
      int statusCode = 200;
      String statusText = "OK";
      String text = null;
      Response response = new TestResponse(headers, statusCode, statusText, text);
      try
      {
         unmarshaller.unmarshal(response);
         fail();
      }
      catch (UnmarshallerException e)
      {
         assertEquals(ApplicationContextUnmarshaller.ERROR_MESSAGE, e.getMessage());
      }
   }
   
   public void testUnmarshallerWithWrongXml()
   {
      unmarshaller = new ApplicationContextUnmarshaller(eventBus, context);
      Header[] headers = {};
      int statusCode = 200;
      String statusText = "OK";
      String text = WRONG_XML;
      Response response = new TestResponse(headers, statusCode, statusText, text);
      try
      {
         unmarshaller.unmarshal(response);
         fail("Check is unmarshaller can parse wrong XML");
      }
      catch (UnmarshallerException e)
      {
      }
   }
   
   private void initApplicationContext()
   {
      Map<String, String> hotKeys = new LinkedHashMap<String, String>();
      hotKeys.put("Save", "Ctrl+65");
      hotKeys.put("New File", "Ctrl+75");
      context.setHotKeys(hotKeys);
      
      context.getDefaultEditors().put("text/plain", "Code Editor");
      
      context.getToolBarItems().add("");
      context.getToolBarItems().add("New File");
      context.getToolBarItems().add("Save");
      context.getToolBarItems().add("---");
      context.getToolBarItems().add("Create Folder");
      
   }
   
}