/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.paas.cloudfoundry;

import java.io.File;
import java.net.URL;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.everrest.http.client.HTTPConnection;
import org.everrest.http.client.HTTPResponse;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Utils;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class CloudFoundryTest extends BaseTest
{
   
   public static void resetMockService() {
      try {
         String logoutURL = BASE_URL + REST_CONTEXT + "/ide/cloudfoundry/logout";
         // "http://localhost:8080/" + 
         
         URL url = new URL(logoutURL);
         HTTPConnection connection = Utils.getConnection(url);
         HTTPResponse response = connection.Post(url.getFile());
         System.out.println("STATUS > " + response.getStatusCode());          
      } catch (Exception e) {
         e.printStackTrace();
      }      
   }   
   
   public void uploadResource(String resource, String destination) {
      System.out.println("REQUEST TO UPLOAD RESOURCE!");
      System.out.println("RESOURCE [" + resource + "]");
      System.out.println("DESTINATION [" + destination + "]");
      
      try {
         File f = new File("src/test/resources/org/exoplatform/ide/paas/cloudfoundry/java-spring-project.zip");
         
         String postURL = BASE_URL + REST_CONTEXT + "/ide/upload/folder/";
         System.out.println("> POST [" + postURL + "]");
         
         PostMethod filePost = new PostMethod(postURL);
         //System.out.println("LOCATION [" + WS_URL + TEST_FOLDER + "/java-spring-project.zip" + "]");
         //String location = "http://localhost:8080/rest/private/ide/upload/folder/";
         String location = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + destination;
         
         Part[] parts = {
            //new StringPart("location", WS_URL + TEST_FOLDER + "/java-spring-project.zip"),
            new StringPart("location", location),

            //http://localhost:8080/IDE/rest/private/jcr/repository/dev-monit/aa/WhatsNewPopup.zip
            //http://localhost:8080/IDE/rest/private/jcr/repository/dev-monit/cloudfoundry-test-application-info/java-spring-project.zip
            //http://localhost:8080/IDE/rest/private/jcr/repository/dev-monit/cloudfoundry-test-application-info
            
            new FilePart("file", f)
            //new StringPart("Cookie", cookie)
            };
         filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
         //HttpClient client = Utils.getHttpClient();
         
         HttpClient client = new HttpClient();
         client.getParams().setAuthenticationPreemptive(true);
         Credentials defaultcreds = new UsernamePasswordCredentials(BaseTest. USER_NAME, BaseTest.USER_PASSWORD);
         client.getState().setCredentials(new AuthScope(BaseTest.IDE_HOST, BaseTest.IDE_PORT, AuthScope.ANY_REALM),
            defaultcreds);
         
         int status = client.executeMethod(filePost);
         System.out.println("\r\n\r\n\r\nUPLOAD ZIP STATUS > " + status + "\r\n\r\n\r\n");
         System.out.println("MESSAGE > " + filePost.getStatusText());
         System.out.println("BODY > " + filePost.getResponseBodyAsString());
         
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      
      
      
      
      
      
      
   }

}
