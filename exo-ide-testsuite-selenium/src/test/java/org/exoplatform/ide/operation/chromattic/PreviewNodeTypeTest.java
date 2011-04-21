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
package org.exoplatform.ide.operation.chromattic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for Chromattic generated node type preview.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 13, 2010 $
 *
 */
public class PreviewNodeTypeTest extends AbstractDataObjectTest
{
   private static final String FILE_NAME = PreviewNodeTypeTest.class.getSimpleName() + ".groovy";

   private final static String TEST_FOLDER = PreviewNodeTypeTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   /**
    * The sample of EXO node type format.
    */
   private final String generatedEXOFormat =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
         + "<!--Node type generation prototype-->"
         + "<nodeTypes xmlns:jcr=\"http://www.jcp.org/jcr/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\">"
         + "<!-- Node type generated for the class java.lang.Object -->"
         + "<nodeType name=\"nt:base\" isMixin=\"false\" hasOrderableChildNodes=\"false\">"
         + "<supertypes>"
         + "<supertype>nt:base</supertype>"
         + "<supertype>mix:referenceable</supertype>"
         + "</supertypes>"
         + "<propertyDefinitions/>"
         + "<childNodeDefinitions/>"
         + "</nodeType>"
         + "<!-- Node type generated for the class A -->"
         + "<nodeType name=\"file\" isMixin=\"false\" hasOrderableChildNodes=\"false\">"
         + "<supertypes>"
         + "<supertype>nt:base</supertype>"
         + "<supertype>mix:referenceable</supertype>"
         + "</supertypes>"
         + "<propertyDefinitions>"
         + "<propertyDefinition name=\"name\" requiredType=\"String\" autoCreated=\"false\" mandatory=\"false\" onParentVersion=\"COPY\" protected=\"false\" multiple=\"false\">"
         + "<valueConstraints/>" + "</propertyDefinition>" + "</propertyDefinitions>" + "<childNodeDefinitions/>"
         + "</nodeType>" + "</nodeTypes>";

   /**
    * The sample CND node type format.
    */
   private final String generatedCNDFormat = "<jcr = 'http://www.jcp.org/jcr/1.0'>"
      + "<nt = 'http://www.jcp.org/jcr/nt/1.0'>" + "<mix = 'http://www.jcp.org/jcr/mix/1.0'>"
      + "[nt:base] > nt:base, mix:referenceable" + "[file] > nt:base, mix:referenceable" + "- name (String)";

   /**
    * Create test folder and test data object file.
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         String url = URL + TEST_FOLDER + "/";
         VirtualFileSystemUtils.mkcol(url);
         VirtualFileSystemUtils.put("src/test/resources/org/exoplatform/ide/operation/chromattic/A.groovy",
            MimeType.CHROMATTIC_DATA_OBJECT, url + FILE_NAME);
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

   /**
    * Clear tests results.
    */
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
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

   /**
    * Clean result of each test.
    * 
    * @throws Exception
    */
   @After
   public void cleanTest() throws Exception
   {
      IDE.editor().closeTab(0);
   }

   /**
    * Tests the appearance of preview node type dialog window.
    */
   @Test
   public void testGenerateNodeTypeForm() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      //Check controls are present and enabled:
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkPreviewNodeTypeButton(true, true);
      checkDeployNodeTypeButton(true, true);

      //Click preview node type button and check dialog window appears
      IDE.toolbar().runCommand(ToolbarCommands.Run.PREVIEW_NODE_TYPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkGenerateNodeTypeFormPresent();

      //Click "Cancel" button
      selenium.click("scLocator=//IButton[ID=\"ideGenerateNodeTypeFormCancelButton\"]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGenerateNodeTypeForm\"]"));

      //Click preview node type button and check dialog window appears
      IDE.toolbar().runCommand(ToolbarCommands.Run.PREVIEW_NODE_TYPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkGenerateNodeTypeFormPresent();

      //Click "Generate" button
      selenium.click("scLocator=//IButton[ID=\"ideGenerateNodeTypeFormGenerateButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGenerateNodeTypeForm\"]"));
      checkViewWithGeneratedCodePresent(true);

      //Close file and check view with generated code is closed.
      IDE.editor().closeTab(0);
      Thread.sleep(TestConstants.SLEEP);
      checkViewWithGeneratedCodePresent(false);
   }

   /**
    * Tests the preview of generated node type with EXO format.
    */
   @Test
   public void testGenerateExoFormat() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);

      IDE.navigator().clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      //Check controls are present and enabled:
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkPreviewNodeTypeButton(true, true);
      checkDeployNodeTypeButton(true, true);

      //Click preview node type button and check dialog window appears
      IDE.toolbar().runCommand(ToolbarCommands.Run.PREVIEW_NODE_TYPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkGenerateNodeTypeFormPresent();

      //Click "Generate" button
      selenium.click("scLocator=//IButton[ID=\"ideGenerateNodeTypeFormGenerateButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGenerateNodeTypeForm\"]"));
      checkViewWithGeneratedCodePresent(true);

      //Check generated code:
      selenium
         .selectFrame("//div[@eventproxy='ideGeneratedTypePreviewForm']//div[@class='CodeMirror-wrapping']/iframe");

      String text = selenium.getText("//body[@class='editbox']");
      //Clear formatting:
      text = text.replaceAll("\n", "");
      for (int i = 0; i < 8; i++)
      {
         text = text.replaceAll(" <", "<");
      }

      IDE.selectMainFrame();
      assertEquals(generatedEXOFormat, text);

      IDE.editor().closeTab(0);
      Thread.sleep(TestConstants.SLEEP);
      checkViewWithGeneratedCodePresent(false);
   }

   /**
    * Tests the preview of generated node type withCND format.
    * 
    * @throws Exception
    */
   @Test
   public void testGenerateCndFormat() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);

      IDE.navigator().clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      //Check controls are present and enabled:
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkPreviewNodeTypeButton(true, true);
      checkDeployNodeTypeButton(true, true);

      //Click preview node type button and check dialog window appears
      IDE.toolbar().runCommand(ToolbarCommands.Run.PREVIEW_NODE_TYPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkGenerateNodeTypeFormPresent();

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGenerateNodeTypeFormDynamicForm\"]/item[name=ideGenerateNodeTypeFormFormatField]/[icon='picker']");
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGenerateNodeTypeFormDynamicForm\"]/item[name=ideGenerateNodeTypeFormFormatField]/pickList/body/row[ideGenerateNodeTypeFormFormatField=CND||1]/col[fieldName=ideGenerateNodeTypeFormFormatField||0]");

      //Click "Generate" button
      selenium.click("scLocator=//IButton[ID=\"ideGenerateNodeTypeFormGenerateButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGenerateNodeTypeForm\"]"));
      checkViewWithGeneratedCodePresent(true);

      //Check generated code:
      selenium
         .selectFrame("//div[@eventproxy='ideGeneratedTypePreviewForm']//div[@class='CodeMirror-wrapping']/iframe");

      String text = selenium.getText("//body[@class='editbox']");
      //Clear formatting:
      text = text.replaceAll("\n", "");
      for (int i = 0; i < 8; i++)
      {
         text = text.replaceAll(" <", "<");
      }

      IDE.selectMainFrame();
      assertEquals(generatedCNDFormat, text);

      IDE.editor().closeTab(0);
      Thread.sleep(TestConstants.SLEEP);
      checkViewWithGeneratedCodePresent(false);
   }

}
