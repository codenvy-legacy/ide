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
package org.exoplatform.ideall.vfs.webdav;


import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.ideall.vfs.api.File;
import org.exoplatform.ideall.vfs.api.Folder;
import org.exoplatform.ideall.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.vfs.api.event.ChildrenReceivedEvent;
import org.exoplatform.ideall.vfs.api.event.ChildrenReceivedHandler;
import org.exoplatform.ideall.vfs.api.event.CopyCompleteEvent;
import org.exoplatform.ideall.vfs.api.event.CopyCompleteHandler;
import org.exoplatform.ideall.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ideall.vfs.api.event.FileContentReceivedHandler;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.vfs.api.event.FolderCreatedEvent;
import org.exoplatform.ideall.vfs.api.event.FolderCreatedHandler;
import org.exoplatform.ideall.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ideall.vfs.api.event.ItemDeletedHandler;
import org.exoplatform.ideall.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.vfs.api.event.MoveCompleteHandler;

import java.util.HashMap;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GwtTestWebDavFileSystem extends GWTTestCase
{
   
   private VirtualFileSystem vfsWebDav;
   private HandlerManager eventbus;
   private HashMap<String , String> images = new HashMap<String, String>();
   private static final String BASE = "http://localhost:8081/rest/jcr/repository/dev-monit/";
   private static final String WRONG_WS = "http://localhost:8081/rest/jcr/repository/not-found/";
   
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventbus = new HandlerManager(null);
      vfsWebDav = new WebDavVirtualFileSystem(eventbus, new EmptyLoader(),images, "/rest");
   }
   
   
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ideall.IDEallVirtualFileSystem";
   }
   
   /**
    * Create new folder
    */  
   public void testCreateFolder()
   {
      
     
      final String newFolderHref = BASE + "test";
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
      WebDavVirtualFileSystem w = new WebDavVirtualFileSystem(eventbus, new EmptyLoader(), images, "/rest");
      w.createFolder(newFolder);
      delayTestFinish(500);
   }
   
   
   /**
    * Create new folder
    */  
   public void testCreateFolderFail()
   {
      
     
      String newFolderHref = WRONG_WS + "test";
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
      delayTestFinish(500);
   }
   

   
   public void testDeleteItem()
   {
      String newFolderHref = BASE + "proba";
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
      delayTestFinish(500);
   }
   
   
   public void testDeleteFolderFail()
   {
      String newFolderHref = BASE + "proba-not-found";
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
      delayTestFinish(500);
   }
   
   
   /**
   * Get folder content
   * 
   */
   public void testGetChildren()
   {
      String newFolderHref = BASE + "main";
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
      delayTestFinish(500);
      
   }

   
   
   /**
    * Get content of the file
    * 
    */
   public void testGetContent()
   {
     
      File file = new File(BASE + "fileContent");
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
      delayTestFinish(500);  
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
     File file = new File(BASE + "newFile");
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
     delayTestFinish(500);
   }


   /**
    * Move existed item to another location as path
    * 
       */
   public void testMove()
   {
      final String newLocation = String.valueOf(System.currentTimeMillis());
      Folder folder = new Folder(BASE + "movetest");
      eventbus.addHandler(MoveCompleteEvent.TYPE, new MoveCompleteHandler()
      {
         public void onMoveComplete(MoveCompleteEvent event)
         {
            assertNotNull(event.getItem());
            assertEquals(event.getItem().getHref(), BASE + newLocation);
            finishTest();
         }
      });
      vfsWebDav.move(folder, BASE + newLocation);
      delayTestFinish(500);
   }
   
   /**
    * Move existed item to another location as path
    * 
       */
   public void testMoveFail()
   {
      Folder folder = new Folder(BASE + "movetest-not-found");
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            assertNotNull(event.getError());
            finishTest();
         }
      });
      vfsWebDav.move(folder, BASE + "new-movedtest");
      delayTestFinish(500);
   }

   /**
    * Copy item to another locations as path
    * 
    * @param item
    * @param destination
    */
   public  void testCopy()
   {
      final String copyLocation = String.valueOf(System.currentTimeMillis());
      Folder folder = new Folder(BASE + "copytest");
      eventbus.addHandler(CopyCompleteEvent.TYPE, new CopyCompleteHandler()
      {
         public void onCopyComplete(CopyCompleteEvent event)
         {
            assertNotNull(event.getDestination());
            assertNotNull(event.getCopiedItem());
            assertEquals(event.getDestination(),BASE + copyLocation);
            finishTest();
         }
      });
      vfsWebDav.copy(folder, BASE + copyLocation);
      delayTestFinish(500);
   }
   
   /**
    * Copy item to another locations as path
    * 
    * @param item
    * @param destination
    */
   public  void testCopyFail()
   {
      final String copyLocation = String.valueOf(System.currentTimeMillis());
      Folder folder = new Folder(BASE + "copytest-not-found");
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            assertNotNull(event.getError());
            finishTest();
         }
      });
      vfsWebDav.copy(folder, BASE + copyLocation);
      delayTestFinish(500);
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
