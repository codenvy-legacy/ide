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

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.model.template.marshal.TemplateListUnmarshaller;
import org.exoplatform.ide.client.model.template.marshal.TemplateMarshaller;
import org.exoplatform.ide.client.samples.ide.DefaultIdeProject;
import org.exoplatform.ide.client.samples.linkedin.LinkedinContactsProject;
import org.exoplatform.ide.client.samples.netvibes.NetvibesSamples;
import org.exoplatform.ide.client.samples.sc.ShoppingCardProject;
import org.exoplatform.ide.client.samples.twittertrends.TwitterTrendsProject;

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

   private interface DefaultFileTemplates
   {
      public static final String EMPTY_XML = "Empty XML";

      public static final String EMPTY_HTML = "Empty HTML";

      public static final String EMPTY_TEXT = "Empty TEXT";

      public static final String GOOGLE_GADGET = "Google Gadget";

      public static final String GROOVY_REST_SERVICE = "Groovy REST Service";

      public static final String GROOVY_TEMPLATE = "Template";

   }

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
      final TemplateList templateList = new TemplateList();

      templateList.getTemplates().add(
         new FileTemplate(MimeType.TEXT_XML, DefaultFileTemplates.EMPTY_XML, "Create empty XML file.", FileTemplates
            .getTemplateFor(MimeType.TEXT_XML), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.TEXT_HTML, DefaultFileTemplates.EMPTY_HTML, "Create empty HTML file.", FileTemplates
            .getTemplateFor(MimeType.TEXT_HTML), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.TEXT_PLAIN, DefaultFileTemplates.EMPTY_TEXT, "Create empty TEXT file.",
            FileTemplates.getTemplateFor(MimeType.TEXT_PLAIN), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.GOOGLE_GADGET, DefaultFileTemplates.GOOGLE_GADGET, "Sample of Google Gadget",
            FileTemplates.getTemplateFor(MimeType.GOOGLE_GADGET), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.GROOVY_SERVICE, DefaultFileTemplates.GROOVY_REST_SERVICE,
            "Sample of Groovy REST service.", FileTemplates.getTemplateFor(MimeType.GROOVY_SERVICE), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.GROOVY_TEMPLATE, DefaultFileTemplates.GROOVY_TEMPLATE, "Sample of Template.",
            FileTemplates.getTemplateFor(MimeType.GROOVY_TEMPLATE), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.UWA_WIDGET, "Netvibes Widget", "Netvibes Widget Skeleton", FileTemplates
            .getTemplateFor(MimeType.UWA_WIDGET), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.UWA_WIDGET, "Netvibes Widget Flash", "Netvibes Widget Flash",
            NetvibesSamples.INSTANCE.getSampleFlashWidgetSource().getText(), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.UWA_WIDGET, "Netvibes Widget Chart", "Netvibes Widget Chart",
            NetvibesSamples.INSTANCE.getSampleChartWidgetSource().getText(), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.UWA_WIDGET, "Netvibes Widget Tabview", "Netvibes Widget Tabview",
            NetvibesSamples.INSTANCE.getSampleTabbedWidgetSource().getText(), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.UWA_WIDGET, "Netvibes Sample Blog Post Widget", "Netvibes Sample Blog Post Widget",
            NetvibesSamples.INSTANCE.getSampleBlogPostWidgetSource().getText(), null));

      ShoppingCardProject shoppingCardProject = new ShoppingCardProject();
      templateList.getTemplates().addAll(shoppingCardProject.getTemplateList());
      
      TwitterTrendsProject twitterTrendsProject = new TwitterTrendsProject();
      templateList.getTemplates().addAll(twitterTrendsProject.getTemplateList());
      
      LinkedinContactsProject linkedinContactsProject = new LinkedinContactsProject();
      templateList.getTemplates().addAll(linkedinContactsProject.getTemplateList());

      DefaultIdeProject defaultIdeProject = new DefaultIdeProject();
      templateList.getTemplates().addAll(defaultIdeProject.getTemplateList());
      
      templateList.getTemplates().add(getEmptyProject());
     
      
      TemplateListUnmarshaller unmarshaller = new TemplateListUnmarshaller(eventBus, templateList);
      int[] acceptStatus = new int[]{HTTPStatus.OK, HTTPStatus.NOT_FOUND};
 
      callback.setResult(templateList);
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(acceptStatus);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   private static ProjectTemplate getEmptyProject()
   {
      ProjectTemplate sampleProject = new ProjectTemplate("new-project");
      sampleProject.setDescription("Empty project");

      return sampleProject;
   }

}
