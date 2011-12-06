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
package org.exoplatform.ide.editor.gtmpl.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.groovy.client.codeassistant.service.GroovyCodeAssistantService;
import org.exoplatform.ide.editor.gtmpl.client.codeassistant.GroovyTemplateCodeAssistant;
import org.exoplatform.ide.editor.gtmpl.client.codemirror.GroovyTemplateAutocompleteHelper;
import org.exoplatform.ide.editor.gtmpl.client.codemirror.GroovyTemplateCodeValidator;
import org.exoplatform.ide.editor.gtmpl.client.codemirror.GroovyTemplateParser;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlOutlineItemCreator;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaTokenWidgetFactory;
import org.exoplatform.ide.editor.java.client.codeassistant.services.CodeAssistantService;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyEditorExtension Mar 10, 2011 3:48:59 PM evgen $
 *
 */
public class GtmplEditorExtension extends Extension implements InitializeServicesHandler,
   JavaCodeAssistantErrorHandler, EditorActiveFileChangedHandler, ProjectOpenedHandler, ProjectClosedHandler
{

   private GroovyTemplateCodeAssistant templateCodeAssistant;

   private JavaTokenWidgetFactory factory;

   private ProjectModel currentProject;

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);

      IDE.getInstance().addControl(
         new NewItemControl("File/New/New Template", "Template", "Create Template", Images.GROOVY_TEMPLATE,
            MimeType.GROOVY_TEMPLATE));
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      CodeAssistantService service;
      if (GroovyCodeAssistantService.get() == null)
         service = new GroovyCodeAssistantService(event.getApplicationConfiguration().getContext(), event.getLoader());
      else
         service = GroovyCodeAssistantService.get();

      factory =
         new JavaTokenWidgetFactory(event.getApplicationConfiguration().getContext()
            + "/ide/code-assistant/groovy/class-doc?fqn=");
      templateCodeAssistant = new GroovyTemplateCodeAssistant(service, factory, this);

      IDE.getInstance()
         .addEditor(
            new CodeMirrorProducer(
               MimeType.GROOVY_TEMPLATE,
               "CodeMirror Groovy Template editor",
               "gtmpl",
               Images.INSTANCE.groovyTemplate(),
               true,
               new CodeMirrorConfiguration()
                  .setGenericParsers(
                     "['parsegtmpl.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'tokenizegroovy.js', 'parsegroovy.js', 'parsegtmplmixed.js']")
                  .setGenericStyles(
                     "['" + CodeMirrorConfiguration.PATH + "css/gtmplcolors.css', '" + CodeMirrorConfiguration.PATH
                        + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css', '"
                        + CodeMirrorConfiguration.PATH + "css/groovycolors.css']")
                  .setParser(new GroovyTemplateParser()).setCanBeOutlined(true)
                  .setAutocompleteHelper(new GroovyTemplateAutocompleteHelper())
                  .setCodeAssistant(templateCodeAssistant).setCodeValidator(new GroovyTemplateCodeValidator())
                  .setCanHaveSeveralMimeTypes(true)));

      IDE.getInstance().addOutlineItemCreator(MimeType.GROOVY_TEMPLATE, new HtmlOutlineItemCreator());
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler#handleError(java.lang.Throwable)
    */
   @Override
   public void handleError(Throwable exc)
   {
      if (exc instanceof ServerException)
      {
         ServerException exception = (ServerException)exc;
         String outputContent =
            "Error (<i>" + exception.getHTTPStatus() + "</i>: <i>" + exception.getStatusText() + "</i>)";
         if (!exception.getMessage().equals(""))
         {
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on "<br />"
         }

         IDE.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent(exc.getMessage()));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() != null)
      {
         ProjectModel project = event.getFile().getProject() != null ? event.getFile().getProject() : currentProject;
         if (project != null)
         {
            templateCodeAssistant.setActiveProjectId(project.getId());
            factory.setProjectId(project.getId());
         }
      }
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      currentProject = event.getProject();
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      currentProject = null;
   }

}
