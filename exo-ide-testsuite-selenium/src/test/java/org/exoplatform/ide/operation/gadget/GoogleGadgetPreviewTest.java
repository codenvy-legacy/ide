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

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.support.PageFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Test for preview gadget feature.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GoogleGadgetPreviewTest extends BaseTest
{
   private static final String PROJECT = GoogleGadgetPreviewTest.class.getSimpleName();

   private final static String FILE_NAME = "Calculator.gadget";
   
   public GadgetPreviewPage CALCULATOR;
   
   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/Calculator.xml";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GOOGLE_GADGET, filePath);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testGadgetPreview() throws Exception
   {
      CALCULATOR = PageFactory.initElements(driver, GadgetPreviewPage.class);
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);
      
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.SHOW_GADGET_PREVIEW, true);
      
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GADGET_PREVIEW);
      
      IDE.PREVIEW.waitGadgetPreviewOpened();
      
      IDE.PREVIEW.selectGadgetPreviewIframe();
      
      assertTrue(CALCULATOR.calculatorPresent());
      assertTrue(CALCULATOR.displayPresent());
      assertTrue(CALCULATOR.numberPresent());
      
      IDE.selectMainFrame();
      
      IDE.EDITOR.closeFile(FILE_NAME);
      //TODO: this test is uncomplete. Changes content of gadget, save and click Preview.
   }

}
