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

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * Test for Chromattic generated node type preview.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 13, 2010 $
 *
 */
public class PreviewNodeTypeTest extends BaseTest
{
   private final static String PROJECT = PreviewNodeTypeTest.class.getSimpleName();

   private static final String FILE_NAME = PreviewNodeTypeTest.class.getSimpleName() + ".cmtc";

   /**
    * The sample of EXO node type format.
    */
   private final String generatedEXOFormat =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
         + "<!--Node type generation prototype-->"
         + "<nodeTypes xmlns:jcr=\"http://www.jcp.org/jcr/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\">"
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
      + "[file] > nt:base, mix:referenceable" + "- name (String)";

   /**
    * Create test folder and test data object file.
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, "application/x-chromattic+groovy",
            "src/test/resources/org/exoplatform/ide/operation/chromattic/A.groovy");
      }
      catch (Exception e)
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
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Tests the appearance of preview node type dialog window.
    */
   @Test
   public void testGenerateNodeTypeView() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      //Check controls are present and enabled:
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.PREVIEW_NODE_TYPE, true);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.PREVIEW_NODE_TYPE);
      IDE.PREVIEW_NODE_TYPE.waitOpened();

      //Click "Cancel" button
      IDE.PREVIEW_NODE_TYPE.clickCancelButton();
      IDE.PREVIEW_NODE_TYPE.waitClosed();

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.PREVIEW_NODE_TYPE);
      IDE.PREVIEW_NODE_TYPE.waitOpened();

      //Click "Generate" button
      IDE.PREVIEW_NODE_TYPE.clickGenerateButton();
      IDE.PREVIEW_NODE_TYPE.waitClosed();
      IDE.PREVIEW_NODE_TYPE.waitGeneratedTypeViewOpened();

      //Close file and check view with generated code is closed.
      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.EDITOR.waitTabNotPresent(FILE_NAME);

      IDE.PREVIEW_NODE_TYPE.waitGeneratedTypeViewClosed();
   }

   /**
   * Tests the preview of generated node type with EXO format.
   */
   @Test
   public void testGenerateExoFormat() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      //Check controls are present and enabled:
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.PREVIEW_NODE_TYPE, true);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.PREVIEW_NODE_TYPE);
      IDE.PREVIEW_NODE_TYPE.waitOpened();

      //Click "Generate" button
      IDE.PREVIEW_NODE_TYPE.selectFormat("EXO");
      IDE.PREVIEW_NODE_TYPE.clickGenerateButton();
      IDE.PREVIEW_NODE_TYPE.waitClosed();
      IDE.PREVIEW_NODE_TYPE.waitGeneratedTypeViewOpened();
      String text = IDE.PREVIEW_NODE_TYPE.getGeneratedNodeType();

      //Clear formatting:
      text = text.replaceAll("\n", "");
      for (int i = 0; i < 8; i++)
      {
         text = text.replaceAll(" <", "<");
      }
      assertEquals(generatedEXOFormat, text);

      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.EDITOR.waitTabNotPresent(FILE_NAME);
      IDE.PREVIEW_NODE_TYPE.waitGeneratedTypeViewClosed();
   }

   /**
      * Tests the preview of generated node type withCND format.
      * 
      * @throws Exception
      */
   @Test
   public void testGenerateCndFormat() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      //Check controls are present and enabled:
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.PREVIEW_NODE_TYPE, true);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.PREVIEW_NODE_TYPE);
      IDE.PREVIEW_NODE_TYPE.waitOpened();
      IDE.PREVIEW_NODE_TYPE.selectFormat("CND");

      //Click "Generate" button
      IDE.PREVIEW_NODE_TYPE.clickGenerateButton();
      IDE.PREVIEW_NODE_TYPE.waitClosed();
      IDE.PREVIEW_NODE_TYPE.waitGeneratedTypeViewOpened();

      //Check generated code:
      String text = IDE.PREVIEW_NODE_TYPE.getGeneratedNodeType();

      //Clear formatting:
      text = text.replaceAll("\n", "");
      for (int i = 0; i < 8; i++)
      {
         text = text.replaceAll(" <", "<");
      }
      assertEquals(generatedCNDFormat, text);

      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.PREVIEW_NODE_TYPE.waitGeneratedTypeViewClosed();
   }
}
