///*
// * Copyright (C) 2010 eXo Platform SAS.
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
// */
//package org.exoplatform.ide.client.model.discovery;
//
//import com.google.gwt.core.client.JavaScriptObject;
//import com.google.gwt.json.client.JSONArray;
//import com.google.gwt.json.client.JSONValue;
//
//import org.exoplatform.ide.client.AbstractGwtTest;
//import org.exoplatform.ide.client.framework.discovery.EntryPoint;
//import org.exoplatform.ide.client.model.discovery.marshal.EntryPointListUnmarshaller;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by The eXo Platform SAS.
// *	
// * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
// * @version $Id:   ${date} ${time}
// *
// */
//public class GwtTestEntryPoint extends AbstractGwtTest
//{
//   private String entryPoints =
//      "{\"entryPoints\":["
//         + "{\"scheme\":\"jcr-webdav\",\"href\":\"http://db2.exoplatform.org:8080/rest/private/jcr/repository/dev-monit/\"},"
//         + "{\"scheme\":\"jcr-webdav\",\"href\":\"http://db2.exoplatform.org:8080/rest/private/jcr/repository/system/\"},"
//         + "{\"scheme\":\"jcr-webdav\",\"href\":\"http://db2.exoplatform.org:8080/rest/private/jcr/repository/portal-system/\"},"
//         + "{\"scheme\":\"jcr-webdav\",\"href\":\"http://db2.exoplatform.org:8080/rest/private/jcr/repository/portal-work/\"},"
//         + "{\"scheme\":\"jcr-webdav\",\"href\":\"http://db2.exoplatform.org:8080/rest/private/jcr/repository/wsrp-system/\"},"
//         + "{\"scheme\":\"jcr-webdav\",\"href\":\"http://db2.exoplatform.org:8080/rest/private/jcr/repository/pc-system/\"}]}";
//
//   /**
//    * Test the unmarshaller for entry points response.
//    */
//   public void testEntryPointUnmarshaller()
//   {
//      JavaScriptObject json = EntryPointListUnmarshaller.build(entryPoints);
//      
//      List<EntryPoint> entryPointList = new ArrayList<EntryPoint>();
//      
//      JSONArray jsonArray = new JSONArray(json);
//      for (int i = 0; i < jsonArray.size(); i++)
//      {
//         JSONValue value = jsonArray.get(i);
//         entryPointList.add(EntryPoint.build(value.toString()));
//      }
//      
//      assertEquals(6, entryPointList.size());
//      EntryPoint entryPoint = entryPointList.get(0);
//      assertEquals("jcr-webdav", entryPoint.getScheme());
//      assertEquals("http://db2.exoplatform.org:8080/rest/private/jcr/repository/dev-monit/", entryPoint.getHref());
//      entryPoint = entryPointList.get(1);
//      assertEquals("jcr-webdav", entryPoint.getScheme());
//      assertEquals("http://db2.exoplatform.org:8080/rest/private/jcr/repository/system/", entryPoint.getHref());
//      entryPoint = entryPointList.get(2);
//      assertEquals("jcr-webdav", entryPoint.getScheme());
//      assertEquals("http://db2.exoplatform.org:8080/rest/private/jcr/repository/portal-system/", entryPoint.getHref());
//      entryPoint = entryPointList.get(3);
//      assertEquals("jcr-webdav", entryPoint.getScheme());
//      assertEquals("http://db2.exoplatform.org:8080/rest/private/jcr/repository/portal-work/", entryPoint.getHref());
//      entryPoint = entryPointList.get(4);
//      assertEquals("jcr-webdav", entryPoint.getScheme());
//      assertEquals("http://db2.exoplatform.org:8080/rest/private/jcr/repository/wsrp-system/", entryPoint.getHref());
//      entryPoint = entryPointList.get(5);
//      assertEquals("jcr-webdav", entryPoint.getScheme());
//      assertEquals("http://db2.exoplatform.org:8080/rest/private/jcr/repository/pc-system/", entryPoint.getHref());
//   }
//}
