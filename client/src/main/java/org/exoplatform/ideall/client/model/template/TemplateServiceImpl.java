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

import org.exoplatform.gwt.commons.initializer.RegistryConstants;
import org.exoplatform.gwt.commons.rest.AsyncRequest;
import org.exoplatform.gwt.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwt.commons.rest.HTTPHeader;
import org.exoplatform.gwt.commons.rest.MimeType;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.template.event.TemplateCreatedEvent;
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

   private HandlerManager eventBus;

   public TemplateServiceImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   @Override
   public void createTemplate(Template template)
   {
      String templateName = template.getName();

      String url =
         Configuration.getRegistryURL() + "/" + RegistryConstants.EXO_APPLICATIONS + "/" + Configuration.APPLICATION + CONTEXT + "/" + templateName
            + "/?createIfNotExist=true";

      TemplateMarshaller marshaller = new TemplateMarshaller(template);
      TemplateCreatedEvent event = new TemplateCreatedEvent(template);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML)
         .data(marshaller)
         .send(callback);
   }

   @Override
   public void deleteTemplate(String templateName)
   {
   }

   @Override
   public void getTemplates()
   {
      String url =
         Configuration.getRegistryURL() + "/" + RegistryConstants.EXO_APPLICATIONS + "/" + Configuration.APPLICATION + CONTEXT + "/?noCache=" + Random.nextInt();

      TemplateList templateList = new TemplateList();

      templateList.getTemplates().add(
         new Template(MimeType.TEXT_XML, "Empty XML", "Create empty XML file.", FileTemplates
            .getTemplateFor(MimeType.TEXT_XML)));

      templateList.getTemplates().add(
         new Template(MimeType.TEXT_HTML, "Empty HTML", "Create empty HTML file.", FileTemplates
            .getTemplateFor(MimeType.TEXT_HTML)));

      templateList.getTemplates().add(
         new Template(MimeType.TEXT_PLAIN, "Empty TEXT", "Create empty TEXT file.", FileTemplates
            .getTemplateFor(MimeType.TEXT_PLAIN)));      
      
      templateList.getTemplates().add(
         new Template(MimeType.GOOGLE_GADGET, "Google Gadget",
            "Sample of Google Gadget", FileTemplates.getTemplateFor(MimeType.GOOGLE_GADGET)));

      templateList.getTemplates().add(
         new Template(MimeType.SCRIPT_GROOVY, "Groovy REST Service", "Sample of Groovy REST service.",
            FileTemplates.getTemplateFor(MimeType.SCRIPT_GROOVY)));

      TemplateListUnmarshaller unmarshaller = new TemplateListUnmarshaller(templateList);
      TemplateListReceivedEvent event = new TemplateListReceivedEvent(templateList);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

}
