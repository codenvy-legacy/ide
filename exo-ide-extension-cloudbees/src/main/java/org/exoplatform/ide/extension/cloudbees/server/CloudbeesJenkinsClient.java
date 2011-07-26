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

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.extension.jenkins.server.JenkinsClient;
import org.exoplatform.ide.extension.jenkins.server.JenkinsException;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudbeesJenkinsClient extends JenkinsClient
{
   private final String loginURL;
   private final String authURL;
   private final byte[] authForm;

   protected CloudbeesJenkinsClient(String baseURL, String user, String password)
   {
      super(baseURL);
      String tmp = baseURL;
      if (!tmp.endsWith("/"))
         tmp += "/";
      authURL = "https://sso.cloudbees.com/sso-gateway/signon/usernamePasswordLogin.do?josso_back_to=" + tmp;
      loginURL = "https://sso.cloudbees.com/sso-gateway/signon/login.do?josso_back_to=" + tmp;
      authForm = ("josso_username=" + user + "&josso_password=" + password + "&josso_cmd=login").getBytes();

      // Need to be able save cookies after authentication.
      if (CookieHandler.getDefault() == null)
         CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
   }

   public CloudbeesJenkinsClient(InitParams initParams)
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

   @Override
   public void createJob(String jobName, String git, String user, String email, File workDir) throws IOException,
      JenkinsException
   {
      doLogin();
      super.createJob(jobName, git, user, email, workDir);
   }

   @Override
   public void updateJob(String jobName, String git, String user, String email, File workDir) throws IOException,
      JenkinsException
   {
      doLogin();
      super.updateJob(jobName, git, user, email, workDir);
   }

   @Override
   public String getJob(String jobName) throws IOException, JenkinsException
   {
      doLogin();
      return super.getJob(jobName);
   }

   @Override
   public void build(String jobName, File workDir) throws IOException, JenkinsException
   {
      doLogin();
      super.build(jobName, workDir);
   }

   @Override
   public JobStatus jobStatus(String jobName, File workDir) throws IOException, JenkinsException
   {
      doLogin();
      return super.jobStatus(jobName, workDir);
   }

   @Override
   public InputStream consoleOutput(String jobName, File workDir) throws IOException, JenkinsException
   {
      doLogin();
      return super.consoleOutput(jobName, workDir);
   }

   private synchronized void doLogin() throws IOException, JenkinsException
   {
      // Since Jenkins does not provide 401 HTTP status when user need authenticated
      // and even does not provide 403 when try access protected resources we need check
      // login page each time to be sure cookies what we have still valid.
      HttpURLConnection http = null;
      int responseCode = 0;
      boolean loggedIn = false;

      try
      {
         http = (HttpURLConnection)new URL(loginURL).openConnection();
         http.setRequestMethod("GET");
         responseCode = http.getResponseCode();
         if (!(responseCode == 200 || responseCode == 302))
            throw fault(http);
         // If not logged in yet then we stay at the same page what we are requested.
         // Otherwise redirected to Jenkins 'dashboard' page (baseURL). 
         loggedIn = !(http.getURL().toString().equals(loginURL));
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }

      if (!loggedIn)
      {
         http = null;
         responseCode = 0;
         try
         {
            http = (HttpURLConnection)new URL(authURL).openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            OutputStream output = http.getOutputStream();
            output.write(authForm);
            output.close();
            responseCode = http.getResponseCode();
            if (!(responseCode == 200 || responseCode == 302))
               throw fault(http);
         }
         finally
         {
            if (http != null)
               http.disconnect();
         }
      }
   }

   /**
    * @see org.exoplatfrom.ide.extension.jenkins.server.JenkinsClient#authenticate(java.net.HttpURLConnection)
    */
   @Override
   protected void authenticate(HttpURLConnection http) throws IOException
   {
      // Nothing to do here since form authentication
   }
}
