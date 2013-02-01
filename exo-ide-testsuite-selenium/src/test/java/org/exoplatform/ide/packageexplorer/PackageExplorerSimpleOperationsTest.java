/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.packageexplorer;

import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version $Id: 11:01:24 AM  Dec 27, 2012 $
 *
 */
public class PackageExplorerSimpleOperationsTest extends BaseTest
{
   private static final String PROJECT = "SimpleOpsPrj";

   private static final String FILE_NAME = "JavaCommentsTest.java";

   private static final String XML_FILE_NAME = "test.xml";

   final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/JavaCommentsTest.zip";

   static Map<String, Link> project;

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/JavaCommentsTest.zip";

      try
      {
         project = VirtualFileSystemUtils.importZipProject(PROJECT, filePath);
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Close and open package explorer using toolbar button.
    * 
    * @throws Exception
    */
   @Test
   public void closeAndOpenPackageExplorerTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      IDE.PROJECT.PACKAGE_EXPLORER.closePackageExplorer();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerClosed();
      IDE.TOOLBAR.runCommand(ToolbarCommands.PackageExplorer.PACKAGE_EXPLORER);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      IDE.TOOLBAR.runCommand(ToolbarCommands.PackageExplorer.PACKAGE_EXPLORER);
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerClosed();
      IDE.TOOLBAR.runCommand(ToolbarCommands.PackageExplorer.PACKAGE_EXPLORER);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
   }

   /**
    * Checking refresh in Package Explorer
    * @throws Exception
    */
   @Test
   public void refreshPackageExplorerTest() throws Exception
   {
      // adding new file in to project via vfs and refresh.
      Link link = project.get(Link.REL_CREATE_FILE);
      VirtualFileSystemUtils.createFileFromLocal(link, XML_FILE_NAME, MimeType.TEXT_XML, filePath);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(XML_FILE_NAME);
   }

   /**
    * Open folders, packages and files in Package Explorer
    * 
    * @throws Exception
    */
   @Test
   public void openFolderPackageAndFileInPackageExplorerTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("commenttest");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("commenttest");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(FILE_NAME);
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick(FILE_NAME);
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      //close folders
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.EDITOR.closeFile(FILE_NAME);
   }

   /**
    * Copy file in Package Explorer
    * @throws Exception 
    * 
    */
   @Test
   public void copyFileInPackageExplorerTest() throws Exception
   {
      // open folders
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("commenttest");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("commenttest");
      // copying
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(FILE_NAME);
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(FILE_NAME);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.COPY_SELECTED_ITEM);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.PASTE);
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      // pasting copied file in to root
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("pom.xml");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.PASTE);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(FILE_NAME);
   }

   /**
    * Deleting file from Package Explorer
    * @throws Exception
    */
   @Test
   public void deleteInPackageExplorerTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(FILE_NAME);
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent(FILE_NAME);
   }

   /**
    * Cutting file in Package Explorer
    * @throws Exception
    */
   @Test
   public void cutInPackageExplorerTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("commenttest");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("commenttest");

      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(FILE_NAME);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.CUT_SELECTED_ITEM);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.PASTE);
      // pasting
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("src");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.PASTE);
      IDE.LOADER.waitClosed();
      // checking that file was removed
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent(FILE_NAME);
      //CHECK STYLE OF PACKAGE
      // check that file appeared in src folder
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer(FILE_NAME);
   }

   /**
    * Copy folder with nested folders in Package Explorer
    *
    * @throws Exception 
    */
   @Test
   public void copyFoldersInPackageExplorerTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(PROJECT);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.LOADER.waitClosed();
      // copying
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src");
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("main");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.COPY_SELECTED_ITEM);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.PASTE);
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src");
      // pasting copied file in to root
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(PROJECT);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.PASTE);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("main");
   }

   /**
    * Delete folder with nested folders in Package Explorer
    * 
    * @throws Exception 
    */
   @Test
   public void deleteFoldersInPackageExplorerTest() throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer(PROJECT);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.LOADER.waitClosed();
      // deleting
      IDE.PROJECT.PACKAGE_EXPLORER.selectItemInPackageExplorer("main");
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorerIsNotPresent("main");
   }

   /**
    * Context menu in package explorer
    */
}
