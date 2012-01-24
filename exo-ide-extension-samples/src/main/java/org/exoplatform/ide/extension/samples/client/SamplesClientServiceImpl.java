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
package org.exoplatform.ide.extension.samples.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudfoundryApplication;
import org.exoplatform.ide.extension.samples.client.paas.heroku.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.marshal.CredentailsMarshaller;
import org.exoplatform.ide.extension.samples.shared.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation for {@link SamplesClientService}.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesClientServiceImpl.java Sep 2, 2011 12:34:27 PM vereshchaka $
 * 
 */
public class SamplesClientServiceImpl extends SamplesClientService
{
   private static final String BASE_URL = "/ide/github";

   private static final String LIST = BASE_URL + "/list";

   private static final String LIST_USER = BASE_URL + "/list/user";

   /** CloudBees **/

   private static final String CLOUDBEES_DOMAINS = "/ide/cloudbees/domains";

   private static final String CLOUDBEES_CREATE = "/ide/cloudbees/apps/create";

   private static final String CLOUDBEES_LOGIN = "/ide/cloudbees/login";

   /** CloudFoundry **/

   private static final String CLOUDFOUNDRY_LOGIN = "/ide/cloudfoundry/login";

   private static final String VALIDATE_ACTION = "/ide/cloudfoundry/apps/validate-action";

   private static final String CLOUDFOUNDRY_CREATE = "/ide/cloudfoundry/apps/create";

   private static final String CF_TARGETS = "/ide/cloudfoundry/target/all";

   /** OpenShift **/

   private static final String OPENSHIFT_TYPES = "/ide/openshift/express/apps/type";

   private static final String OPENSHIFT_CREATE = "/ide/openshift/express/apps/create";

   private static final String OPENSHIFT_LOGIN = "/ide/openshift/express/login";

   /** Heroku **/
   private static final String HEROKU_CREATE = "/ide/heroku/apps/create";

   private static final String HEROKU_LOGIN = "/ide/heroku/login";

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   public static final String SUPPORT = "support";

   public SamplesClientServiceImpl(String restContext, Loader loader)
   {
      this.loader = loader;
      this.restServiceContext = restContext;
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#getRepositoriesList(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getRepositoriesList(AsyncRequestCallback<List<Repository>> callback) throws RequestException
   {
      String url = restServiceContext + LIST;
      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#getRepositoriesList(java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getRepositoriesList(String userName, AsyncRequestCallback<List<Repository>> callback)
      throws RequestException
   {
      String url = restServiceContext + LIST_USER + "?username=" + userName;
      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#createCloudBeesApplication(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String,
    *      org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void createCloudBeesApplication(String appId, String vfsId, String projectId, String warFile, String message,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback) throws RequestException
   {
      final String url = restServiceContext + CLOUDBEES_CREATE;

      String params = "appid=" + appId + "&";
      params += "war=" + warFile;
      params += "&vfsid=" + vfsId + "&projectid=" + projectId;
      if (message != null && !message.isEmpty())
         params += "&message=" + message;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#getDomains(org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void getDomains(CloudBeesAsyncRequestCallback<List<String>> callback) throws RequestException
   {
      final String url = restServiceContext + CLOUDBEES_DOMAINS;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#loginToCloudBees(java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void loginToCloudBees(String email, String password, AsyncRequestCallback<String> callback)
      throws RequestException
   {
      String url = restServiceContext + CLOUDBEES_LOGIN;

      HashMap<String, String> credentials = new HashMap<String, String>();
      credentials.put("email", email);
      credentials.put("password", password);
      CredentailsMarshaller marshaller = new CredentailsMarshaller(credentials);

      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(marshaller.marshal())
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#login(java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void login(Paas paas, String email, String password, AsyncRequestCallback<String> callback)
      throws RequestException
   {
      String url = restServiceContext;

      if (Paas.CLOUDBEES == paas)
      {
         url += CLOUDBEES_LOGIN;
      }
      else if (Paas.CLOUDFOUNDRY == paas)
      {
         url += CLOUDFOUNDRY_LOGIN;
      }
      else if (Paas.HEROKU == paas)
      {
         url += HEROKU_LOGIN;
      }
      else if (Paas.OPENSHIFT == paas)
      {
         url += OPENSHIFT_LOGIN;
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Unknown PaaS: " + paas + ". Can't login."));
         return;
      }

      HashMap<String, String> credentials = new HashMap<String, String>();
      if (Paas.OPENSHIFT == paas)
      {
         credentials.put("rhlogin", email);
         credentials.put("password", password);
      }
      else
      {
         credentials.put("email", email);
         credentials.put("password", password);
      }
      CredentailsMarshaller marshaller = new CredentailsMarshaller(credentials);

      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(marshaller.marshal())
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#validateCloudfoundryAction(java.lang.String,
    *      java.lang.String, boolean,
    *      org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void validateCloudfoundryAction(String server, String appName, String workDir,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException
   {
      final String postUrl = restServiceContext + VALIDATE_ACTION;

      String params = "action=create";
      params += (server == null) ? "" : "&server=" + server;
      params += (appName == null) ? "" : "&name=" + appName;
      params += (workDir == null) ? "" : "&workdir=" + workDir;

      AsyncRequest.build(RequestBuilder.POST, postUrl + "?" + params).loader(loader).send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#createCloudFoundryApplication(String,
    *      java.lang.String, boolean, java.lang.String, java.lang.String,
    *      org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void createCloudFoundryApplication(String vfsId, String server, String name, String url, String workDir,
      String projectId, String war, CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback)
      throws RequestException
   {
      final String requestUrl = restServiceContext + CLOUDFOUNDRY_CREATE;

      String params = "name=" + name;
      params += "&workdir=" + workDir;
      params += (server != null) ? "&server=" + server : "";
      params += (war != null) ? "&war=" + war : "";
      params += (url != null) ? "&url=" + url : "";
      params += (vfsId != null) ? "&vfsid=" + vfsId : "";
      params += (projectId != null) ? "&projectid=" + projectId : "";
      params += "&nostart=true";

      AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#getCloudFoundryTargets(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getCloudFoundryTargets(AsyncRequestCallback<List<String>> callback) throws RequestException
   {
      String url = restServiceContext + CF_TARGETS;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#getOpenShiftTypes(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getOpenShiftTypes(AsyncRequestCallback<List<String>> callback) throws RequestException
   {
      String url = restServiceContext + OPENSHIFT_TYPES;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#createOpenShitfApplication(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void createOpenShitfApplication(String name, String vfsId, String projectId, String type,
      AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + OPENSHIFT_CREATE;

      String params = "?name=" + name + "&type=" + type + "&vfsid=" + vfsId + "&projectid=" + projectId;
      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#createHerokuApplication(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String,
    *      org.exoplatform.ide.extension.samples.client.paas.heroku.HerokuAsyncRequestCallback)
    */
   @Override
   public void createHerokuApplication(String applicationName, String vfsId, String projectid, String remoteName,
      HerokuAsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + HEROKU_CREATE;
      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (remoteName != null && !remoteName.trim().isEmpty()) ? "remote=" + remoteName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&" : "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

}
