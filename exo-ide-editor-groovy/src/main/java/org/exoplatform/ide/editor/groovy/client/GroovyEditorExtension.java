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
package org.exoplatform.ide.editor.groovy.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.copy.MimeType;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
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
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.groovy.client.codeassistant.GroovyCodeAssistant;
import org.exoplatform.ide.editor.groovy.client.codeassistant.service.GroovyCodeAssistantService;
import org.exoplatform.ide.editor.groovy.client.codemirror.GroovyAutocompleteHelper;
import org.exoplatform.ide.editor.groovy.client.codemirror.GroovyCodeValidator;
import org.exoplatform.ide.editor.groovy.client.codemirror.GroovyOutlineItemCreator;
import org.exoplatform.ide.editor.groovy.client.codemirror.GroovyParser;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaTokenWidgetFactory;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.FindClassesUnmarshaller;
import org.exoplatform.ide.editor.java.client.codemirror.JavaCodeValidator;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyEditorExtension Mar 10, 2011 3:48:59 PM evgen $
 * 
 */
public class GroovyEditorExtension extends Extension implements InitializeServicesHandler,
   JavaCodeAssistantErrorHandler, EditorActiveFileChangedHandler, ProjectOpenedHandler, ProjectClosedHandler
{

   private JavaCodeAssistant groovyCodeAssistant;

   private JavaTokenWidgetFactory factory;

   private ProjectModel currentProject;
   
   private JavaCodeValidator groovyCodeValidator;
   
   private GroovyCodeAssistantService service;

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
         new NewItemControl("File/New/New REST Service", "REST Service", "Create REST Service", Images.REST_SERVICE,
            MimeType.GROOVY_SERVICE).setDelimiterBefore(true));

      IDE.getInstance().addControl(
         new NewItemControl("File/New/New POGO", "POGO", "Create POGO", Images.GROOVY, MimeType.APPLICATION_GROOVY));
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      if (GroovyCodeAssistantService.get() == null)
         service = new GroovyCodeAssistantService(event.getApplicationConfiguration().getContext(), event.getLoader());
      else
         service = GroovyCodeAssistantService.get();

      factory =
         new JavaTokenWidgetFactory(event.getApplicationConfiguration().getContext()
            + "/ide/code-assistant/groovy/class-doc?fqn=");
      groovyCodeAssistant = new GroovyCodeAssistant(service, factory, this);
      groovyCodeValidator = new GroovyCodeValidator();
      IDE.getInstance().addEditor(
         new CodeMirrorProducer(MimeType.APPLICATION_GROOVY, "CodeMirror POJO editor", "groovy", Images.INSTANCE
            .groovy(), true, new CodeMirrorConfiguration().setGenericParsers("['parsegroovy.js', 'tokenizegroovy.js']")
            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/groovycolors.css']")
            .setParser(new GroovyParser()).setCanBeOutlined(true).setAutocompleteHelper(new GroovyAutocompleteHelper())
            .setCodeAssistant(groovyCodeAssistant).setCodeValidator(groovyCodeValidator)));

      IDE.getInstance().addEditor(
         new CodeMirrorProducer(MimeType.GROOVY_SERVICE, "CodeMirror REST Service editor", "grs", Images.INSTANCE
            .groovy(), true, new CodeMirrorConfiguration().setGenericParsers("['parsegroovy.js', 'tokenizegroovy.js']")
            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/groovycolors.css']")
            .setParser(new GroovyParser()).setCanBeOutlined(true).setAutocompleteHelper(new GroovyAutocompleteHelper())
            .setCodeAssistant(groovyCodeAssistant).setCodeValidator(groovyCodeValidator)));

      GroovyOutlineItemCreator groovyOutlineItemCreator = new GroovyOutlineItemCreator();
      IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_GROOVY, groovyOutlineItemCreator);
      IDE.getInstance().addOutlineItemCreator(MimeType.GROOVY_SERVICE, groovyOutlineItemCreator);

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
            outputContent += "<br />" + exception.getMessage().replace("\n", "<br />"); // replace "end of line" symbols on
                                                                                        // "<br />"
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
   public void onEditorActiveFileChanged(final EditorActiveFileChangedEvent event)
   {
      if (event.getFile() != null)
      {
         final ProjectModel project = event.getFile().getProject() != null ? event.getFile().getProject() : currentProject;
         if (project != null)
         {
            groovyCodeAssistant.setActiveProjectId(project.getId());
            factory.setProjectId(project.getId());
         }
         
         if (event.getFile().getMimeType().equals(MimeType.APPLICATION_GROOVY) || event.getFile().getMimeType()
                  .equals(MimeType.GROOVY_SERVICE)
                  || event.getFile().getMimeType().equals(MimeType.CHROMATTIC_DATA_OBJECT))
         {
            List<Token> classes = new ArrayList<Token>();
            FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(classes);
            service.findClassesByProject(event.getFile().getId(), project.getId(),
               new AsyncRequestCallback<List<Token>>(unmarshaller)
               {

                  @Override
                  protected void onSuccess(List<Token> result)
                  {
                     groovyCodeValidator.setClassesFromProject(result);
                     ((CodeMirror)event.getEditor()).validateCode();
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     handleError(exception);
                  }
               });
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
