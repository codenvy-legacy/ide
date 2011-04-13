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
package org.exoplatform.ide.client.model.template;


import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Random;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.model.template.marshal.TemplateListUnmarshaller;
import org.exoplatform.ide.client.model.template.marshal.TemplateMarshaller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class TemplateServiceImpl extends TemplateService
{
   private static final String TEMPLATE_REST_URL = "/ide/templates";

   private static final String CONTEXT = "/templates";

   private static final String TEMPLATE = "template-";

   private String restContext;

   private HandlerManager eventBus;

   private Loader loader;
   
   private String restServiceContext;

   public TemplateServiceImpl(HandlerManager eventBus, Loader loader, String restContext, 
      String restServiceContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restContext = restContext;
      this.restServiceContext = restServiceContext;

   }

   @Override
   public void createTemplate(Template template, TemplateCreatedCallback callback)
   {
      String url = restContext + CONTEXT + "/" + TEMPLATE + System.currentTimeMillis() + "/?createIfNotExist=true";
      TemplateMarshaller marshaller = new TemplateMarshaller(template);

      callback.setResult(template);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).data(marshaller).send(callback);
   }

   @Override
   public void deleteTemplate(Template template, TemplateDeletedCallback callback)
   {
      String url = restContext + CONTEXT + "/" + template.getNodeName();

      callback.setResult(template);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "DELETE")
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).send(callback);

   }

   @Override
   public void getTemplates(AsyncRequestCallback<TemplateList> callback)
   {
      String url = restContext + CONTEXT + "/?noCache=" + Random.nextInt();
      TemplateList templateList = new TemplateList();
      
      TemplateListUnmarshaller unmarshaller = new TemplateListUnmarshaller(eventBus, templateList);
      int[] acceptStatus = new int[]{HTTPStatus.OK, HTTPStatus.NOT_FOUND};

      callback.setResult(templateList);
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(acceptStatus);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }
   
   @Override
   public void getTemplateList(String type, AsyncRequestCallback<List<TemplateNative>> callback)
   {
      List<TemplateNative> templateList = new ArrayList<TemplateNative>();
      callback.setResult(templateList);
      
      DefaultTemplatesUnmarshaller unmarshal =
         new DefaultTemplatesUnmarshaller(templateList);
      
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshal);
      
      final String url = restServiceContext + TEMPLATE_REST_URL + "/list";
      
      AsyncRequest.build(RequestBuilder.GET, url, loader)
      .header("type", type).send(callback);
   }
   
   @Override
   public void createProject(String templateName, String location, AsyncRequestCallback<String> callback)
   {
      final String url = restServiceContext + TEMPLATE_REST_URL + "/create";
      
      callback.setResult(url);
      
      AsyncRequest.build(RequestBuilder.GET, url, loader)
      .header("type", "project").header("template-name", templateName)
      .header("location", location).send(callback);
   }
   
   @Override
   public void getFileContent(String templateName, AsyncRequestCallback<String> callback)
   {
      final String url = restServiceContext + TEMPLATE_REST_URL + "/file-content";

      TemplateContentUnmarshaller unmarshal = new TemplateContentUnmarshaller(callback);
      
      callback.setPayload(unmarshal);
      
      AsyncRequest.build(RequestBuilder.GET, url, loader)
      .header("template-name", templateName)
      .send(callback);
   }
}
