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
import org.exoplatform.ide.client.IDE;
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
      public static final String EMPTY_XML = IDE.TEMPLATE_CONSTANT.templateEmptyXmlName();
      
      public static final String EMPTY_XML_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateEmptyXmlDescription(); 

      public static final String EMPTY_HTML = IDE.TEMPLATE_CONSTANT.templateEmptyHtmlName();
      
      public static final String EMPTY_HTML_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateEmptyHtmlDescription();

      public static final String EMPTY_TEXT = IDE.TEMPLATE_CONSTANT.templateEmptyTextName();
      
      public static final String EMPTY_TEXT_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateEmptyTextDescription();

      public static final String GOOGLE_GADGET = IDE.TEMPLATE_CONSTANT.templateGoogleGadgetName();
      
      public static final String GOOGLE_GADGET_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateGoogleGadgetDescription();

      public static final String GROOVY_REST_SERVICE = IDE.TEMPLATE_CONSTANT.templateGroovyRestServiceName();
      
      public static final String GROOVY_REST_SERVICE_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateGroovyRestServiceDescription();

      public static final String GROOVY_TEMPLATE = IDE.TEMPLATE_CONSTANT.templateGroovyTemplateName();
      
      public static final String GROOVY_TEMPLATE_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateGroovyTemplateDescription();
      
      public static final String NETVIBES_WIDGET = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetName();
      
      public static final String NETVIBES_WIDGET_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetDescription();
      
      public static final String NETVIBES_WIDGET_FLASH = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetFlashName();
      
      public static final String NETVIBES_WIDGET_FLASH_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetFlashDescription();
      
      public static final String NETVIBES_WIDGET_CHART = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetChartName();
      
      public static final String NETVIBES_WIDGET_CHART_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetChartDescription();
      
      public static final String NETVIBES_WIDGET_TABVIEW = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetTabViewName();
      
      public static final String NETVIBES_WIDGET_TABVIEW_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetTabViewDescription();
      
      public static final String NETVIBES_WIDGET_SAMPLE_BLOG_POST = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetSampleBlogPostName();
      
      public static final String NETVIBES_WIDGET_SAMPLE_BLOG_POST_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetSampleBlogPostDescription();

   }

   private static final String CONTEXT = "/templates";

   private static final String TEMPLATE = "template-";
   
   /* Template constants */
   private static final String EMPTY_PROJECT_NAME = IDE.TEMPLATE_CONSTANT.templateEmptyProjectName();
   
   private static final String EMPTY_PROJECT_DESCRIPTION = IDE.TEMPLATE_CONSTANT.templateEmptyProjectDescription();

   /* Fields */
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
         new FileTemplate(MimeType.TEXT_XML, DefaultFileTemplates.EMPTY_XML, DefaultFileTemplates.EMPTY_XML_DESCRIPTION, FileTemplates
            .getTemplateFor(MimeType.TEXT_XML), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.TEXT_HTML, DefaultFileTemplates.EMPTY_HTML, DefaultFileTemplates.EMPTY_HTML_DESCRIPTION, FileTemplates
            .getTemplateFor(MimeType.TEXT_HTML), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.TEXT_PLAIN, DefaultFileTemplates.EMPTY_TEXT, DefaultFileTemplates.EMPTY_TEXT_DESCRIPTION,
            FileTemplates.getTemplateFor(MimeType.TEXT_PLAIN), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.GOOGLE_GADGET, DefaultFileTemplates.GOOGLE_GADGET, DefaultFileTemplates.GOOGLE_GADGET_DESCRIPTION,
            FileTemplates.getTemplateFor(MimeType.GOOGLE_GADGET), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.GROOVY_SERVICE, DefaultFileTemplates.GROOVY_REST_SERVICE,
            DefaultFileTemplates.GROOVY_REST_SERVICE_DESCRIPTION, FileTemplates.getTemplateFor(MimeType.GROOVY_SERVICE), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.GROOVY_TEMPLATE, DefaultFileTemplates.GROOVY_TEMPLATE, DefaultFileTemplates.GROOVY_TEMPLATE_DESCRIPTION,
            FileTemplates.getTemplateFor(MimeType.GROOVY_TEMPLATE), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.UWA_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET_DESCRIPTION, FileTemplates
            .getTemplateFor(MimeType.UWA_WIDGET), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.UWA_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET_FLASH, DefaultFileTemplates.NETVIBES_WIDGET_FLASH_DESCRIPTION,
            NetvibesSamples.INSTANCE.getSampleFlashWidgetSource().getText(), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.UWA_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET_CHART, DefaultFileTemplates.NETVIBES_WIDGET_CHART_DESCRIPTION,
            NetvibesSamples.INSTANCE.getSampleChartWidgetSource().getText(), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.UWA_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET_TABVIEW, DefaultFileTemplates.NETVIBES_WIDGET_TABVIEW_DESCRIPTION,
            NetvibesSamples.INSTANCE.getSampleTabbedWidgetSource().getText(), null));

      templateList.getTemplates().add(
         new FileTemplate(MimeType.UWA_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET_SAMPLE_BLOG_POST, 
            DefaultFileTemplates.NETVIBES_WIDGET_SAMPLE_BLOG_POST_DESCRIPTION,
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
      ProjectTemplate sampleProject = new ProjectTemplate(EMPTY_PROJECT_NAME);
      sampleProject.setDescription(EMPTY_PROJECT_DESCRIPTION);

      return sampleProject;
   }

}
