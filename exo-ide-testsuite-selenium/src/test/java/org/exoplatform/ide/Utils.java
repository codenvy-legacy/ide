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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.everrest.http.client.ModuleException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
   public static final String COMMAND = "/ide/groovy/";

   public static HttpURLConnection getConnection(URL url) throws IOException
   {
      login();

      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setAllowUserInteraction(false);
      return connection;
   }

   private static int changeServiceState(String baseUrl, String restContext, String location, String state)
      throws IOException
   {
      int status = -1;
      HttpURLConnection connection = null;
      try
      {
         URL url = new URL(baseUrl + restContext + COMMAND + state);
         connection = getConnection(url);
         connection.setRequestMethod("POST");
         connection.setRequestProperty("location", location);
         connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         status = connection.getResponseCode();
      }
      finally
      {
         if (connection != null)
         {
            connection.disconnect();
         }
      }
      return status;
   }

   public static HttpClient getHttpClient()
   {
      HttpClient client = new HttpClient();
      client.getParams().setAuthenticationPreemptive(true);
      Credentials defaultcreds = new UsernamePasswordCredentials(BaseTest.USER_NAME, BaseTest.USER_PASSWORD);
      client.getState().setCredentials(new AuthScope(BaseTest.IDE_HOST, BaseTest.IDE_PORT, AuthScope.ANY_REALM),
         defaultcreds);
      return client;
   }

   /**
    * @param baseUrl
    * @param restContext
    * @param location
    * @return
    * @throws ModuleException 
    * @throws IOException 
    */
   public static int undeployService(String baseUrl, String restContext, String location) throws IOException
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
   public static int deployService(String baseUrl, String restContext, String location) throws IOException
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
      MessageDigest m;
      try
      {
         m = MessageDigest.getInstance("MD5");
         m.reset();
         m.update(href.getBytes());

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

   private static void login() throws IOException
   {
      if (CookieHandler.getDefault() == null)
         CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

      if (isLogged())
         return;
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)new URL(BaseTest.LOGIN_URL).openConnection();
         http.setRequestMethod("POST");
         http.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
         http.setDoOutput(true);
         OutputStream output = http.getOutputStream();
         output.write(("j_username=" + BaseTest.USER_NAME + "&j_password=" + BaseTest.USER_PASSWORD).getBytes());
         output.close();
         http.getResponseCode();
      }
      catch (MalformedURLException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      finally
      {
         http.disconnect();
      }
   }

   private static boolean isLogged()
   {
      HttpURLConnection http = null;
      BufferedReader reader = null;
      try
      {
         http = (HttpURLConnection)new URL(BaseTest.APPLICATION_URL).openConnection();
         http.setRequestMethod("GET");
         http.getResponseCode();

         InputStream in = http.getInputStream();

         reader = new BufferedReader(new InputStreamReader(in));
         StringBuilder sb = new StringBuilder();

         String line = null;
         while ((line = reader.readLine()) != null)
         {
            sb.append(line);
            sb.append('\n');
         }
         if (reader != null)
         {
            reader.close();
         }
         in.close();
         return !sb.toString().contains("loginForm");
      }
      catch (MalformedURLException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }
      return false;
   }

}
