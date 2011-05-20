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

import static org.junit.Assert.assertEquals;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.edit.JavaTypeValidationAndFixingTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 
 */
public class CodeOutLineGroovyTemplateTest extends BaseTest
{
   
   private final static String FILE_NAME = "GroovyTemplateCodeOutline.gtmpl";

   private final static String TEST_FOLDER = JavaTypeValidationAndFixingTest.class.getSimpleName();

   private static final String WAIT_FOR_PARSING_TEST_LOCATOR =
      "//html[@style='border-width: 0pt;']//body[@class='editbox']//span[284][@class='xml-tagname']";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/GroovyTemplateCodeOutline.gtmpl";
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_TEMPLATE, WS_URL + TEST_FOLDER + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   // IDE-178:Groovy Template Code Outline
   @Test
   public void testCreateOutlineTreeGroovyTemplate() throws Exception
   {
      //---- 1-2 -----------------
      //open file with text
      // Open groovy file with test content

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + TEST_FOLDER + "/");

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME, false);
      waitForElementPresent(WAIT_FOR_PARSING_TEST_LOCATOR);

      //---- 3 -----------------
      //open Outline Panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      waitForElementPresent("ideOutlineTreeGrid");

      //---- 4 -----------------
      //check Outline tree
      checkTreeCorrectlyCreated();
   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      //check for presence of tab outline

      //check tree correctly created:
      //IDE.OUTLINE.assertElmentPresentById("groovy code");
      
      IDE.OUTLINE.assertElmentPresentById("groovy code:GROOVY_TAG:1");
      IDE.OUTLINE.assertElmentPresentById("a1:PROPERTY:2");
      IDE.OUTLINE.assertElmentPresentById("a2:PROPERTY:3");
      IDE.OUTLINE.assertElmentPresentById("a2:METHOD:4");
      IDE.OUTLINE.assertElmentPresentById("a3:METHOD:7");
      IDE.OUTLINE.assertElmentPresentById("cTab:PROPERTY:10");
      IDE.OUTLINE.assertElmentPresentById("cName:PROPERTY:10");
      IDE.OUTLINE.assertElmentPresentById("description:PROPERTY:10");
      IDE.OUTLINE.assertElmentPresentById("displayName:PROPERTY:10");
      IDE.OUTLINE.assertElmentPresentById("isSelected:PROPERTY:11");
      IDE.OUTLINE.assertElmentPresentById("a4:PROPERTY:22");
      //check other nodes
      IDE.OUTLINE.assertElmentPresentById("groovy code:GROOVY_TAG:26");
      IDE.OUTLINE.assertElmentPresentById("div:TAG:27");
      IDE.OUTLINE.assertElmentPresentById("div:TAG:28");
      IDE.OUTLINE.assertElmentPresentById("groovy code:GROOVY_TAG:29");
      IDE.OUTLINE.assertElmentPresentById("groovy code:GROOVY_TAG:32");
      IDE.OUTLINE.assertElmentPresentById("div:TAG:33");
      IDE.OUTLINE.assertElmentPresentById("a:TAG:34");
      IDE.OUTLINE.assertElmentPresentById("groovy code:GROOVY_TAG:34");
   }

}
