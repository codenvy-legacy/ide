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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Outline.TokenType;
import org.exoplatform.ide.operation.edit.GroovyTypeValidationAndFixingTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

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

   private final static String PROJECT = CodeOutLineGroovyTemplateTest.class.getSimpleName();

   private OutlineTreeHelper outlineTreeHelper;

   public CodeOutLineGroovyTemplateTest()
   {
      this.outlineTreeHelper = new OutlineTreeHelper();
   }

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/GroovyTemplateCodeOutline.gtmpl";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GROOVY_TEMPLATE, filePath);
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testCreateOutlineTreeGroovyTemplate() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      IDE.OUTLINE.waitOutlineTreeVisible();

      checkTreeCorrectlyCreated();

      //      assertTrue(IDE.OUTLINE.isItemPresentById("groovy code:GROOVY_TAG:1"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("a1:PROPERTY:2"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("a2:PROPERTY:3"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("a2:METHOD:4"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("a3:METHOD:7"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("cTab:PROPERTY:10"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("cName:PROPERTY:10"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("description:PROPERTY:10"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("displayName:PROPERTY:10"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("isSelected:PROPERTY:11"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("a4:PROPERTY:22"));
      //check other nodes
      //      assertTrue(IDE.OUTLINE.isItemPresentById("groovy code:GROOVY_TAG:26"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("div:TAG:27"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("div:TAG:28"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("groovy code:GROOVY_TAG:29"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("groovy code:GROOVY_TAG:32"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("div:TAG:33"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("a:TAG:34"));
      //      assertTrue(IDE.OUTLINE.isItemPresentById("groovy code:GROOVY_TAG:34"));
   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      // create initial outline tree map
      OutlineTreeHelper.init();

      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();

      // expand outline tree
      outlineTreeHelper.expandOutlineTree();

      // TODO issue IDE-1499
      IDE.GOTOLINE.goToLine(10);
      IDE.GOTOLINE.goToLine(29);

      outlineTreeHelper.addOutlineItem("groovy code", 1, TokenType.GROOVY_TAG);
      outlineTreeHelper.addOutlineItem("a1 : Object", 2, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("a2 : String", 3, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("a2() : boolean", 4, TokenType.METHOD);
      outlineTreeHelper.addOutlineItem("a3(String) : void", 7, TokenType.METHOD);
      outlineTreeHelper.addOutlineItem("cTab : String", 10, false, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("cName : String", 10, false, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("description : String", 10, false, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("displayName : String", 10, false, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("isSelected : boolean", 11, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("a4 : Integer", 22, TokenType.PROPERTY);
      outlineTreeHelper.addOutlineItem("groovy code", 26, TokenType.GROOVY_TAG);
      outlineTreeHelper.addOutlineItem("div", 27, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("div", 28, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("groovy code", 29, TokenType.GROOVY_TAG);
      outlineTreeHelper.addOutlineItem("groovy code", 32, TokenType.GROOVY_TAG);
      outlineTreeHelper.addOutlineItem("div", 33, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("a", 34, false, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("groovy code", 34, false, TokenType.GROOVY_TAG);

      outlineTreeHelper.checkOutlineTree();
   }
}
