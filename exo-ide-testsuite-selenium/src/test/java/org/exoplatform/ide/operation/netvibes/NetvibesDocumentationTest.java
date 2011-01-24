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
package org.exoplatform.ide.operation.netvibes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: NetVibesDocumentationTest Jan 24, 2011 2:25:35 PM evgen $
 *
 */
public class NetvibesDocumentationTest extends BaseTest
{

   /**
    *  Locator for documentation iframe
    */
   private static final String DE_DOCUMENTATION_FRAME = "//iframe[@id='gwt-debug-ideDocumentationFrame']";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   private static String FILE_NAME = NetvibesDocumentationTest.class.getName(); 
   
   @Test
   public void testNetvibesDocumentation() throws Exception
   {
      waitForRootElement();
      
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.NETVIBES_WIDGET);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      
      IDE.toolbar().runCommand(ToolbarCommands.View.SHOW_DOCUMENTATION);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      
      assertTrue(selenium.isElementPresent(DE_DOCUMENTATION_FRAME));
      
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      
      assertFalse(selenium.isElementPresent(DE_DOCUMENTATION_FRAME));
      
      IDE.editor().selectTab(0);
      
      assertTrue(selenium.isElementPresent(DE_DOCUMENTATION_FRAME));
      
      saveAsByTopMenu(FILE_NAME);
      
      assertTrue(selenium.isElementPresent(DE_DOCUMENTATION_FRAME));
      
      IDE.editor().selectTab(1);
      
      assertFalse(selenium.isElementPresent(DE_DOCUMENTATION_FRAME));
      
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      waitForRootElement();
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertTrue(selenium.isElementPresent(DE_DOCUMENTATION_FRAME));
      
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL+FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
   
}
