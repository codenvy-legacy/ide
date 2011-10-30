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
import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.services.security.ConversationState;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class JenkinsClient
{
   protected class HttpStream extends InputStream
   {
      private final HttpURLConnection http;

      private InputStream in;

      private boolean closed;

      public HttpStream(HttpURLConnection http)
      {
         this.http = http;
      }

      public int read() throws IOException
      {
         if (closed)
            return -1;
         if (in == null)
            in = http.getInputStream();
         int i = in.read();
         if (i == -1)
         {
            if (!closed)
            {
               try
               {
                  close();
               }
               catch (IOException ignored)
               {
                  // Ignore errors occurs when try close.
               }
            }
         }
         return i;
      }

      public void close() throws IOException
      {
         in.close();
         http.disconnect();
         closed = true;
      }

      @Override
      protected void finalize() throws Throwable
      {
         if (!closed)
         {
            try
            {
               close();
            }
            catch (IOException ignored)
            {
               // Ignore errors occurs when try close.
            }
         }
         super.finalize();
      }
   }

   protected final String baseURL;

   private String jobTemplate;

   private Templates transfomRules;

   public JenkinsClient(String baseURL)
   {
      this.baseURL = baseURL;
   }

   public void createJob(String jobName, String git, String user, String email, VirtualFileSystem vfs, String projectId)
      throws IOException, JenkinsException, VirtualFileSystemException
   {
      URL url = new URL(baseURL + "/createItem?name=" + jobName);
      postJob(url, configXml(git, user, email));
      if (vfs != null && projectId != null)
      {
         writeJenkinsJobName(vfs, projectId, jobName);
      }
   }

   public void updateJob(String jobName, String git, String user, String email, VirtualFileSystem vfs, String projectId)
      throws IOException, JenkinsException, VirtualFileSystemException
   {
      if ((jobName == null || jobName.isEmpty()))
      {
         jobName = readJenkinsJobName(vfs, projectId);
      }
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
         if (responseCode == 404)
            throw new JenkinsException(404, "Job '" + jobName + "' not found.\n", "text/plain");
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

   public void build(String jobName, VirtualFileSystem vfs, String projectId) throws IOException, JenkinsException,
      VirtualFileSystemException
   {
      ConversationState userState = ConversationState.getCurrent();
      if (userState != null)
      {
         String lastJob = (String)userState.getAttribute("org.exoplatform.ide.jenkins.job");
         if (lastJob != null)
         {
            try
            {
               JobStatus lastJobStatus = jobStatus(lastJob, null, null);
               if (JobStatus.Status.END == lastJobStatus.getStatus())
                  userState.removeAttribute("org.exoplatform.ide.jenkins.job");
               else
                  throw new JenkinsException(400, "Build job '" + lastJob
                     + "' in progress. Not allowed have more then one build at the same time. ", "text/plain");
            }
            catch (JenkinsException e)
            {
               // Do nothing if job does not exist.
               if (e.getResponseStatus() != 404)
                  throw e;
            }
         }
      }

      if (jobName == null || jobName.isEmpty())
      {
         jobName = readJenkinsJobName(vfs, projectId);
      }
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
      if (userState != null)
         userState.setAttribute("org.exoplatform.ide.jenkins.job", jobName);
   }

   public JobStatus jobStatus(String jobName, VirtualFileSystem vfs, String projectId) throws IOException,
      JenkinsException, VirtualFileSystemException
   {
      if (jobName == null || jobName.isEmpty())
      {
         jobName = readJenkinsJobName(vfs, projectId);
      }

      if (inQueue(jobName))
         return new JobStatus(jobName, JobStatus.Status.QUEUE, null, null);

      HttpURLConnection http = null;
      try
      {
         final String root = "status";
         String xpathRequest = new StringBuilder() //
            .append("/mavenModuleSetBuild/building") //
            .append("|/mavenModuleSetBuild/url") //
            .append("|/mavenModuleSetBuild/result") //
            .append("|/mavenModuleSetBuild/artifact") //
            .toString();
         URL url = new URL(baseURL + "/job/" + jobName + "/lastBuild/api/xml" //
            + "?xpath=" + xpathRequest //
            + "&wrapper=" + root);

         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode == 404)
         {
            // May be newly created job. Such job does not have last build yet.
            getJob(jobName); // Check job exists or not.
            return new JobStatus(jobName, JobStatus.Status.END, null, null);
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
            try
            {
               DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
               Document doc = df.newDocumentBuilder().parse(new InputSource(new StringReader(body)));
               XPath xpath = XPathFactory.newInstance().newXPath();
               String building = xpath.evaluate("/" + root + "/building", doc);

               if (Boolean.parseBoolean(building)) // Building in progress.
                  return new JobStatus(jobName, JobStatus.Status.BUILD, null, null);

               String result = xpath.evaluate("/" + root + "/result", doc);
               String buildUrl = xpath.evaluate("/" + root + "/url", doc);
               if ("SUCCESS".equals(result))
               {
                  // If build successful provide URL to download artifact.

                  // Be sure only one artifact provided.
                  int artifacts =
                     ((Double)xpath.evaluate("count(/" + root + "/artifact)", doc, XPathConstants.NUMBER)).intValue();

                  if (artifacts == 1)
                  {
                     // Good, only one artifact tag.
                     String relativePath = xpath.evaluate("/" + root + "/artifact/relativePath", doc);
                     return new JobStatus(jobName, JobStatus.Status.END, result, buildUrl + "artifact/" + relativePath);
                  }
               }
               // Cannot provide URL for download, e.g. build failed, canceled, etc.
               return new JobStatus(jobName, JobStatus.Status.END, result, null);
            }
            catch (SAXException e)
            {
               throw new RuntimeException(e.getMessage(), e);
            }
            catch (IOException e)
            {
               throw new RuntimeException(e.getMessage(), e);
            }
            catch (ParserConfigurationException e)
            {
               throw new RuntimeException(e.getMessage(), e);
            }
            catch (XPathExpressionException e)
            {
               throw new RuntimeException(e.getMessage(), e);
            }
         }
         // Unexpected or empty result from Jenkins.
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

   public InputStream consoleOutput(String jobName, VirtualFileSystem vfs, String projectId) throws IOException,
      JenkinsException, VirtualFileSystemException
   {
      if (jobName == null || jobName.isEmpty())
      {
         jobName = readJenkinsJobName(vfs, projectId);
      }

      if (jobStatus(jobName, vfs, projectId).getStatus() != JobStatus.Status.END)
         return null; // Do not show output if job in queue for build or building now.

      HttpURLConnection http = null;
      try
      {
         URL url = new URL(baseURL + "/job/" + jobName + "/lastBuild/consoleText");
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode == 404)
            return null; // Job exists (checked before) but there is no last-build yet. 
         if (responseCode != 200)
            throw fault(http);
         return new HttpStream(http);
      }
      catch (IOException e)
      {
         if (http != null)
            http.disconnect();
         throw e;
      }
      catch (JenkinsException e)
      {
         if (http != null)
            http.disconnect();
         throw e;
      }
   }

   public void deleteJob(String jobName, VirtualFileSystem vfs, String projectId) throws IOException, JenkinsException,
      VirtualFileSystemException
   {
      if (jobName == null || jobName.isEmpty())
      {
         jobName = readJenkinsJobName(vfs, projectId);
      }
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(baseURL + "/job/" + jobName + "/doDelete");
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("POST");
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode != 302)
            throw fault(http);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
      if (vfs != null && projectId != null)
      {
         writeJenkinsJobName(vfs, projectId, null);
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

   private void writeJenkinsJobName(VirtualFileSystem vfs, String projectId, String jobName)
      throws VirtualFileSystemException
   {
      ConvertibleProperty p = new ConvertibleProperty("jenkins-job", jobName);
      List<ConvertibleProperty> properties = new ArrayList<ConvertibleProperty>(1);
      properties.add(p);
      vfs.updateItem(projectId, properties, null);
   }

   private String readJenkinsJobName(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException
   {
      Item item = vfs.getItem(projectId, PropertyFilter.valueOf("jenkins-job"));
      String job = (String)item.getPropertyValue("jenkins-job");
      if (job == null || job.isEmpty())
         throw new RuntimeException("Job name required. ");
      return job;
   }
}
