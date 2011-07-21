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
package org.exoplatform.ide.extension.jenkins.server;

import org.exoplatform.ide.extension.jenkins.shared.JobStatus;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class JenkinsClient
{
   protected final String baseURL;

   private String jobTemplate;
   private Templates transfomRules;

   public JenkinsClient(String baseURL)
   {
      this.baseURL = baseURL;
   }

   public void createJob(String jobName, String git, String user, String email) throws IOException, JenkinsException
   {
      URL url = new URL(baseURL + "/createItem?name=" + jobName);
      postJob(url, configXml(git, user, email));
   }

   public void updateJob(String jobName, String git, String user, String email) throws IOException, JenkinsException
   {
      URL url = new URL(baseURL + "/job/" + jobName + "/config.xml");
      postJob(url, configXml(git, user, email));
   }

   private void postJob(URL url, String data) throws IOException, JenkinsException
   {
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("POST");
         authenticate(http);
         http.setRequestProperty("Content-type", "application/xml");
         http.setDoOutput(true);
         BufferedWriter wr = null;
         try
         {
            wr = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
            wr.write(data);
            wr.flush();
         }
         finally
         {
            if (wr != null)
               wr.close();
         }

         int responseCode = http.getResponseCode();
         if (responseCode != 200)
            throw fault(http);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

   protected String configXml(String git, String user, String email)
   {
      if (jobTemplate == null)
         loadJobTemplate(); // Load XML template of Jenkins job configuration.
      if (transfomRules == null)
         loadTransformRules(); // Load XSLT transformation rules. 
      try
      {
         Transformer tr = transfomRules.newTransformer();
         tr.setParameter("git-repository", git);
         tr.setParameter("userName", user);
         tr.setParameter("userEmail", email);
         tr.setParameter("mavenName", "Maven 2.2.1");

         StringWriter output = new StringWriter();
         tr.transform(new StreamSource(new StringReader(jobTemplate)), new StreamResult(output));

         return output.toString();
      }
      catch (TransformerConfigurationException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      catch (TransformerException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   private void loadJobTemplate()
   {
      if (jobTemplate != null)
         return;

      synchronized (this)
      {
         if (jobTemplate != null)
            return;

         final String fileName = "jenkins-config.xml";
         InputStream template = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
         if (template == null)
            throw new RuntimeException("Not found jenkins job configuration template. Required file : " + fileName);
         try
         {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(template.available());
            byte[] b = new byte[1024];
            int r;
            while ((r = template.read(b)) != -1)
               bout.write(b, 0, r);
            jobTemplate = bout.toString();
         }
         catch (IOException e)
         {
            throw new RuntimeException("Failed read jenkins job configuration template. " + e.getMessage(), e);
         }
         finally
         {
            try
            {
               template.close();
            }
            catch (IOException ignored)
            {
            }
         }
      }
   }

   private void loadTransformRules()
   {
      if (transfomRules != null)
         return;

      synchronized (this)
      {
         if (transfomRules != null)
            return;

         try
         {
            final String fileName = "jenkins-config.xslt";
            InputStream xsltSource = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (xsltSource == null)
               throw new RuntimeException("File " + fileName + " not found.");
            transfomRules = TransformerFactory.newInstance().newTemplates(new StreamSource(xsltSource));
         }
         catch (TransformerConfigurationException e)
         {
            throw new RuntimeException(e.getMessage(), e);
         }
         catch (TransformerFactoryConfigurationError e)
         {
            throw new RuntimeException(e.getMessage(), e);
         }
      }
   }

   public String getJob(String jobName) throws IOException, JenkinsException
   {
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(baseURL + "/job/" + jobName + "/config.xml");
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode != 200)
            throw fault(http);
         InputStream input = http.getInputStream();
         int contentLength = http.getContentLength();
         String body = null;
         if (input != null)
         {
            try
            {
               body = readBody(input, contentLength);
            }
            finally
            {
               input.close();
            }
         }
         return body;
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

   public void build(String jobName) throws IOException, JenkinsException
   {
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(baseURL + "/job/" + jobName + "/build");
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         int responseCode = http.getResponseCode();
         // Returns 302 if build running. But may return 200 if run build failed, e. g. if user is not authenticated.
         if (responseCode != 302)
            throw fault(http);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

   public JobStatus jobStatus(String jobName) throws IOException, JenkinsException
   {
      if (inQueue(jobName))
         return new JobStatus(jobName, JobStatus.Status.QUEUE, null);

      HttpURLConnection http = null;
      try
      {
         URL url = new URL(baseURL + "/job/" + jobName + "/lastBuild/api/xml" + //
            "?xpath=" + //
            "concat(/mavenModuleSetBuild/building,'%20',/mavenModuleSetBuild/result)");
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode == 404)
         {
            // May be newly created job. Such job does not have last build yet.
            getJob(jobName); // Check job exists or not.
            return new JobStatus(jobName, JobStatus.Status.END, null);
         }
         else if (responseCode != 200)
         {
            throw fault(http);
         }

         InputStream input = http.getInputStream();
         int contentLength = http.getContentLength();
         String body = null;
         if (input != null)
         {
            try
            {
               body = readBody(input, contentLength);
            }
            finally
            {
               input.close();
            }
         }
         if (body != null && body.length() > 0)
         {
            String[] tmp = body.split(" ");
            if (tmp.length == 2)
               return new JobStatus(jobName, JobStatus.Status.END, tmp[1]);
            else if (tmp.length == 1)
               return new JobStatus(jobName, Boolean.parseBoolean(tmp[0]) ? JobStatus.Status.BUILD : null, null);
         }
         throw new RuntimeException("Unable get job status. ");
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

   private boolean inQueue(String jobName) throws IOException, JenkinsException
   {
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(baseURL + "/queue/api/xml" + //
            "?xpath=" + //
            "/queue/item/task/name%3D'" + jobName + "'");
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode != 200)
            throw fault(http);

         InputStream input = http.getInputStream();
         int contentLength = http.getContentLength();
         String body = null;
         if (input != null)
         {
            try
            {
               body = readBody(input, contentLength);
            }
            finally
            {
               input.close();
            }
         }
         return Boolean.parseBoolean(body);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

   protected JenkinsException fault(HttpURLConnection http) throws IOException
   {
      InputStream errorStream = null;
      try
      {
         int responseCode = http.getResponseCode();
         int length = http.getContentLength();
         errorStream = http.getErrorStream();
         if (errorStream == null)
            return new JenkinsException(responseCode, null, null);

         String body = readBody(errorStream, length);

         if (body != null)
            return new JenkinsException(responseCode, body, http.getContentType());

         return new JenkinsException(responseCode, null, null);
      }
      finally
      {
         if (errorStream != null)
            errorStream.close();
      }
   }

   private String readBody(InputStream input, int contentLength) throws IOException
   {
      String body = null;
      if (contentLength > 0)
      {
         byte[] b = new byte[contentLength];
         for (int point = -1, off = 0; (point = input.read(b, off, contentLength - off)) > 0; off += point) //
         ;
         body = new String(b);
      }
      else if (contentLength < 0)
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int point = -1;
         while ((point = input.read(buf)) != -1)
            bout.write(buf, 0, point);
         body = bout.toString();
      }
      return body;
   }

   protected abstract void authenticate(HttpURLConnection http) throws IOException;
}
