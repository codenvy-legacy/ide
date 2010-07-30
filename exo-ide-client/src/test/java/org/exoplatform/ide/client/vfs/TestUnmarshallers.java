///**
// * Copyright (C) 2009 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// *
// */
//package org.exoplatform.ide.client.vfs;
//
//import java.util.Collection;
//
//import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
//import org.exoplatform.gwtframework.commons.xml.QName;
//import org.exoplatform.ide.client.AbstractGwtTest;
//import org.exoplatform.ide.client.model.vfs.api.Folder;
//import org.exoplatform.ide.client.model.vfs.webdav.marshal.FolderContentUnmarshaller;
//
///**
// * Created by The eXo Platform SAS .
// * 
// * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
// * @version $
// */
//
//public class TestUnmarshallers extends AbstractGwtTest
//{
//
//   private String PROPFIND_RESPONSE = "<?xml version=\"1.0\" ?>" +
//		"<D:multistatus xmlns:D=\"DAV:\">" +
//		   "<D:response>" +
//		      "<D:href>" +
//		         "http://gwt.demo.exoplatform.org/rest/private/jcr/repository/dev-monit/" +
//		      "</D:href>" +
//		      "<D:propstat>" +
//   		      "<D:prop>" +
//   		         "<D:displayname>dev-monit</D:displayname>" +
//   		         //"<D:creationdate b:dt=\"dateTime.tz\">2010-03-17T15:12:04Z</D:creationdate>" +
//   		         "<D:getlastmodified b:dt=\"dateTime.rfc1123\">Thu, 18 Mar 201021:30:12 GMT</D:getlastmodified>" +
//   		         "<D:resourcetype>" +
//   		            "<D:collection />" +
//   		         "</D:resourcetype>" +
//   		         "<jcr:mixinTypes xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">exo:owneable</jcr:mixinTypes>" +
//   		         "<exo:permissions xmlns:exo=\"http://www.exoplatform.com/jcr/exo/1.0\">any read</exo:permissions>" +
//   		         "<exo:owner xmlns:exo=\"http://www.exoplatform.com/jcr/exo/1.0\">__system</exo:owner>" +
//   		      "</D:prop>" +
//               "<D:status>HTTP/1.1 200 OK</D:status>" +
//		      "</D:propstat>" +
//		   "</D:response>" +
//		   
//         "<D:response>" +
//            "<D:href>" +
//               "http://gwt.demo.exoplatform.org/rest/private/jcr/repository/dev-monit/folder1/" +
//            "</D:href>" +
//            "<D:propstat>" +
//               "<D:prop>" +
//                  "<D:displayname>folder1</D:displayname>" +
//                  "<D:resourcetype>" +
//                     "<D:collection />" +
//                  "</D:resourcetype>" +
//                  "<jcr:mixinTypes xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">exo:owneable</jcr:mixinTypes>" +
//                  "<exo:permissions xmlns:exo=\"http://www.exoplatform.com/jcr/exo/1.0\">any read</exo:permissions>" +
//                  "<exo:owner xmlns:exo=\"http://www.exoplatform.com/jcr/exo/1.0\">__system</exo:owner>" +
//               "</D:prop>" +
//               "<D:status>HTTP/1.1 200 OK</D:status>" +
//            "</D:propstat>" +
//         "</D:response>" +
//         
//		"</D:multistatus>";
//
////   private Property getProperty(Collection<Property> properties) {
////   }
//   
//   public void testFolderContentUnmarshaller() throws Exception
//   {
//      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>> TestUnmarshallers.testFolderContentUnmarshaller()");
//
//      Folder folder = new Folder("http://gwt.demo.exoplatform.org/rest/private/jcr/repository/dev-monit/");
//      FolderContentUnmarshaller unmarshaller = new FolderContentUnmarshaller(folder);
//      
//      unmarshaller.unmarshal(PROPFIND_RESPONSE);
//      
//      assertEquals(folder.getChildren().size(), 1);
//      
//      assertEquals(folder.getHref(), "http://gwt.demo.exoplatform.org/rest/private/jcr/repository/dev-monit/");
//
//      for (Property p : folder.getProperties()) {
//         System.out.println("property name: " + p.getName().getLocalName());
//      }
//      
//      System.out.println("displayname property > " + folder.getProperty(new QName("displayname", "DAV:")));
//      
//      assertEquals(folder.getProperty(new QName("displayname", "DAV:")), "dev-monit");
//
//      System.out.println("!!!!! TESTING COMPLETE !!!!!");
//   }
//
//}
