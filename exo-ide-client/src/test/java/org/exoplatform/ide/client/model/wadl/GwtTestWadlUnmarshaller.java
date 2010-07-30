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
package org.exoplatform.ideall.client.model.wadl;

import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.ideall.client.AbstractGwtTest;
import org.exoplatform.ideall.client.TestResponse;
import org.exoplatform.ideall.client.module.groovy.service.wadl.marshal.WadlServiceOutputUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class GwtTestWadlUnmarshaller extends AbstractGwtTest
{
   private WadlServiceOutputUnmarshaller unmarshaller;
   
   private HandlerManager eventBus;
   
   private WadlApplication application;
   
   private Response response;
   
   private static final String WADL_XML = 

"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><application xmlns=\"http://research.sun.com/wadl/2006/10\"><resources base=\"http://127.0.0.1:8888/r"
 + "est/private\"><resource path=\"/mine\"><method name=\"OPTIONS\"><response><representation mediaType=\"application/vnd.sun.wadl+xml\"/></response></method><resource pat"
 + "h=\"helloworld/{name}\"><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"template\" name=\"name\"/><method name=\"POST\" id=\"helloWorld\"><req"
 + "uest><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Header1\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type="
 + "\"xs:string\" style=\"query\" name=\"Query1\"/><representation mediaType=\"application/xml\"/></request><response><representation mediaType=\"application/json\"/></respon"
 + "se></method><method name=\"GET\" id=\"hello\"><response><representation mediaType=\"*/*\"/></response></method></resource></resource></resources></application>";
   
// "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><application xmlns=\"http://research.sun.com/wadl/2006/10\"><resources base=\"http://127.0.0.1:8888/r"
// + "est/private\"><resource path=\"/mine\"><method name=\"OPTIONS\"><response><representation mediaType=\"application/vnd.sun.wadl+xml\"/></response></method><resource pat"
// + "h=\"InnerPath/{pathParam}\"><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"template\" name=\"pathParam\"/><method name=\"POST\" id=\"post1\">"
// + "<request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Test-Header1\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSche"
// + "ma\" type=\"xs:string\" style=\"query\" name=\"Test Query Parameter 1\"/><representation mediaType=\"application/json\"/></request><response><representation mediaType=\"t"
// + "ext/html\"/></response></method><method name=\"POST\" id=\"post2\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\""
// + "Test-Header2\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"query\" name=\"Test Query Parameter 2\"/><representation mediaType=\"appli"
// + "cation/xml\"/></request><response><representation mediaType=\"application/json\"/></response></method></resource></resource></resources></application>"
//;
   
   private static final String WRONG_XML = 
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
      + "<application xmlns=\"http://research.sun.com/wadl/2006/10\">"
      + "<resources base=\"http://127.0.0.1:8888/rest/private\">"
         + "<res>wrong xml"
      + "</resources>"
   + "</application>";

   
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventBus = new HandlerManager(null);
      application = new WadlApplication();
      unmarshaller = new WadlServiceOutputUnmarshaller(eventBus, application);
   }
   
   @Override
   protected void gwtTearDown() throws Exception
   {
      super.gwtTearDown();
      eventBus = null;
      application = null;
      unmarshaller = null;
   }
   
   public void testUnmarshaller()
   {
      Header[] headers = {};
      int statusCode = 200;
      String statusText = "OK";
      String text = WADL_XML;
      response = new TestResponse(headers, statusCode, statusText, text);
      try
      {
         unmarshaller.unmarshal(response);
      }
      catch (UnmarshallerException e)
      {
         e.printStackTrace();
         fail();
      }
      assertEquals("http://127.0.0.1:8888/rest/private", application.getResources().getBase());
      assertEquals(1, application.getResources().getResource().size());
      assertEquals("/mine", application.getResources().getResource().get(0).getPath());
      assertEquals(2, application.getResources().getResource().get(0).getMethodOrResource().size());
      List<Object> objects = application.getResources().getResource().get(0).getMethodOrResource();
      for (Object obj : objects)
      {
         //test method OPTIONS
         if (obj instanceof Method)
         {
            Method method = (Method)obj;
            assertEquals("OPTIONS", method.getName());
         }
         //test resource on path "helloworld/{name}" with 2 methods: POST and GET 
         //and one param "name"
         else if (obj instanceof Resource)
         {
            Resource resource = (Resource)obj;
            
            assertEquals("/mine/helloworld/{name}", resource.getPath());
            assertEquals(2, resource.getMethodOrResource().size());
            assertEquals(0, resource.getAny().size());
            assertEquals(0, resource.getDoc().size());
            assertEquals(0, resource.getOtherAttributes().size());
            assertEquals(1, resource.getParam().size());
            assertEquals("name", resource.getParam().get(0).getName());
            assertEquals(0, resource.getType().size());
            
            for (Object object : resource.getMethodOrResource())
            {
               assertTrue(object instanceof Method);
               Method method = (Method) object;
               
               //check POST method
               //this code matchs to the test
               //
               // @POST
               // @Consumes("application/xml")
               // @Produces("application/json")
               // @Path("helloworld/{name}")
               // public String helloWorld(@PathParam("name") String name,
               //   @HeaderParam("Header1") String header1,
               //   @QueryParam("Query1") String query1,
               //   String body) {
               //      return "Hello " + name + " header " + header1 + " query "  + query1;
               // }
               if (method.getName().equals("POST"))
               {
                  assertEquals("application/xml", method.getRequest().getRepresentation().get(0).getMediaType());
                  assertEquals("application/json", method.getResponse().getRepresentationOrFault().get(0).getMediaType());
                  assertEquals(2, method.getRequest().getParam().size());
                  assertEquals(0, method.getResponse().getParam().size());
                  assertEquals("Header1", method.getRequest().getParam().get(0).getName());
                  assertEquals("Query1", method.getRequest().getParam().get(1).getName());
               }
               //check POST method
               //this code matchs to the test
               //
               // @GET
               // @Path("helloworld/{name}")
               // public String hello(@PathParam("name") String name) {
               //   return "Hello " + name
               // }
               else if (method.getName().equals("GET"))
               {
                  assertNull(method.getRequest());
                  assertEquals(0, method.getResponse().getParam().size());
                  assertEquals(1, method.getResponse().getRepresentationOrFault().size());
                  assertEquals("*/*", method.getResponse().getRepresentationOrFault().get(0).getMediaType());
               }
               else
               {
                  fail("Check method name. Expected are POST or GET");
               }
            }
         }
         else
         {
            fail("Object is not method or resource");
         }
      }
   }
   
   public void testUnmarshallerWithNullXml()
   {
      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
      
         public void onError(ExceptionThrownEvent event)
         {
            finishTest();
         }
      });
      
      Header[] headers = {};
      int statusCode = 200;
      String statusText = "OK";
      String text = null;
      response = new TestResponse(headers, statusCode, statusText, text);
      
      delayTestFinish(5000);
      try
      {
         unmarshaller.unmarshal(response);
      }
      catch (UnmarshallerException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }

      assertNull(application.getResources());
      
   }
   
   public void testUnmarshallerWithWrongXml()
   {
      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {

         public void onError(ExceptionThrownEvent event)
         {
            finishTest();
         }
      });

      Header[] headers = {};
      int statusCode = 200;
      String statusText = "OK";
      String text = WRONG_XML;
      response = new TestResponse(headers, statusCode, statusText, text);

      delayTestFinish(5000);
      try
      {
         unmarshaller.unmarshal(response);
      }
      catch (UnmarshallerException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }

      assertNull(application.getResources());
   }

}
