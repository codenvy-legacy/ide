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
package org.exoplatform.ide.operation.gadget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import org.everrest.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for creating gadget from template.
 * 
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 11, 2010
 *
 */
public class GadgetDevelopmentTest extends BaseTest
{

   private static final String FILE_NAME = "Test Gadget File";

   private static final String FILE_NAME_FULL = "Test Gadget File.xml";

   private static final String FOLDER_NAME = GadgetDevelopmentTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME + "/";

   @BeforeClass
   public static void setUp() throws IOException, ModuleException
   {
      VirtualFileSystemUtils.mkcol(URL);
   }

   //IDE-78
   @Test
   public void createGadgetFromTemplate() throws Exception
   {
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL);
      IDE.WORKSPACE.selectItem(URL);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);

      //wait for File from template form appeared
      IDE.TEMPLATES.waitForFileFromTemplateForm();

      //Select "Google Gadget" in the central column, change "File Name" field text on "Test Gadget File" name, click on "Create" button.
      IDE.TEMPLATES.selectFileTemplate("Google Gadget");

      IDE.TEMPLATES.typeNameToInputField(FILE_NAME);
      IDE.TEMPLATES.clickCreateButton();

      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.checkIsTabPresentInEditorTabset(FILE_NAME + " *", true);

      //Click on "Save As" button and save file "Test Gadget File" with default name.
      saveAsUsingToolbarButton(FILE_NAME_FULL);

      assertEquals(200, VirtualFileSystemUtils.get(URL + FILE_NAME_FULL).getStatusCode());

      IDE.EDITOR.closeFile(0);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME_FULL, false);
      IDE.EDITOR.waitTabPresent(0);
      
      final String tabTitle = IDE.EDITOR.getTabTitle(0);
      assertTrue(tabTitle.equals(FILE_NAME_FULL)|| tabTitle.equals(FILE_NAME_FULL + " *"));
      IDE.EDITOR.closeFile(0);
   }

   @AfterClass
   public static void tearDown() throws IOException, ModuleException
   {
      cleanRegistry();
      VirtualFileSystemUtils.delete(URL);
   }

}