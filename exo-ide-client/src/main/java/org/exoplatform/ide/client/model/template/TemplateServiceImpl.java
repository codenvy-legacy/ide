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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Random;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.model.template.marshal.FileTemplateListMarshaller;
import org.exoplatform.ide.client.model.template.marshal.FileTemplateListUnmarshaller;
import org.exoplatform.ide.client.model.template.marshal.FileTemplateMarshaller;
import org.exoplatform.ide.client.model.template.marshal.ProjectTemplateListMarshaller;
import org.exoplatform.ide.client.model.template.marshal.ProjectTemplateListUnmarshaller;
import org.exoplatform.ide.client.model.template.marshal.ProjectTemplateMarshaller;
import org.exoplatform.ide.client.model.template.marshal.TemplateListUnmarshaller;
import org.exoplatform.ide.client.model.template.marshal.TemplateMarshaller;
import org.exoplatform.ide.client.samples.NetvibesSamples;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

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

      public static final String GROOVY_REST_SERVICE_DESCRIPTION = IDE.TEMPLATE_CONSTANT
         .templateGroovyRestServiceDescription();

      public static final String GROOVY_TEMPLATE = IDE.TEMPLATE_CONSTANT.templateGroovyTemplateName();

      public static final String GROOVY_TEMPLATE_DESCRIPTION = IDE.TEMPLATE_CONSTANT
         .templateGroovyTemplateDescription();

      public static final String NETVIBES_WIDGET = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetName();

      public static final String NETVIBES_WIDGET_DESCRIPTION = IDE.TEMPLATE_CONSTANT
         .templateNetvibesWidgetDescription();

      public static final String NETVIBES_WIDGET_FLASH = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetFlashName();

      public static final String NETVIBES_WIDGET_FLASH_DESCRIPTION = IDE.TEMPLATE_CONSTANT
         .templateNetvibesWidgetFlashDescription();

      public static final String NETVIBES_WIDGET_CHART = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetChartName();

      public static final String NETVIBES_WIDGET_CHART_DESCRIPTION = IDE.TEMPLATE_CONSTANT
         .templateNetvibesWidgetChartDescription();

      public static final String NETVIBES_WIDGET_TABVIEW = IDE.TEMPLATE_CONSTANT.templateNetvibesWidgetTabViewName();

      public static final String NETVIBES_WIDGET_TABVIEW_DESCRIPTION = IDE.TEMPLATE_CONSTANT
         .templateNetvibesWidgetTabViewDescription();

      public static final String NETVIBES_WIDGET_SAMPLE_BLOG_POST = IDE.TEMPLATE_CONSTANT
         .templateNetvibesWidgetSampleBlogPostName();

      public static final String NETVIBES_WIDGET_SAMPLE_BLOG_POST_DESCRIPTION = IDE.TEMPLATE_CONSTANT
         .templateNetvibesWidgetSampleBlogPostDescription();

   }

   private static final String CONTEXT = "/templates";

   private static final String TEMPLATE = "template-";

   /* Fields */
   private String registryContext;

   private String restContext;

   private HandlerManager eventBus;

   private Loader loader;

   public TemplateServiceImpl(HandlerManager eventBus, Loader loader, String registryContext, String restContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.registryContext = registryContext;
      this.restContext = restContext;
   }

   @Override
   public void createTemplate(Template template, TemplateCreatedCallback callback)
   {
      String url = registryContext + CONTEXT + "/" + TEMPLATE + System.currentTimeMillis() + "/?createIfNotExist=true";
      TemplateMarshaller marshaller = new TemplateMarshaller(template);

      callback.setResult(template);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).data(marshaller).send(callback);
   }

   @Override
   public void deleteTemplate(Template template, TemplateDeletedCallback callback)
   {
      String url = registryContext + CONTEXT + "/" + template.getNodeName();

      callback.setResult(template);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "DELETE")
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).send(callback);
   }

   @Override
   public void getTemplates(AsyncRequestCallback<TemplateList> callback)
   {
      String url = registryContext + CONTEXT + "/?noCache=" + Random.nextInt();
      final TemplateList templateList = new TemplateList();

      TemplateListUnmarshaller unmarshaller = new TemplateListUnmarshaller(eventBus, templateList);
      int[] acceptStatus = new int[]{HTTPStatus.OK, HTTPStatus.NOT_FOUND};

      callback.setResult(templateList);
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(acceptStatus);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.model.template.TemplateService#addFileTemplate(org.exoplatform.ide.client.model.template.Template,
    *      org.exoplatform.ide.client.model.template.TemplateCreatedCallback)
    */
   @Override
   public void addFileTemplate(FileTemplate template, AsyncRequestCallback<FileTemplate> callback)
   {
      String url = restContext + "/ide/templates/file/add";
      FileTemplateMarshaller marshaller = new FileTemplateMarshaller(template);

      callback.setResult(template);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(marshaller).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.model.template.TemplateService#getFileTemplateList(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getFileTemplateList(AsyncRequestCallback<FileTemplateList> callback)
   {
      String url = restContext + "/ide/templates/file/list";
      FileTemplateList fileTemplateList = new FileTemplateList();
      fileTemplateList.setFileTemplates(getDefaultFileTemplates());
      FileTemplateListUnmarshaller unmarshaller = new FileTemplateListUnmarshaller(fileTemplateList);

      callback.setResult(fileTemplateList);
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.model.template.TemplateService#deleteFileTemplate(java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deleteFileTemplate(String templateName, AsyncRequestCallback<String> callback)
   {
      String url = restContext + "/ide/templates/file/delete";
      String param = "name=" + URL.encodePathSegment(templateName);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + param, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.model.template.TemplateService#getProjectTemplateList(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getProjectTemplateList(AsyncRequestCallback<ProjectTemplateList> callback)
   {
      String url = restContext + "/ide/templates/project/list";
      ProjectTemplateList projectTemplateList = new ProjectTemplateList();
      projectTemplateList.setProjectTemplates(getDefaultProjectTemplates());
      ProjectTemplateListUnmarshaller unmarshaller = new ProjectTemplateListUnmarshaller(projectTemplateList);

      callback.setResult(projectTemplateList);
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.model.template.TemplateService#deleteProjectTemplate(java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deleteProjectTemplate(String templateName, AsyncRequestCallback<String> callback)
   {
      String url = restContext + "/ide/templates/project/delete";
      String param = "name=" + URL.encodePathSegment(templateName);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + param, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.model.template.TemplateService#addProjectTemplate(org.exoplatform.ide.client.model.template.ProjectTemplate,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void addProjectTemplate(ProjectTemplate projectTemplate, AsyncRequestCallback<String> callback)
   {
      String url = restContext + "/ide/templates/project/add";
      ProjectTemplateMarshaller marshaller = new ProjectTemplateMarshaller(projectTemplate);

      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(marshaller).send(callback);
   }

   /*
    * Methods, used for templates transfer from registry to settings file.
    */

   /**
    * @see org.exoplatform.ide.client.model.template.TemplateService#addFileTemplateList(java.util.List,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void addFileTemplateList(List<FileTemplate> fileTemplates, AsyncRequestCallback<String> callback)
   {
      String url = restContext + "/ide/templates/file/add/list";
      FileTemplateListMarshaller marshaller = new FileTemplateListMarshaller(fileTemplates);

      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(marshaller).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.model.template.TemplateService#addProjectTemplateList(java.util.List,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void addProjectTemplateList(List<ProjectTemplate> projectTemplates, AsyncRequestCallback<String> callback)
   {
      String url = restContext + "/ide/templates/project/add/list";
      ProjectTemplateListMarshaller marshaller = new ProjectTemplateListMarshaller(projectTemplates);

      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(marshaller).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.model.template.TemplateService#deleteTemplatesFromRegistry(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deleteTemplatesFromRegistry(AsyncRequestCallback<String> callback)
   {
      String url = registryContext + CONTEXT;

      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "DELETE")
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).send(callback);
   }

   // ----Implementation-------------------

   private List<FileTemplate> getDefaultFileTemplates()
   {
      List<FileTemplate> fileTemplates = new ArrayList<FileTemplate>();
      fileTemplates.add(new FileTemplate(MimeType.TEXT_XML, DefaultFileTemplates.EMPTY_XML,
         DefaultFileTemplates.EMPTY_XML_DESCRIPTION, FileTemplates.getTemplateFor(MimeType.TEXT_XML), true));

      fileTemplates.add(new FileTemplate(MimeType.TEXT_HTML, DefaultFileTemplates.EMPTY_HTML,
         DefaultFileTemplates.EMPTY_HTML_DESCRIPTION, FileTemplates.getTemplateFor(MimeType.TEXT_HTML), true));

      fileTemplates.add(new FileTemplate(MimeType.TEXT_PLAIN, DefaultFileTemplates.EMPTY_TEXT,
         DefaultFileTemplates.EMPTY_TEXT_DESCRIPTION, FileTemplates.getTemplateFor(MimeType.TEXT_PLAIN), true));

      fileTemplates.add(new FileTemplate(MimeType.GOOGLE_GADGET, DefaultFileTemplates.GOOGLE_GADGET,
         DefaultFileTemplates.GOOGLE_GADGET_DESCRIPTION, FileTemplates.getTemplateFor(MimeType.GOOGLE_GADGET), true));

      fileTemplates.add(new FileTemplate(MimeType.GROOVY_SERVICE, DefaultFileTemplates.GROOVY_REST_SERVICE,
         DefaultFileTemplates.GROOVY_REST_SERVICE_DESCRIPTION, FileTemplates.getTemplateFor(MimeType.GROOVY_SERVICE),
         true));

      fileTemplates
         .add(new FileTemplate(MimeType.GROOVY_TEMPLATE, DefaultFileTemplates.GROOVY_TEMPLATE,
            DefaultFileTemplates.GROOVY_TEMPLATE_DESCRIPTION, FileTemplates.getTemplateFor(MimeType.GROOVY_TEMPLATE),
            true));

      fileTemplates.add(new FileTemplate(MimeType.UWA_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET,
         DefaultFileTemplates.NETVIBES_WIDGET_DESCRIPTION, FileTemplates.getTemplateFor(MimeType.UWA_WIDGET), true));

      fileTemplates.add(new FileTemplate(MimeType.UWA_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET_FLASH,
         DefaultFileTemplates.NETVIBES_WIDGET_FLASH_DESCRIPTION, NetvibesSamples.INSTANCE.getSampleFlashWidgetSource()
            .getText(), true));

      fileTemplates.add(new FileTemplate(MimeType.UWA_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET_CHART,
         DefaultFileTemplates.NETVIBES_WIDGET_CHART_DESCRIPTION, NetvibesSamples.INSTANCE.getSampleChartWidgetSource()
            .getText(), true));

      fileTemplates.add(new FileTemplate(MimeType.UWA_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET_TABVIEW,
         DefaultFileTemplates.NETVIBES_WIDGET_TABVIEW_DESCRIPTION, NetvibesSamples.INSTANCE
            .getSampleTabbedWidgetSource().getText(), true));

      fileTemplates.add(new FileTemplate(MimeType.UWA_WIDGET, DefaultFileTemplates.NETVIBES_WIDGET_SAMPLE_BLOG_POST,
         DefaultFileTemplates.NETVIBES_WIDGET_SAMPLE_BLOG_POST_DESCRIPTION, NetvibesSamples.INSTANCE
            .getSampleBlogPostWidgetSource().getText(), true));

      // find file templates in default projects
      // ShoppingCardProject shoppingCardProject = new ShoppingCardProject();
      // for (Template template : shoppingCardProject.getTemplateList())
      // {
      // if (template instanceof FileTemplate)
      // fileTemplates.add((FileTemplate)template);
      // }
      //
      // TwitterTrendsProject twitterTrendsProject = new TwitterTrendsProject();
      // for (Template template : twitterTrendsProject.getTemplateList())
      // {
      // if (template instanceof FileTemplate)
      // fileTemplates.add((FileTemplate)template);
      // }
      //
      // LinkedinContactsProject linkedinContactsProject = new LinkedinContactsProject();
      // for (Template template : linkedinContactsProject.getTemplateList())
      // {
      // if (template instanceof FileTemplate)
      // fileTemplates.add((FileTemplate)template);
      // }
      //
      // DefaultIdeProject defaultIdeProject = new DefaultIdeProject();
      // for (Template template : defaultIdeProject.getTemplateList())
      // {
      // if (template instanceof FileTemplate)
      // fileTemplates.add((FileTemplate)template);
      // }
      return fileTemplates;
   }

   private List<ProjectTemplate> getDefaultProjectTemplates()
   {
      List<ProjectTemplate> projectTemplates = new ArrayList<ProjectTemplate>();

      // ShoppingCardProject shoppingCardProject = new ShoppingCardProject();
      // projectTemplates.add(shoppingCardProject.getProjectTemplate());
      //
      // TwitterTrendsProject twitterTrendsProject = new TwitterTrendsProject();
      // projectTemplates.add(twitterTrendsProject.getProjectTemplate());
      //
      // LinkedinContactsProject linkedinContactsProject = new LinkedinContactsProject();
      // projectTemplates.add(linkedinContactsProject.getProjectTemplate());
      //
      // DefaultIdeProject defaultIdeProject = new DefaultIdeProject();
      // projectTemplates.add(defaultIdeProject.getProjectTemplate());

      // projectTemplates.add(getEmptyProject());
      return projectTemplates;
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.client.model.template.TemplateService#createProjectFromTemplate(java.lang.String, java.lang.String,
    *      org.exoplatform.ide.client.model.template.ProjectTemplate,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void createProjectFromTemplate(String vfsId, String parentId, String name, String templateName,
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<ProjectModel> callback)
      throws RequestException
   {
      String url = restContext + "/ide/templates/project/create";
      url += "?vfsid=" + vfsId;
      url += "&name=" + name;
      url += "&parentId=" + parentId;
      url += "&templateName=" + templateName;
      url = URL.encode(url);
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest.build(RequestBuilder.POST, url).send(callback);
   }

}
