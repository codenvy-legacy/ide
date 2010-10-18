/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.vfs.webdav;

import java.util.HashMap;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ChildrenReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ChildrenReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.event.CopyCompleteEvent;
import org.exoplatform.ide.client.framework.vfs.event.CopyCompleteHandler;
import org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.event.FileContentSavedEvent;
import org.exoplatform.ide.client.framework.vfs.event.FileContentSavedHandler;
import org.exoplatform.ide.client.framework.vfs.event.FolderCreatedEvent;
import org.exoplatform.ide.client.framework.vfs.event.FolderCreatedHandler;
import org.exoplatform.ide.client.framework.vfs.event.ItemDeletedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemDeletedHandler;
import org.exoplatform.ide.client.framework.vfs.event.ItemVersionsReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemVersionsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.event.MoveCompleteEvent;
import org.exoplatform.ide.client.framework.vfs.event.MoveCompleteHandler;
import org.exoplatform.ide.client.module.vfs.webdav.WebDavVirtualFileSystem;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GwtTestWebDavFileSystem extends GWTTestCase
{

   private VirtualFileSystem vfsWebDav;

   private HandlerManager eventbus;

   private HashMap<String, String> images = new HashMap<String, String>();

   private static String testUrl;

   private static String testUrlWrongWs;

   private final int DELAY_TEST = 5000;

   @Override
   @Before
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      testUrl = "http://" + Window.Location.getHost() + "/rest/jcr/repository/dev-monit/"; 
      testUrlWrongWs = "http://" + Window.Location.getHost() + "/rest/jcr/repository/not-found/";
      eventbus = new HandlerManager(null);
      vfsWebDav = new WebDavVirtualFileSystem(eventbus, new EmptyLoader(), images, "/rest");
   }

   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.client.module.IDEVirtualFileSystem";
   }

   /**
    * Create new folder
    */
   @Test
   public void testCreateFolder()
   {

      final String newFolderHref = testUrl + "test";
      Folder newFolder = new Folder(newFolderHref);
      eventbus.addHandler(FolderCreatedEvent.TYPE, new FolderCreatedHandler()
      {
         public void onFolderCreated(FolderCreatedEvent event)
         {
            assertNotNull(event.getFolder());
            assertEquals(newFolderHref, event.getFolder().getHref());
            finishTest();
         }
      });
      vfsWebDav.createFolder(newFolder);
      delayTestFinish(DELAY_TEST);
   }

   /**
//    * Create new folder
//    */
   public void testCreateFolderFail()
   {

      String newFolderHref = testUrlWrongWs + "test";
      Folder newFolder = new Folder(newFolderHref);
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            assertNotNull(event.getError());
            finishTest();
         }
      });
      WebDavVirtualFileSystem w = new WebDavVirtualFileSystem(eventbus, new EmptyLoader(), images, "/rest");
      w.createFolder(newFolder);
      delayTestFinish(DELAY_TEST);
   }

   public void testDeleteItem()
   {
      String newFolderHref = testUrl + "proba";
      Folder newFolder = new Folder(newFolderHref);
      eventbus.addHandler(ItemDeletedEvent.TYPE, new ItemDeletedHandler()
      {
         public void onItemDeleted(ItemDeletedEvent event)
         {
            finishTest();
         }
      });
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new MockExceptionThrownHandler());
      vfsWebDav.deleteItem(newFolder);
      delayTestFinish(DELAY_TEST);
   }

   public void testDeleteFolderFail()
   {
      String newFolderHref = testUrl + "proba-not-found";
      Folder newFolder = new Folder(newFolderHref);
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            assertNotNull(event.getError());
            finishTest();
         }
      });
      vfsWebDav.deleteItem(newFolder);
      delayTestFinish(DELAY_TEST);
   }

   /**
   * Get folder content
   * 
   */
   public void testGetChildren()
   {
      String newFolderHref = testUrl + "main";
      Folder newFolder = new Folder(newFolderHref);
      eventbus.addHandler(ChildrenReceivedEvent.TYPE, new ChildrenReceivedHandler()
      {
         public void onChildrenReceived(ChildrenReceivedEvent event)
         {
            finishTest();
         }
      });
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getErrorMessage());
            finishTest();
         }
      });
      vfsWebDav.getChildren(newFolder);
      delayTestFinish(DELAY_TEST);

   }

   /**
    * Get content of the file
    * 
    */
   public void testGetContent()
   {

      File file = new File(testUrl + "fileContent");
      eventbus.addHandler(FileContentReceivedEvent.TYPE, new FileContentReceivedHandler()
      {
         public void onFileContentReceived(FileContentReceivedEvent event)
         {
            finishTest();
         }
      });

      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getErrorMessage());
            finishTest();
         }
      });
      vfsWebDav.getContent(file);
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Save file content
    * 
    * @param file
    * @param path
    */
   public void testSaveContent()
   {
      final String fileContent = System.currentTimeMillis() + "";
      File file = new File(testUrl + "newFile");
      file.setContentType("text/plain");
      file.setJcrContentNodeType(NodeTypeUtil.getContentNodeType("text/plain"));
      //     newFile.setIcon(ImageUtil.getIcon(contentType));
      file.setNewFile(true);
      file.setContent(fileContent);
      file.setContentChanged(true);
      eventbus.addHandler(FileContentSavedEvent.TYPE, new FileContentSavedHandler()
      {
         public void onFileContentSaved(FileContentSavedEvent event)
         {
            assertNotNull(event.getFile());
            assertEquals(event.getFile().getContent(), fileContent);
            finishTest();
         }
      });
      vfsWebDav.saveContent(file);
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Move existed item to another location as path
    * 
       */
   public void testMove()
   {
      final String newLocation = String.valueOf(System.currentTimeMillis());
      Folder folder = new Folder(testUrl + "movetest");
      eventbus.addHandler(MoveCompleteEvent.TYPE, new MoveCompleteHandler()
      {
         public void onMoveComplete(MoveCompleteEvent event)
         {
            assertNotNull(event.getItem());
            assertEquals(event.getItem().getHref(), testUrl + newLocation);
            finishTest();
         }
      });
      vfsWebDav.move(folder, testUrl + newLocation);
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Move existed item to another location as path
    * 
       */
   public void testMoveFail()
   {
      Folder folder = new Folder(testUrl + "movetest-not-found");
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            assertNotNull(event.getError());
            finishTest();
         }
      });
      vfsWebDav.move(folder, testUrl + "new-movedtest");
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Copy item to another locations as path
    * 
    * @param item
    * @param destination
    */
   public void testCopy()
   {
      final String copyLocation = String.valueOf(System.currentTimeMillis());
      Folder folder = new Folder(testUrl + "copytest");
      eventbus.addHandler(CopyCompleteEvent.TYPE, new CopyCompleteHandler()
      {
         public void onCopyComplete(CopyCompleteEvent event)
         {
            assertNotNull(event.getDestination());
            assertNotNull(event.getCopiedItem());
            assertEquals(event.getDestination(), testUrl + copyLocation);
            finishTest();
         }
      });
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            event.getError().printStackTrace();
         }
      });
      vfsWebDav.copy(folder, testUrl + copyLocation);
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Copy item to another locations as path
    * 
    * @param item
    * @param destination
    */
   public void testCopyFail()
   {
      final String copyLocation = String.valueOf(System.currentTimeMillis());
      Folder folder = new Folder(testUrl + "copytest-not-found");
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            assertNotNull(event.getError());
            finishTest();
         }
      });
      vfsWebDav.copy(folder, testUrl + copyLocation);
      delayTestFinish(DELAY_TEST);
   }
   
   public void testGetVersionList()
   {
      final String fileContent = System.currentTimeMillis() + "";
      File file = new File(testUrl + "versionFile");
      file.setContentType("text/plain");
      file.setJcrContentNodeType(NodeTypeUtil.getContentNodeType("text/plain"));
      //     newFile.setIcon(ImageUtil.getIcon(contentType));
      file.setNewFile(true);
      file.setContent(fileContent);
      file.setContentChanged(true);
      
      vfsWebDav.saveContent(file);
      
      file.setContent(file.getContent() + " " + System.currentTimeMillis());
      file.setContentChanged(true);
      vfsWebDav.saveContent(file);
      
      file.setContent(file.getContent() + " " + System.currentTimeMillis());
      file.setContentChanged(true);
      vfsWebDav.saveContent(file);
      
      eventbus.addHandler(ItemVersionsReceivedEvent.TYPE, new ItemVersionsReceivedHandler()
      {
         @Override
         public void onItemVersionsReceived(ItemVersionsReceivedEvent event)
         {
            assertNotNull(event.getVersions());
            assertNotNull(event.getItem());
            assertEquals(3, event.getVersions().size());
            assertNotNull(event.getVersions().get(0).getHref());
         }
      });
      
      vfsWebDav.getVersions(file);
   }

   //   /**
   //    * Get properties of file or folder
   //    * 
   //    * @param item
   //    */
   //   public void testGetProperties()
   //   {
   //      
   //   }
   //
   //   /**
   //    * Save properties of file or folder
   //    * 
   //    * @param item
   //    */
   //   public void testSaveProperties()
   //   {
   //      
   //   }
   //
   //   /**
   //    * Search files
   //    * 
   //    * @param folder
   //    * @param text
   //    * @param mimeType
   //    * @param path
   //    */
   //   public void testSsearch()
   //   {
   //   
   //   }

   @Override
   protected void gwtTearDown() throws Exception
   {
   }

   private class MockExceptionThrownHandler implements ExceptionThrownHandler
   {

      //     private boolean fail; 
      //      public MockExceptionThrownHandler(boolean fail)
      //      {
      //         this.fail = fail;
      //      }

      public void onError(ExceptionThrownEvent event)
      {
         fail();
      }

   }

}
