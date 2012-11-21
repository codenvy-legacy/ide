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

import javax.net.ssl.HttpsURLConnection;

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
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
      if (BaseTest.IDE_HOST.contains("localhost"))
      {
         standaloneLogin();
      }
      else
      {
         login();
      }
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestProperty("Referer", url.toString());
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
         fail();
      }
      return "";

   }

   //login for standalone
   /**
    * login on standalone bundle
    * @throws IOException
    */
   private static void standaloneLogin() throws IOException
   {
      if (CookieHandler.getDefault() == null)
         CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

      if (isStandaloneLogged())
         return;
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)new URL(BaseTest.STANDALONE_LOGIN_URL).openConnection();
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
      }
      catch (IOException e)
      {
      }
      finally
      {
         http.disconnect();
      }
   }

   /**
    * check user login or nut on standalone
    * @return
    */
   private static boolean isStandaloneLogged()
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
         return !sb.toString().contains("loginFormId");
      }
      catch (MalformedURLException e)
      {
      }
      catch (IOException e)
      {
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

   // login for bundles cloud-ide with https (cloudtest, cloudtest2, staging and production servers)
   // workaround: we make 5 attempts to login, until we become authorized on the server 
   /**
    * login for test servers, production server and staging 
    */
   private static void login()// throws IOException
   {
      CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
      if (isLogged())
         return;
      HttpsURLConnection https1 = null;
      HttpsURLConnection https2 = null;
      HttpsURLConnection https3 = null;
      HttpURLConnection http4 = null;
      HttpURLConnection https5 = null;
      HttpURLConnection https6 = null;
      HttpURLConnection http = null;

      try
      {
         String lOGIN_URL =
            BaseTest.LOGIN_URL + "cloud/ide.jsp?username=" + BaseTest.USER_NAME + "&password=" + BaseTest.USER_PASSWORD;
         System.err.println(lOGIN_URL);
         https1 = (HttpsURLConnection)new URL(lOGIN_URL).openConnection();
         https1.setRequestMethod("GET");
         https1.setAllowUserInteraction(false);
         https1.setInstanceFollowRedirects(true);
         Map<String, List<String>> headerFields = https1.getHeaderFields();
         Set<String> keySet = headerFields.keySet();

         for (String key : keySet)
         {
            List<String> vals = headerFields.get(key);
            for (String string : vals)
            {
               System.out.println("               " + key + " :: " + string);
            }
         }
         System.err.println("     >>>>>>         " + https1.getResponseCode());
         System.err.println("-----------------------------------------------------");

         if (https1.getResponseCode() == 302)
         {
            String rediString = https1.getHeaderField("Location");
            System.out.println("Now go to https1 : " + rediString);
            http = (HttpURLConnection)new URL(rediString).openConnection();
         }

         http.setRequestMethod("GET");
         http.setAllowUserInteraction(false);
         http.setInstanceFollowRedirects(true);
         headerFields = http.getHeaderFields();
         keySet = headerFields.keySet();
         for (String key : keySet)
         {
            List<String> vals = headerFields.get(key);
            for (String string : vals)
            {
               System.out.println("               " + key + " :: " + string);
            }
         }

         System.err.println("-----------------------------------------------------");
         if (http.getResponseCode() == 302)
         {
            String rediString = http.getHeaderField("Location");
            System.out.println("Now go to http: " + rediString);
            https2 = (HttpsURLConnection)new URL(rediString).openConnection();
         }
         //------------------------------------------------3         
         https2.setRequestMethod("GET");
         https2.setAllowUserInteraction(false);
         https2.setInstanceFollowRedirects(true);
         headerFields = https2.getHeaderFields();
         keySet = headerFields.keySet();
         for (String key : keySet)
         {
            List<String> vals = headerFields.get(key);
            for (String string : vals)
            {
               System.out.println("               " + key + " :: " + string);
            }
         }
         if (http.getResponseCode() == 302)
         {
            String rediString = http.getHeaderField("Location");
            System.out.println("Now go to https2: " + rediString);
            https3 = (HttpsURLConnection)new URL(rediString).openConnection();
         }

         //--------------------------------------------4        
         https3.setRequestMethod("GET");
         https3.setAllowUserInteraction(false);
         https3.setInstanceFollowRedirects(true);
         headerFields = https3.getHeaderFields();
         keySet = headerFields.keySet();
         for (String key : keySet)
         {
            List<String> vals = headerFields.get(key);
            for (String string : vals)
            {
               System.out.println("               " + key + " :: " + string);
            }
         }
         if (https3.getResponseCode() == 302)
         {
            String rediString = https3.getHeaderField("Location");
            System.out.println("Now go to https3: " + rediString);
            http4 = (HttpURLConnection)new URL(rediString).openConnection();
         }

         //-----------------------------------------------
         http4.setRequestMethod("GET");
         http4.setAllowUserInteraction(false);
         http4.setInstanceFollowRedirects(true);
         headerFields = http4.getHeaderFields();
         keySet = headerFields.keySet();
         for (String key : keySet)
         {
            List<String> vals = headerFields.get(key);
            for (String string : vals)
            {
               System.out.println("               " + key + " :: " + string);
            }
         }
         if (http4.getResponseCode() == 302)
         {
            String rediString = http4.getHeaderField("Location");
            System.out.println("Now go to https4: " + rediString);
            https5 = (HttpURLConnection)new URL(rediString).openConnection();
         }

         //-----------------------------------------------
         https5.setRequestMethod("GET");
         https5.setAllowUserInteraction(false);
         https5.setInstanceFollowRedirects(true);
         headerFields = https5.getHeaderFields();
         keySet = headerFields.keySet();
         for (String key : keySet)
         {
            List<String> vals = headerFields.get(key);
            for (String string : vals)
            {
               System.out.println("               " + key + " :: " + string);
            }
         }
         if (https5.getResponseCode() == 302)
         {
            String rediString = https5.getHeaderField("Location");
            System.out.println("Now go to https5: " + rediString);
            https6 = (HttpURLConnection)new URL(rediString).openConnection();
         }

         //-----------------------------------------------
         https6.setRequestMethod("GET");
         https6.setAllowUserInteraction(false);
         https6.setInstanceFollowRedirects(true);
         headerFields = https6.getHeaderFields();
         keySet = headerFields.keySet();
         for (String key : keySet)
         {
            List<String> vals = headerFields.get(key);
            for (String string : vals)
            {
               System.out.println("               " + key + " :: " + string);
            }
         }
         if (https6.getResponseCode() == 302)
         {
            String rediString = https6.getHeaderField("Location");
            System.out.println("Now go to https6: " + rediString);

         }

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
         System.out.println("++++++++++++++++++++ https1 " + https1.getURL());
         System.out.println("++++++++++++++++++++ http " + http.getURL());
         System.out.println("++++++++++++++++++++ https2 " + https2.getURL());
         System.out.println("++++++++++++++++++++ https3 " + https3.getURL());
         System.out.println("++++++++++++++++++++ http4 " + http4.getURL());
         System.out.println("++++++++++++++++++++ https5 " + https5.getURL());
         System.out.println("++++++++++++++++++++ https6 " + https6.getURL());

         if (https1 != null)
            https1.disconnect();

         if (http != null)
            http.disconnect();

         if (https2 != null)
            https2.disconnect();

         if (https3 != null)
            https3.disconnect();

         if (http4 != null)
            http4.disconnect();

         if (https5 != null)
            https5.disconnect();

         if (https6 != null)
            https6.disconnect();

      }

   }

   /**
    * check login user on test servers, production server and staging
    * @return
    */
   private static boolean isLogged()
   {
      HttpsURLConnection https = null;
      BufferedReader reader = null;
      try
      {
         // URL wsURL = new URL(BaseTest.APPLICATION_URL);

         https = (HttpsURLConnection)new URL(BaseTest.LOGIN_URL + "cloud/ide.jsp").openConnection();
         https.setRequestMethod("GET");
         InputStream in = https.getInputStream();
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
         return !sb.toString().contains("loginFormId");
      }
      catch (MalformedURLException e)
      {
      }
      catch (IOException e)
      {
      }
      finally
      {
         if (https != null)
         {
            https.disconnect();
         }
      }
      return false;
   }

}
