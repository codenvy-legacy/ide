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
package org.exoplatform.ide.operation.browse.highlight;

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 12, 2010 $
 *
 */
public class HighlightBottomTabSetTest extends BaseTest
{
   private static final String PROJECT = HighlightBottomTabSetTest.class.getSimpleName();

   private static String FOLDER_NAME = HighlightBottomTabSetTest.class.getSimpleName();

   private static String FILE_NAME = "HighlightBottomTabSetTestFILE";

   private static String SHOW_PROPERTIES_ICON_LOCATOR =
      "//div[@panel-id='operation']//table/tbody/tr/td/table/tbody/tr/td[2]//div[@class='tabMiddleCenterInner']/div/div/table/tbody/tr/td[1]/img";

   @Before
   public void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/HtmlCodeOutline.html";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.TEXT_HTML,
         filePath);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      
   }

   @Test
   public void testHighlightBottopTabSet() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitTabPresent(1);
      
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);
      IDE.PROPERTIES.waitOpened();

      assertTrue(IDE.PROPERTIES.isActive());

      // assertTrue(selenium().isElementPresent("//div[@eventproxy='isc_PropertiesForm_0'  and contains(@style, 'border: 3px solid rgb(122, 173, 224)')]/"));
      selenium().click(SHOW_PROPERTIES_ICON_LOCATOR);
      assertTrue(IDE.PROPERTIES.isActive());

      IDE.EDITOR.clickOnEditor(0);

      //TODO should be completed after fix problem highlighting in codeeditor after setting cursor in text
      //
      //      IDE.PERSPECTIVE.checkViewIsNotActive("ideFilePropertiesView");
      //      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //      IDE.PERSPECTIVE.checkViewIsNotActive("editor-0");
      //
      //      IDE.EDITOR.typeTextIntoEditor(0, "test test");
      //      IDE.PERSPECTIVE.checkViewIsNotActive("editor-0");

   }

   @After
   public void tearDown()
   {
      deleteCookies();
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
