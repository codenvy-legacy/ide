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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for netvibes documentation frame.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: NetVibesDocumentationTest Jan 24, 2011 2:25:35 PM evgen $
 *
 */
public class NetvibesDocumentationTest extends BaseTest
{

   /**
    *  Locator for documentation iframe
    */
   private static final String IDE_DOCUMENTATION_FRAME = "//iframe[@id='gwt-debug-ideDocumentationFrame']";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private static String FILE_NAME = NetvibesDocumentationTest.class.getSimpleName();

   @Test
   public void testNetvibesDocumentation() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.NETVIBES_WIDGET);
      IDE.EDITOR.waitTabPresent(0);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.View.SHOW_DOCUMENTATION, true);

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_DOCUMENTATION);
      waitForElementPresent(IDE_DOCUMENTATION_FRAME);
      assertTrue(selenium().isElementPresent(IDE_DOCUMENTATION_FRAME));

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      IDE.EDITOR.waitTabPresent(1);
      waitForElementNotPresent(IDE_DOCUMENTATION_FRAME);
      assertFalse(selenium().isElementPresent(IDE_DOCUMENTATION_FRAME));

      IDE.EDITOR.selectTab(0);
      waitForElementPresent(IDE_DOCUMENTATION_FRAME);
      assertTrue(selenium().isElementPresent(IDE_DOCUMENTATION_FRAME));

      saveAsByTopMenu(FILE_NAME);
      waitForElementPresent(IDE_DOCUMENTATION_FRAME);
      assertTrue(selenium().isElementPresent(IDE_DOCUMENTATION_FRAME));

      IDE.EDITOR.selectTab(1);
      waitForElementNotPresent(IDE_DOCUMENTATION_FRAME);
      assertFalse(selenium().isElementPresent(IDE_DOCUMENTATION_FRAME));

      refresh();
      IDE.WORKSPACE.waitForItem(WS_URL + FILE_NAME);
      waitForElementPresent(IDE_DOCUMENTATION_FRAME);
      
      assertTrue(selenium().isElementPresent(IDE_DOCUMENTATION_FRAME));

   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FILE_NAME);
      }
      catch (IOException e)
      {
      }
   }

}
