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

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.samples.client.marshal.RepositoriesUnmarshaller;
import org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudfoundryApplication;
import org.exoplatform.ide.extension.samples.client.paas.heroku.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.marshal.CloudfoundryApplicationUnmarshaller;
import org.exoplatform.ide.extension.samples.client.paas.marshal.CredentailsMarshaller;
import org.exoplatform.ide.extension.samples.client.paas.marshal.DeployWarUnmarshaller;
import org.exoplatform.ide.extension.samples.client.paas.marshal.DomainsUnmarshaller;
import org.exoplatform.ide.extension.samples.client.paas.marshal.OpenShiftTypesUnmarshaller;
import org.exoplatform.ide.extension.samples.client.paas.marshal.TargetsUnmarshaller;
import org.exoplatform.ide.extension.samples.shared.Repository;

import java.util.ArrayList;
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
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   public static final String SUPPORT = "support";

   public SamplesClientServiceImpl(HandlerManager eventBus, String restContext, Loader loader)
   {
      this.loader = loader;
      this.eventBus = eventBus;
      this.restServiceContext = restContext;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#getRepositoriesList(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getRepositoriesList(AsyncRequestCallback<List<Repository>> callback)
   {
      String url = restServiceContext + LIST;
      callback.setEventBus(eventBus);
      List<Repository> repos = new ArrayList<Repository>();
      callback.setPayload(new RepositoriesUnmarshaller(repos));
      callback.setResult(repos);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#createCloudBeesApplication(java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void createCloudBeesApplication(String appId, String vfsId, String projectId, String warFile, String message,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback)
   {
      final String url = restServiceContext + CLOUDBEES_CREATE;

      String params = "appid=" + appId + "&";
      params += "war=" + warFile;
      params += "&vfsid=" + vfsId + "&projectid=" + projectId;
      if (message != null && !message.isEmpty())
         params += "&message=" + message;
      
      Map<String, String> responseMap = new HashMap<String, String>();
      callback.setResult(responseMap);
      callback.setEventBus(eventBus);

      DeployWarUnmarshaller unmarshaller = new DeployWarUnmarshaller(responseMap);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#getDomains(org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void getDomains(CloudBeesAsyncRequestCallback<List<String>> callback)
   {
      final String url = restServiceContext + CLOUDBEES_DOMAINS;

      List<String> domains = new ArrayList<String>();
      callback.setResult(domains);
      callback.setEventBus(eventBus);

      DomainsUnmarshaller unmarshaller = new DomainsUnmarshaller(domains);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#loginToCloudBees(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void loginToCloudBees(String email, String password, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + CLOUDBEES_LOGIN;

      HashMap<String, String> credentials = new HashMap<String, String>();
      credentials.put("email", email);
      credentials.put("password", password);
      CredentailsMarshaller marshaller = new CredentailsMarshaller(credentials);

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).data(marshaller)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#login(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void login(Paas paas, String email, String password, AsyncRequestCallback<String> callback)
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
         eventBus.fireEvent(new ExceptionThrownEvent("Unknown PaaS: " + paas + ". Can't login."));
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

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).data(marshaller)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#validateCloudfoundryAction(java.lang.String, java.lang.String, boolean, org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void validateCloudfoundryAction(String server, String appName, 
      String workDir, CloudFoundryAsyncRequestCallback<String> callback)
   {
      final String postUrl = restServiceContext + VALIDATE_ACTION;

      String params = "action=create";
      params += (server == null) ? "" : "&server=" + server;
      params += (appName == null) ? "" : "&name=" + appName;
      params += (workDir == null) ? "" : "&workdir=" + workDir;

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, postUrl + "?" + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#createCloudFoundryApplication(String, java.lang.String, boolean, java.lang.String, java.lang.String, org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void createCloudFoundryApplication(String server, String name, String url, String workDir,
      String war, CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback)
   {
      final String requestUrl = restServiceContext + CLOUDFOUNDRY_CREATE;

      callback.setEventBus(eventBus);

      CloudfoundryApplication cloudfoundryApplication = new CloudfoundryApplication();

      CloudfoundryApplicationUnmarshaller unmarshaller =
         new CloudfoundryApplicationUnmarshaller(cloudfoundryApplication);
      callback.setPayload(unmarshaller);
      callback.setResult(cloudfoundryApplication);

      String params = "name=" + name;
      params += "&workdir=" + workDir;
      params += (server != null) ? "&server=" + server : "";
      params += (war != null) ? "&war=" + war : "";
      params += (url != null) ? "&url=" + url : "";

      AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#getCloudFoundryTargets(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getCloudFoundryTargets(AsyncRequestCallback<List<String>> callback)
   {
      String url = restServiceContext + CF_TARGETS;
      callback.setEventBus(eventBus);
      List<String> targes = new ArrayList<String>();
      callback.setResult(targes);
      TargetsUnmarshaller unmarshaller = new TargetsUnmarshaller(targes);
      callback.setPayload(unmarshaller);
      
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
      .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#getOpenShiftTypes(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getOpenShiftTypes(AsyncRequestCallback<List<String>> callback)
   {
      String url = restServiceContext + OPENSHIFT_TYPES;

      List<String> types = new ArrayList<String>();
      OpenShiftTypesUnmarshaller unmarshaller = new OpenShiftTypesUnmarshaller(types);
      callback.setResult(types);
      callback.setPayload(unmarshaller);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#createOpenShitfApplication(java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void createOpenShitfApplication(String name, String vfsId, String projectId, String type,
      AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + OPENSHIFT_CREATE;

      callback.setResult(name);
      callback.setEventBus(eventBus);
      String params = "?name=" + name + "&type=" + type + "&vfsid=" + vfsId + "&projectid=" + projectId;
      AsyncRequest.build(RequestBuilder.POST, url + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.SamplesClientService#createHerokuApplication(java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.samples.client.paas.heroku.HerokuAsyncRequestCallback)
    */
   @Override
   public void createHerokuApplication(String applicationName, String vfsId, String projectid, String remoteName,
      HerokuAsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + HEROKU_CREATE;
      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (remoteName != null && !remoteName.trim().isEmpty()) ? "remote=" + remoteName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&": "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      callback.setResult(applicationName);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

}
