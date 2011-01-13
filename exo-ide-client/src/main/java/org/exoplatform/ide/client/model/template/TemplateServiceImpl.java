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

import java.util.ArrayList;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.model.template.event.TemplateCreatedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateDeletedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ide.client.model.template.marshal.TemplateListUnmarshaller;
import org.exoplatform.ide.client.model.template.marshal.TemplateMarshaller;

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

      public static final String GREETING_GOOGLE_GADGET = "Greeting Google Gadget";

      public static final String GROOVY_REST_SERVICE = "Groovy REST Service";

      public static final String GROOVY_TEMPLATE = "Template";
      
      public static final String GROOVY_CLASS_PATH = "Groovy Build Class Path";
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
   public void createTemplate(Template template)
   {
      String url = restContext + CONTEXT + "/" + TEMPLATE + System.currentTimeMillis() + "/?createIfNotExist=true";
      TemplateMarshaller marshaller = new TemplateMarshaller(template);
      TemplateCreatedEvent event = new TemplateCreatedEvent(template);

      String errorMessage = "Registry service is not deployed.<br>Template already exist.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).data(marshaller).send(callback);
   }

   @Override
   public void deleteTemplate(Template template)
   {
      String url = restContext + CONTEXT + "/" + template.getNodeName();
      String errorMessage = "Registry service is not deployed.<br>Template not found.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);
      TemplateDeletedEvent event = new TemplateDeletedEvent(template);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "DELETE")
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).send(callback);

   }

   @Override
   public void getTemplates()
   {
      String url = restContext + CONTEXT + "/?noCache=" + Random.nextInt();
      TemplateList templateList = new TemplateList();

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
         new FileTemplate(MimeType.APPLICATION_GROOVY_CLASSPATH, DefaultFileTemplates.GROOVY_CLASS_PATH, "Groovy build class path file. Is used to configure dependency locations.", FileTemplates
            .getTemplateFor(MimeType.APPLICATION_GROOVY_CLASSPATH), null));
      

      templateList.getTemplates().add(createFileTemplateForSampleProject());

      templateList.getTemplates().add(getSampleProject());

      TemplateListUnmarshaller unmarshaller = new TemplateListUnmarshaller(eventBus, templateList);
      TemplateListReceivedEvent event = new TemplateListReceivedEvent(templateList);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, event);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   private ProjectTemplate getSampleProject()
   {
      ProjectTemplate sampleProject = new ProjectTemplate("Sample project");
      sampleProject.setDescription("Sample project with REST script and Google Gadget");
      String classPathFileName = ".groovyclasspath";
      FileTemplate classPathTemplate = 
         new FileTemplate(DefaultFileTemplates.GROOVY_CLASS_PATH, classPathFileName);
      sampleProject.setClassPathLocation(classPathFileName);
      sampleProject.getChildren().add(classPathTemplate);

      FolderTemplate businessLogicFolder = new FolderTemplate("business logic");
      businessLogicFolder.setChildren(new ArrayList<Template>());
      FileTemplate restScriptTemplate =
         new FileTemplate(DefaultFileTemplates.GROOVY_REST_SERVICE, "Greeting REST Service.groovy");
      businessLogicFolder.getChildren().add(restScriptTemplate);

      FolderTemplate uiFolder = new FolderTemplate("UI");
      uiFolder.setChildren(new ArrayList<Template>());
      FileTemplate gadgetFileTemplate =
         new FileTemplate(DefaultFileTemplates.GREETING_GOOGLE_GADGET, "Greeting Google Gadget.xml");
      uiFolder.getChildren().add(gadgetFileTemplate);
      
      FolderTemplate dataFolder = new FolderTemplate("data");

      sampleProject.getChildren().add(dataFolder);
      sampleProject.getChildren().add(businessLogicFolder);
      sampleProject.getChildren().add(uiFolder);

      return sampleProject;
   }

   private FileTemplate createFileTemplateForSampleProject()
   {
      final String gadgetContent =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
            + "<Module>\n"
            + "  <ModulePrefs title=\"Hello World!\" />\n"
            + "  <Content type=\"html\">\n"
            + "    <![CDATA[ \n"
            + "    <div id=\"content_div\">\n"
            + "      <form name=\"sample\">\n"
            + "        Enter your name:<br>\n"
            + "        <input type=\"text\" name=\"user\">\n"
            + "        <input type=\"button\" name=\"button1\" value=\"Ok\" onClick=\"hello(this.form)\">\n"
            + "      </form>\n"
            + "      <span id=\"response\"></span>\n"
            + "    </div>\n"
            + "    <script type=\"text/javascript\">\n\n"
            + "      function hello(form) {\n"
            + "        if (form.user.value == \"\")\n"
            + "          document.getElementById('response').innerHTML=\"Please enter name!\";\n"
            + "        else {\n"
            + "          getGreeting(form.user.value);\n"
            + "        }\n"
            + "      }\n\n"
            + "      function getGreeting(userName) {\n"
            + "        var params = {};\n"
            + "        params[gadgets.io.RequestParameters.CONTENT_TYPE] = gadgets.io.ContentType.TEXT;\n"
            + "        var url = 'http://' + location.host + '/rest/my-service/helloworld/' + userName;\n"
            + "        gadgets.io.makeRequest(url, response, params);\n"
            + "      }\n"
            + "      // Callback function to process the response\n"
            + "      function response(obj) {\n"
            + "        var responseText = obj.text;\n\n"
            + "        var html = \"<div style='padding: 5px;background-color: #FFFFBF;font-family:Arial, Helvetica;\"\n"
            + "            + \"text-align:left;font-size:90%'>\";\n\n" + "        html += responseText;\n"
            + "        html += \"</div>\";\n" + "        // Output html in div.\n"
            + "        document.getElementById('response').innerHTML = html;\n" + "      }\n\n" + "    </script>\n"
            + "    ]]></Content></Module>\n";

      FileTemplate gadgetFileTemplate =
         new FileTemplate(MimeType.GOOGLE_GADGET, DefaultFileTemplates.GREETING_GOOGLE_GADGET,
            "Google Gadget with request to service", gadgetContent, null);
      gadgetFileTemplate.setFileName("Greeting Google Gadget.xml");

      return gadgetFileTemplate;
   }
}
