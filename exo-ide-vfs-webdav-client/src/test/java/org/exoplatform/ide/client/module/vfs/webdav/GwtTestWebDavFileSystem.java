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
package org.exoplatform.ide.client.module.vfs.webdav;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.framework.vfs.CopyCallback;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.FolderCreateCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.LockToken;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.callback.MoveItemCallback;
import org.exoplatform.ide.client.module.vfs.webdav.marshal.LockItemUnmarshaller;
import org.exoplatform.ide.testframework.http.MockResponse;
import org.junit.Before;

import java.util.Arrays;
import java.util.HashMap;

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
   
   private static final String WEBDAV_CONTEXT = "/jcr/";

   @Override
   @Before
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      testUrl = "http://" + Window.Location.getHost() + "/rest"+WEBDAV_CONTEXT+"repository/dev-monit/";
      testUrlWrongWs = "http://" + Window.Location.getHost() + "/rest"+WEBDAV_CONTEXT+"repository/not-found/";
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
   public void testCreateFolder()
   {

      final String newFolderHref = testUrl + "test";
      Folder newFolder = new Folder(newFolderHref);
      vfsWebDav.createFolder(newFolder, new FolderCreateCallback()
      {
         @Override
         protected void onSuccess(Folder result)
         {
            assertNotNull(result);
            assertEquals(newFolderHref, result.getHref());
            finishTest();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            fail();
         }
         
      });
      delayTestFinish(DELAY_TEST);
   }

   public void testCreateFolderFail()
   {

      String newFolderHref = testUrlWrongWs + "test";
      Folder newFolder = new Folder(newFolderHref);
      WebDavVirtualFileSystem w = new WebDavVirtualFileSystem(eventbus, new EmptyLoader(), images, "/rest");
      w.createFolder(newFolder, new FolderCreateCallback()
      {
         @Override
         protected void onSuccess(Folder result)
         {
            fail("Url was wrong. Can't create folder.");
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            assertNotNull(exception);
            finishTest();
         }
         
      });
      delayTestFinish(DELAY_TEST);
   }

   public void testDeleteItem()
   {
      String newFolderHref = testUrl + "proba";
      Folder newFolder = new Folder(newFolderHref);
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new MockExceptionThrownHandler());
      vfsWebDav.deleteItem(newFolder, new AsyncRequestCallback<Item>()
      {
         @Override
         protected void onSuccess(Item result)
         {
            finishTest();
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            fail();
         }

      });
      delayTestFinish(DELAY_TEST);
   }

   public void testDeleteFolderFail()
   {
      String newFolderHref = testUrl + "proba-not-found";
      Folder newFolder = new Folder(newFolderHref);
      vfsWebDav.deleteItem(newFolder, new AsyncRequestCallback<Item>()
      {
         @Override
         protected void onSuccess(Item result)
         {
            fail();
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            finishTest();
         }

      });
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Save file content
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
      vfsWebDav.saveContent(file, null, new FileContentSaveCallback()
      {
         @Override
         protected void onSuccess(FileData result)
         {
            assertNotNull(result.getFile());
            assertEquals(result.getFile().getContent(), fileContent);
            finishTest();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            fail();
         }
      });
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
      vfsWebDav.move(folder, testUrl + newLocation, null, new MoveItemCallback()
      {
         @Override
         protected void onSuccess(MoveItemData result)
         {
            assertNotNull(result.getItem());
            assertEquals(result.getItem().getHref(), testUrl + newLocation);
            finishTest();

         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            fail();
         }
      });
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Move existed item to another location as path
    * 
   */
   public void testMoveFail()
   {
      Folder folder = new Folder(testUrl + "movetest-not-found");
      vfsWebDav.move(folder, testUrl + "new-movedtest", null, new MoveItemCallback()
      {
         @Override
         protected void onSuccess(MoveItemData result)
         {
            fail();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            finishTest();
         }
      });
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
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            event.getException().printStackTrace();
         }
      });
      vfsWebDav.copy(folder, testUrl + copyLocation, new CopyCallback()
      {
         @Override
         protected void onSuccess(CopyItemData result)
         {
            assertNotNull(result.getDestination());
            assertNotNull(result.getItem());
            assertEquals(result.getDestination(), testUrl + copyLocation);
            finishTest();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            fail();
         }
      });
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
            assertNotNull(event.getException());
            finishTest();
         }
      });
      vfsWebDav.copy(folder, testUrl + copyLocation, new CopyCallback()
      {
         @Override
         protected void onSuccess(CopyItemData result)
         {
            fail();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            finishTest();
         }
      });
      delayTestFinish(DELAY_TEST);
   }

//   public void testGetVersionList()
//   {
//      final String fileContent = System.currentTimeMillis() + "";
//      File file = new File(testUrl + "versionFile");
//      file.setContentType("text/plain");
//      file.setJcrContentNodeType(NodeTypeUtil.getContentNodeType("text/plain"));
//      //     newFile.setIcon(ImageUtil.getIcon(contentType));
//      file.setNewFile(true);
//      file.setContent(fileContent);
//      file.setContentChanged(true);
//
//      vfsWebDav.saveContent(file);
//
//      file.setContent(file.getContent() + " " + System.currentTimeMillis());
//      file.setContentChanged(true);
//      vfsWebDav.saveContent(file);
//
//      file.setContent(file.getContent() + " " + System.currentTimeMillis());
//      file.setContentChanged(true);
//      vfsWebDav.saveContent(file);
//
//      eventbus.addHandler(ItemVersionsReceivedEvent.TYPE, new ItemVersionsReceivedHandler()
//      {
//         public void onItemVersionsReceived(ItemVersionsReceivedEvent event)
//         {
//            assertNotNull(event.getVersions());
//            assertNotNull(event.getItem());
//            assertEquals(3, event.getVersions().size());
//            assertNotNull(event.getVersions().get(0).getHref());
//         }
//      });
//
//      vfsWebDav.getVersions(file);
//   }
//
   public void testGetACL()
   {
      final String fileContent = System.currentTimeMillis() + "";
      File file = new File(testUrl + "versionFile");
      file.setContentType("text/plain");
      file.setJcrContentNodeType(NodeTypeUtil.getContentNodeType("text/plain"));
      //     newFile.setIcon(ImageUtil.getIcon(contentType));
      file.setNewFile(true);
      file.setContent(fileContent);
      file.setContentChanged(true);
            
      vfsWebDav.saveContent(file, null, new FileContentSaveCallback()
      {
         @Override
         protected void onSuccess(FileData result)
         {
            vfsWebDav.getProperties(result.getFile(), Arrays.asList(new QName[]{ItemProperty.ACL.ACL}),
               new ItemPropertiesCallback()
               {

                  @Override
                  protected void onSuccess(Item result)
                  {
                     finishTest();
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     fail();
                  }

               });
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            fail();
         }
      });
   }
   
   public void testLockUnmarshaller()
   {
      String xml =
         "<?xml version=\"1.0\" encoding=\"utf-8\" ?><D:prop xmlns:D=\"DAV:\"><D:lockdiscovery>"
            + "<D:activelock><D:locktype><D:write/></D:locktype>"
            + "<D:lockscope><D:exclusive/></D:lockscope><D:depth>Infinity</D:depth>" + "<D:owner>" + "<D:href>"
            + "evgen" + "</D:href>" + "</D:owner>"
            + "<D:timeout>Second-604800</D:timeout>" + " <D:locktoken>" + "<D:href>"
            + "opaquelocktoken:e71d4fae-5dec-22d6-fea5-00a0c91e6be4" + "</D:href>" + "  </D:locktoken>"
            + " </D:activelock>" + "</D:lockdiscovery>" + "</D:prop>";

      LockToken lockToken = new LockToken();
      LockItemUnmarshaller unmarshaller = new LockItemUnmarshaller(lockToken);

      MockResponse response = new MockResponse(xml);
      try
      {
         unmarshaller.unmarshal(response);
      }
      catch (UnmarshallerException e)
      {
         fail(e.getMessage());
         e.printStackTrace();
      }
   }

   
   public void testNullLockUnmarshaller()
   {
      String xml =
         "<?xml version=\"1.0\" encoding=\"utf-8\" ?><D:prop xmlns:D=\"DAV:\"><D:lockdiscovery>"
            + "<D:activelock><D:locktype><D:write/></D:locktype>"
            + "<D:lockscope><D:exclusive/></D:lockscope><D:depth>Infinity</D:depth>" + "<D:owner>" + "<D:href>"
            + "evgen" + "</D:href>" + "</D:owner>"
            + "<D:timeout>Second-604800</D:timeout>"
            + " </D:activelock>" + "</D:lockdiscovery>" + "</D:prop>";

      LockToken lockToken = new LockToken();
      LockItemUnmarshaller unmarshaller = new LockItemUnmarshaller(lockToken);

      MockResponse response = new MockResponse(xml);
      try
      {
         unmarshaller.unmarshal(response);
         fail();
      }
      catch (UnmarshallerException e)
      {
         System.out.println(e.getMessage());
      }
   }
   
   private class MockExceptionThrownHandler implements ExceptionThrownHandler
   {
      public void onError(ExceptionThrownEvent event)
      {
         fail();
      }
   }

}
