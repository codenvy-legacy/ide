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
package org.exoplatform.ide;

import static org.junit.Assert.fail;

import org.exoplatform.common.http.client.CookieModule;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.common.http.client.NVPair;
import org.exoplatform.common.http.client.ProtocolNotSuppException;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class Utils
{

   public static final String USER = "root";

   public static final String PASSWD = "gtn";

   public static final String COMMAND = "/ide/groovy/";

   public static HTTPConnection getConnection(URL url) throws ProtocolNotSuppException
   {
      HTTPConnection connection = new HTTPConnection(url);
      connection.setAllowUserInteraction(false);
      connection.removeModule(CookieModule.class);
      connection.addBasicAuthorization(null, USER, PASSWD);
      return connection;
   }

   private static int changeServiceState(String baseUrl, String restContext, String location, String state)
      throws IOException, ModuleException
   {
      URL url = new URL(baseUrl + restContext + COMMAND + state);
      HTTPConnection connection = getConnection(url);
      NVPair[] headers = new NVPair[2];
      headers[0] = new NVPair("Location", location);
      headers[1] = new NVPair(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_FORM_URLENCODED);
      HTTPResponse response = connection.Post(url.getFile(), "", headers);
      return response.getStatusCode();
   }

   /**
    * @param baseUrl
    * @param restContext
    * @param location
    * @return
    * @throws ModuleException 
    * @throws IOException 
    */
   public static int undeployService(String baseUrl, String restContext, String location) throws IOException,
      ModuleException
   {
      return changeServiceState(baseUrl, restContext, location, "undeploy");
   }

   /**
    * @param baseUrl
    * @param restContext
    * @param location
    * @return
    * @throws ModuleException 
    * @throws IOException 
    */
   public static int deployService(String baseUrl, String restContext, String location) throws IOException,
      ModuleException
   {
      return changeServiceState(baseUrl, restContext, location, "deploy");
   }

   public static String readFileAsString(String filePath) throws java.io.IOException
   {
      StringBuffer fileData = new StringBuffer(1000);
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      char[] buf = new char[1024];
      int numRead = 0;
      while ((numRead = reader.read(buf)) != -1)
      {
         String readData = String.valueOf(buf, 0, numRead);
         fileData.append(readData);
         buf = new char[1024];
      }
      reader.close();
      return fileData.toString();
   }

   /**
    * Encode URL(Add /IDE/ to path) string in md5 hash 
    * @param href to encode
    * @return md5 hash of string
    */
   public static String md5(String href)
   {
      //encode href
      String segment = href.substring(BaseTest.ENTRY_POINT_URL.length());
      if (segment.startsWith("/"))
      {
         segment = segment.substring(1);
      }
      if (segment.endsWith("/"))
      {
         segment = segment.substring(0, segment.length() - 1);
      }
      String[] pathSegments = segment.split("/");
      String encoded = BaseTest.ENTRY_POINT_URL;
      if (encoded.endsWith("/"))
      {
         encoded = encoded.substring(0, encoded.length() - 1);
      }
      for (int i = 0; i < pathSegments.length; i++)
      {
         pathSegments[i] = BaseTest.selenium.getEval("encodeURIComponent('" + pathSegments[i] + "')");
         encoded += "/" + pathSegments[i];
      }
      if (href.endsWith("/"))
      {
         encoded += "/";
      }
      MessageDigest m;
      try
      {
         m = MessageDigest.getInstance("MD5");
         m.reset();
         //add /IDE/ path segment to URL be equals with client URL 

         if (BaseTest.isRunIdeAsShell())
         {
            m.update(href.getBytes());
         }
         else
         {
            m.update((BaseTest.BASE_URL + "IDE/" + href.substring(BaseTest.BASE_URL.length())).getBytes());            
         }
         
         byte[] digest = m.digest();
         BigInteger bigInt = new BigInteger(1, digest);
         String hashtext = bigInt.toString(16);
         // Now we need to zero pad it if you actually want the full 32 chars.
         while (hashtext.length() < 32)
         {
            hashtext = "0" + hashtext;
         }
         return hashtext;
      }
      catch (NoSuchAlgorithmException e)
      {
         e.printStackTrace();
         fail();
      }
      return "";

   }
   
   /**
    * Encode string in md5 hash
    * @param string to encode
    * @return md5 hash of string
    */
   public static String md5old(String string)
   {
      MessageDigest m;
      try
      {
         m = MessageDigest.getInstance("MD5");
         m.reset();
         m.update(string.getBytes());
         byte[] digest = m.digest();
         BigInteger bigInt = new BigInteger(1, digest);
         String hashtext = bigInt.toString(16);
         // Now we need to zero pad it if you actually want the full 32 chars.
         while (hashtext.length() < 32)
         {
            hashtext = "0" + hashtext;
         }
         return hashtext;
      }
      catch (NoSuchAlgorithmException e)
      {
         e.printStackTrace();
         fail();
      }
      return "";

   }

}
