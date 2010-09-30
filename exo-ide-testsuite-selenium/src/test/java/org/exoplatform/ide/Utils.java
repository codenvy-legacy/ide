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
package org.exoplatform.ide;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.exoplatform.common.http.client.CookieModule;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.common.http.client.NVPair;
import org.exoplatform.common.http.client.ParseException;
import org.exoplatform.common.http.client.ProtocolNotSuppException;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class Utils
{
   
   public static final String REALM = "gatein-domain";
   
   public static final String USER = "root";
   
   public static final String PASSWD = "gtn";
   
   public static final String COMMAND =  "/services/groovy/load?state=";
   
   public static HTTPConnection getConnection(URL url) throws ProtocolNotSuppException
   {
      HTTPConnection connection = new HTTPConnection(url);
      connection.setAllowUserInteraction(false);
      connection.removeModule(CookieModule.class);
      connection.addBasicAuthorization(null, USER, PASSWD);
      return connection;
   }
   
   private static int changeServiceState(String baseUrl, String restContext, String location, boolean state) throws IOException, ModuleException
   {
      URL url = new URL(baseUrl + restContext + COMMAND + String.valueOf(state));
      HTTPConnection connection = getConnection(url);
      NVPair[] headers = new NVPair[1];
      headers[0] = new NVPair("location", location);
      HTTPResponse response = connection.Post(url.getFile(),"", headers);
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
   public static int undeployService(String baseUrl, String restContext, String location) throws IOException, ModuleException
   {
      return changeServiceState(baseUrl, restContext, location, false);
   }
   
   /**
    * @param baseUrl
    * @param restContext
    * @param location
    * @return
    * @throws ModuleException 
    * @throws IOException 
    */
   public static int deployService(String baseUrl, String restContext, String location) throws IOException, ModuleException
   {
     return changeServiceState(baseUrl, restContext, location, true);
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
   
   
   

}
