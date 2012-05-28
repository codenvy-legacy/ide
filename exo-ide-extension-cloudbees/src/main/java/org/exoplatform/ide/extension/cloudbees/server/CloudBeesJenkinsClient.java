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
package org.exoplatform.ide.extension.cloudbees.server;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.extension.jenkins.server.JenkinsClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudBeesJenkinsClient extends JenkinsClient
{
   private final String credentials;

   public CloudBeesJenkinsClient(String baseURL, String user, String password) throws UnsupportedEncodingException
   {
      super(baseURL);
      credentials = "Basic " + new String(encodeBase64((user + ":" + password).getBytes("ISO-8859-1")), "ISO-8859-1");
   }

   public CloudBeesJenkinsClient(InitParams initParams) throws UnsupportedEncodingException
   {
      this( //
         readValueParam(initParams, "jenkins-base-url", "https://exoplatform.ci.cloudbees.com"), //
         readValueParam(initParams, "jenkins-user", null), //
         readValueParam(initParams, "jenkins-password", null) //
      );
   }

   private static String readValueParam(InitParams initParams, String paramName, String defaultValue)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
            return vp.getValue();
      }
      return defaultValue;
   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.server.JenkinsClient#authenticate(java.net.HttpURLConnection)
    */
   @Override
   protected void authenticate(HttpURLConnection http) throws IOException
   {
      http.setRequestProperty("Authorization", credentials);
   }
}
