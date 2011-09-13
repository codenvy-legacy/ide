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

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.samples.client.marshal.RepositoriesUnmarshaller;
import org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.marshal.CredentailsMarshaller;
import org.exoplatform.ide.extension.samples.client.paas.marshal.DeployWarUnmarshaller;
import org.exoplatform.ide.extension.samples.client.paas.marshal.DomainsUnmarshaller;
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
   public void createCloudBeesApplication(String appId, String warFile, String message, String workDir,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback)
   {
      final String url = restServiceContext + CLOUDBEES_CREATE;

      String params = "appid=" + appId + "&";
      params += "war=" + warFile;
      if (message != null && !message.isEmpty())
         params += "&message=" + message;
      
      if(workDir !=null)
         params +="&workdir="+ workDir;

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


}
