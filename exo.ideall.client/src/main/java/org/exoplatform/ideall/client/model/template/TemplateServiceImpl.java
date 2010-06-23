/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.template;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.initializer.RegistryConstants;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.template.event.TemplateCreatedEvent;
import org.exoplatform.ideall.client.model.template.event.TemplateDeletedEvent;
import org.exoplatform.ideall.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ideall.client.model.template.marshal.TemplateListUnmarshaller;
import org.exoplatform.ideall.client.model.template.marshal.TemplateMarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Random;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class TemplateServiceImpl extends TemplateService
{

   private static final String CONTEXT = "/templates";

   private static final String TEMPLATE = "template-";
   
   private String restContext;

   private HandlerManager eventBus;

   private Loader loader;

   public TemplateServiceImpl(HandlerManager eventBus, Loader loader, String restContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restContext = restContext;
      
   }

   @Override
   public void createTemplate(Template template)
   {
      String url =  restContext + CONTEXT + "/" + TEMPLATE + System.currentTimeMillis() + "/?createIfNotExist=true";   
      TemplateMarshaller marshaller = new TemplateMarshaller(template);
      TemplateCreatedEvent event = new TemplateCreatedEvent(template);

      String errorMessage = "Registry service is not deployed.<br>Template already exist.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT").header(
         HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).data(marshaller).send(callback);
   }

   @Override
   public void deleteTemplate(Template template)
   {
      String url = restContext + CONTEXT + "/" + template.getNodeName();
      System.out.println("TemplateServiceImpl.deleteTemplate()"+url);
      String errorMessage = "Registry service is not deployed.<br>Template not found.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);
      TemplateDeletedEvent event = new TemplateDeletedEvent(template.getName());

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "DELETE").header(
         HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).send(callback);

   }

   @Override
   public void getTemplates()
   {
      String url = restContext + CONTEXT + "/?noCache=" + Random.nextInt();
      System.out.println("TemplateServiceImpl.getTemplates()"+url);

      TemplateList templateList = new TemplateList();

      templateList.getTemplates().add(
         new Template(MimeType.TEXT_XML, "Empty XML", "Create empty XML file.", FileTemplates
            .getTemplateFor(MimeType.TEXT_XML), null));

      templateList.getTemplates().add(
         new Template(MimeType.TEXT_HTML, "Empty HTML", "Create empty HTML file.", FileTemplates
            .getTemplateFor(MimeType.TEXT_HTML), null));

      templateList.getTemplates().add(
         new Template(MimeType.TEXT_PLAIN, "Empty TEXT", "Create empty TEXT file.", FileTemplates
            .getTemplateFor(MimeType.TEXT_PLAIN), null));

      templateList.getTemplates().add(
         new Template(MimeType.GOOGLE_GADGET, "Google Gadget", "Sample of Google Gadget", FileTemplates
            .getTemplateFor(MimeType.GOOGLE_GADGET), null));

      templateList.getTemplates().add(
         new Template(MimeType.SCRIPT_GROOVY, "Groovy REST Service", "Sample of Groovy REST service.", FileTemplates
            .getTemplateFor(MimeType.SCRIPT_GROOVY), null));

      templateList.getTemplates().add(
         new Template(MimeType.UWA_WIDGET, "Netvibes Widget", "Netvibes Widget Skeleton", FileTemplates
            .getTemplateFor(MimeType.UWA_WIDGET), null));

      TemplateListUnmarshaller unmarshaller = new TemplateListUnmarshaller(eventBus, templateList);
      TemplateListReceivedEvent event = new TemplateListReceivedEvent(templateList);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, event);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }
}
