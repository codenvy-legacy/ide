///*
// * Copyright (C) 2003-2009 eXo Platform SAS.
// *
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Affero General Public License
// * as published by the Free Software Foundation; either version 3
// * of the License, or (at your option) any later version.
//
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
//
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, see<http://www.gnu.org/licenses/>.
// */
//package org.exoplatform.ideall.client;
//
//import junit.framework.Test;
//import junit.framework.TestSuite;
//
//import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
//import org.exoplatform.gwtframework.commons.loader.Loader;
//import org.exoplatform.ideall.client.model.vfs.webdav.WebDavVirtualFileSystem;
//import org.exoplatform.ideall.client.vfs.TestUnmarshallers;
//
//import com.google.gwt.event.shared.HandlerManager;
//import com.google.gwt.junit.tools.GWTTestSuite;
//
///**
// * Created by The eXo Platform SAS.
// * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
// * @version $Id: $
// */
//public class GwtTestWebDavVirtualFileSystem extends GWTTestSuite
//{
//
//   private HandlerManager eventBus;
//
//   /**
//    * {@inheritDoc}
//    */
//   //   @Override
//   //   public String getModuleName()
//   //   {
//   //      return "org.exoplatform.ideall.IDEGwtTest";
//   //   }
//   public static Test suite()
//   {
//      TestSuite suite = new TestSuite("Test for a WebDav Virtual File System");
//      suite.addTestSuite(TestUnmarshallers.class);
//      //      suite.addTestSuite(EventTest.class);
//      //      suite.addTestSuite(CopyTest.class);
//      return suite;
//   }
//
//   public GwtTestWebDavVirtualFileSystem()
//   {
//      System.out.println("GwtTestWebDavVirtualFileSystem.GwtTestWebDavVirtualFileSystem()");
//      eventBus = new HandlerManager(null);
//      Loader loader = new EmptyLoader();
//      new WebDavVirtualFileSystem(eventBus, loader);
//   }
//
//   //   public void testPropfind() {
//   //      System.out.println("GwtTestWebDavVirtualFileSystem.testPropfind()");
//   //      
//   //      TestUnmarshallers.testFolderContentUnmarshaller();
//   //      
//   //   }
//
//   //   public void testGetChildren()
//   //   {
//   //      System.out.println("GwtTestWebDavVirtualFileSystem.testGetChildren()");
//   //      
//   ////      Configuration c = new Configuration(eventBus);
//   ////
//   ////      try {
//   ////         //c.setContext("org.exoplatform.ideall.IDEApplication/testwebdav");
//   ////         c.setContext("testwebdav");
//   ////
//   ////         String path = "/testwebdav/asdfasd";
//   ////         Folder folder = new Folder(path);
//   ////         VirtualFileSystem.getInstance().getChildren(folder);         
//   ////      } catch (Exception exc) {
//   ////         exc.printStackTrace();
//   ////      }
//   //      
//   //      AsyncRequestCallback callback = new AsyncRequestCallback(new HandlerManager(null), new MockEvent());
//   //      AsyncRequest.build(RequestBuilder.GET, "testwebdav/alala").send(callback);      
//   //   }
//
//}
