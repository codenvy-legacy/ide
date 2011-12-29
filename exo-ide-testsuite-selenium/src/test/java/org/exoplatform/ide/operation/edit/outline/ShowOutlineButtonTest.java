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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Check, that toolbar button Show outline is present only
 * JavaScript, XML, HTML, Google Gadget or Groovy template files are opened.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Aug 11, 2010
 *
 */

public class ShowOutlineButtonTest extends BaseTest
{
   private final static String PROJECT = OutlineClosingTest.class.getSimpleName();

   @BeforeClass
   public static void setUp() throws Exception
   {
      VirtualFileSystemUtils.createDefaultProject(PROJECT);
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

   //check, that show outline button is shown only for
   //files, which have outline
   @Test
   public void testShowOutlineButton() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.js");
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE));

      //---- 2 ------
      //open xml file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.xml");
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE));

      //---- 3 ------
      //open html file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.html");
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE));

      //---- 4 ------
      //open google gadget file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.gadget");
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE));

      //---- 5 ------
      //open text file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.TOOLBAR.waitButtonNotPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE);

      //---- 6 ------
      //open css file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.CSS_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.css");
      IDE.TOOLBAR.waitButtonNotPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE);
      
      //---- 7 ------
      //open rest service file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.grs");
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE));

      //---- 8 ------
      //open groovy script file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.groovy");
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE));

      //---- 9 ------
      //open groovy template file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.gtmpl");
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE));

      //---- 10 ------
      //open select tab with xml file
      IDE.EDITOR.selectTab(2);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.xml");
      assertTrue(IDE.TOOLBAR.isButtonPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE));
   }
}