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
package org.exoplatform.ideall.client.gadget;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ideall.client.AbstractGwtTest;
import org.exoplatform.ideall.client.TestResponse;
import org.exoplatform.ideall.client.module.gadget.service.GadgetMetadata;
import org.exoplatform.ideall.client.module.gadget.service.TokenRequest;
import org.exoplatform.ideall.client.module.gadget.service.TokenResponse;
import org.exoplatform.ideall.client.module.gadget.service.marshal.GadgetMetadataUnmarshaler;
import org.exoplatform.ideall.client.module.gadget.service.marshal.TokenRequestMarshaler;
import org.exoplatform.ideall.client.module.gadget.service.marshal.TokenResponseUnmarshal;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class GwtTestGagdetTransformers extends AbstractGwtTest
{
   private String gadgetMetadataWithError =
      "{\"gadgets\":[" + "{\"errors\":[\"Unable to retrieve gadget xml. HTTP error 500\"],\"moduleId\":0,"
         + "\"url\":\"http://db2.exoplatform.org:8080/rest/jcr/repository/system/Untitled%20file.xml\"}]}";

   private String gadgetMetadataString =
      "{\"gadgets\":["
         + "{\"userPrefs\":{},\"moduleId\":0,\"screenshot\":\"\","
         + "\"singleton\":false,\"width\":0,\"links\":{},"
         + "\"authorLink\":\"\",\"iframeUrl\":\"?container=&v=6e8eea5e2f5a735c36acddacfe66e88&lang=en&country=US&view=&url=http%3A%2F%2Fdb2.exoplatform.org%3A8080%2Frest%2Fjcr%2Frepository%2Fdev-monit%2FUntitled%2520file.xml\","
         + "\"url\":\"http://db2.exoplatform.org:8080/rest/jcr/repository/dev-monit/Untitled%20file.xml\","
         + "\"scaling\":false,\"title\":\"Hello World!\",\"height\":0,"
         + "\"titleUrl\":\"\",\"thumbnail\":\"\",\"scrolling\":false,"
         + "\"views\":{\"default\":{\"preferredHeight\":0,\"quirks\":true,\"type\":\"html\",\"preferredWidth\":0}},"
         + "\"features\":[],\"showStats\":false,\"categories\":[\"\",\"\"],\"showInDirectory\":false,\"authorPhoto\":\"\"}]}";

   private String securityToken =
      "{\"moduleId\":0,\"securityToken\":\"default:5lNbMmEEmQHoRc3JdvexATXMe5xISqir+HwirpKwdXEFKmlLz0oj3bqGG4kvl7e4yUVORfWjTvcnxBYdljLCaG/ZsLVud4PeZuWx0iMW0ddE3PLbf9BQUUNPxOa9i48l6QKwlA09ZtxMje4SEfnQwGyjle+/YFEhr3J/ylH1Q1YfovnGZYIxhUBSTYm9gDZRX30RKg8YEkHB10ISZbO6rUKMFd6/FoEMk+Zm3FBvpwlHh4eJ\","
         + "\"gadgetURL\":\"http://db2.exoplatform.org:8080/rest/jcr/repository/dev-monit/Untitled%20file.xml\"}";

   /**
    * Test for gadget metadata response unmarshaler.
    */
   public void testGadgetMetadataUnmarshaler()
   {
      GadgetMetadata gadgetMetadata = new GadgetMetadata();
      GadgetMetadataUnmarshaler unmarshaler = new GadgetMetadataUnmarshaler(null, gadgetMetadata);
      TestResponse testResponse = new TestResponse(gadgetMetadataString);
      try
      {
         unmarshaler.unmarshal(testResponse);
         assertNull(gadgetMetadata.getUserPrefs().isArray());
         assertEquals(0.0, gadgetMetadata.getModuleId());
         assertEquals("", gadgetMetadata.getScreenshot());
         assertFalse(gadgetMetadata.isSingleton());
         assertEquals(0.0, gadgetMetadata.getWidth());
         assertNull(gadgetMetadata.getLinks().isArray());
         assertEquals("", gadgetMetadata.getAuthorLink());
         assertEquals(
            "?container=&v=6e8eea5e2f5a735c36acddacfe66e88&lang=en&country=US&view=&url=http%3A%2F%2Fdb2.exoplatform.org%3A8080%2Frest%2Fjcr%2Frepository%2Fdev-monit%2FUntitled%2520file.xml",
            gadgetMetadata.getIframeUrl());
         assertEquals("http://db2.exoplatform.org:8080/rest/jcr/repository/dev-monit/Untitled%20file.xml",
            gadgetMetadata.getUrl());
         assertFalse(gadgetMetadata.isScrolling());
         assertFalse(gadgetMetadata.isScaling());
         assertEquals("Hello World!", gadgetMetadata.getTitle());
         assertEquals(0.0, gadgetMetadata.getHeight());
         assertEquals("", gadgetMetadata.getThumbnail());
         assertEquals("", gadgetMetadata.getTitleUrl());
         assertEquals(1, gadgetMetadata.getViews().size());
         assertEquals(0, gadgetMetadata.getFeatures().length);
         assertFalse(gadgetMetadata.isShowStats());
         assertEquals(2, gadgetMetadata.getCategories().length);
         assertFalse(gadgetMetadata.isShowInDirectory());
         assertEquals("", gadgetMetadata.getAuthorPhoto());
      }
      catch (UnmarshallerException e)
      {
         fail();
      }
   }

   /**
    * Test for gadget metadata with errors response unmarshaler.
    */
   public void testGadgetMetadataWithErrorUnmarshaler()
   {
      GadgetMetadata gadgetMetadata = new GadgetMetadata();
      GadgetMetadataUnmarshaler unmarshaler = new GadgetMetadataUnmarshaler(null, gadgetMetadata);
      TestResponse testResponse = new TestResponse(gadgetMetadataWithError);
      try
      {
         unmarshaler.unmarshal(testResponse);
         assertEquals("http://db2.exoplatform.org:8080/rest/jcr/repository/system/Untitled%20file.xml", gadgetMetadata
            .getUrl());
      }
      catch (UnmarshallerException e)
      {
         fail();
      }
   }

   /**
    * Test marshaler for token request.
    */
   public void testTokenRequestMarshaler()
   {
      String owner = "root";
      String viewer = "root";
      Long moduleId = 0L;
      String container = "default";
      String domain = null;
      String href = "http://db2.exoplatform.org:8080/rest/private/jcr/repository/portal-system/file";
      String gadgetURL = URL.encode(href);
      TokenRequest tokenRequest = new TokenRequest(gadgetURL, owner, viewer, moduleId, container, domain);
      TokenRequestMarshaler marshaler = new TokenRequestMarshaler(tokenRequest);
      try
      {
         JSONValue jsValue = JSONParser.parse(marshaler.marshal());
         assertTrue(jsValue.isObject().containsKey(TokenRequest.OWNER));
         assertEquals(owner, jsValue.isObject().get(TokenRequest.OWNER).isString().stringValue());
         assertTrue(jsValue.isObject().containsKey(TokenRequest.CONTAINER));
         assertEquals(container, jsValue.isObject().get(TokenRequest.CONTAINER).isString().stringValue());
         assertTrue(jsValue.isObject().containsKey(TokenRequest.DOMAIN));
         assertTrue(jsValue.isObject().containsKey(TokenRequest.MODULE_ID));
         assertEquals((double)moduleId, jsValue.isObject().get(TokenRequest.MODULE_ID).isNumber().doubleValue());
         assertTrue(jsValue.isObject().containsKey(TokenRequest.VIEWER));
         assertEquals(viewer, jsValue.isObject().get(TokenRequest.VIEWER).isString().stringValue());
         assertTrue(jsValue.isObject().containsKey(TokenRequest.GADGET_URL));
         assertEquals(gadgetURL, jsValue.isObject().get(TokenRequest.GADGET_URL).isString().stringValue());
      }
      catch (Exception e)
      {
         fail();
      }
   }

   /**
    * Test for token response unmarshaler.
    */
   public void testTokenResponseUnmarshaler()
   {
      TokenResponse tokenResponse = new TokenResponse();
      TokenResponseUnmarshal unmarshaler = new TokenResponseUnmarshal(null, tokenResponse);
      TestResponse response = new TestResponse(securityToken);
      try
      {
         unmarshaler.unmarshal(response);
         assertEquals(
            "default:5lNbMmEEmQHoRc3JdvexATXMe5xISqir+HwirpKwdXEFKmlLz0oj3bqGG4kvl7e4yUVORfWjTvcnxBYdljLCaG/ZsLVud4PeZuWx0iMW0ddE3PLbf9BQUUNPxOa9i48l6QKwlA09ZtxMje4SEfnQwGyjle+/YFEhr3J/ylH1Q1YfovnGZYIxhUBSTYm9gDZRX30RKg8YEkHB10ISZbO6rUKMFd6/FoEMk+Zm3FBvpwlHh4eJ",
            tokenResponse.getSecurityToken());
         assertEquals(new Long(0), tokenResponse.getModuleId());
         assertEquals("http://db2.exoplatform.org:8080/rest/jcr/repository/dev-monit/Untitled%20file.xml",
            tokenResponse.getGadgetURL());
      }
      catch (UnmarshallerException e)
      {
         fail();
      }
   }

}
