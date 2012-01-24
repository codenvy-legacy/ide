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
package org.exoplatform.ide.editor.chromattic.client;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
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
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyEditorExtension Mar 10, 2011 3:48:59 PM evgen $
 * 
 */
public class ChromatticEditorExtension extends Extension implements InitializeServicesHandler,
   JavaCodeAssistantErrorHandler, ProjectOpenedHandler, EditorActiveFileChangedHandler
{
   private ProjectModel currentProject;

   private JavaCodeAssistant groovyCodeAssistant;

   private JavaTokenWidgetFactory factory;
   
   private GroovyCodeValidator groovyCodeValidator;
   
   private GroovyCodeAssistantService service;

   private static final Images IMAGES = GWT.create(Images.class);

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      IDE.getInstance().addControl(
         new NewItemControl("File/New/New Data Object", "Data Object", "Create Data Object", Images.CHROMATTIC,
            MimeType.CHROMATTIC_DATA_OBJECT));
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
         new CodeMirrorProducer(MimeType.CHROMATTIC_DATA_OBJECT, "CodeMirror Data Object editor", "cmtc", IMAGES
            .chromattic(), true, new CodeMirrorConfiguration()
            .setGenericParsers("['parsegroovy.js', 'tokenizegroovy.js']")
            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/groovycolors.css']")
            .setParser(new GroovyParser()).setCanBeOutlined(true).setAutocompleteHelper(new GroovyAutocompleteHelper())
            .setCodeAssistant(groovyCodeAssistant).setCodeValidator(groovyCodeValidator)));

      IDE.getInstance().addOutlineItemCreator(MimeType.CHROMATTIC_DATA_OBJECT, new GroovyOutlineItemCreator());
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
         StringBuffer outputContent = new StringBuffer();
         outputContent.append("Error (<i>").append(exception.getHTTPStatus()).append("</i>: <i>")
            .append(exception.getStatusText()).append("</i>)");
         if (!exception.getMessage().equals(""))
         {
            outputContent.append("<br />").append(exception.getMessage().replace("\n", "<br />")); // replace "end of line"
                                                                                                   // symbols on "<br />"
         }

         IDE.fireEvent(new OutputEvent(outputContent.toString(), OutputMessage.Type.ERROR));
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent(exc.getMessage()));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      String projectId = event.getProject().getId();
      groovyCodeAssistant.setActiveProjectId(projectId);
      factory.setProjectId(projectId);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(final EditorActiveFileChangedEvent event)
   {
      FileModel file = event.getFile();
      if (file == null)
         return;

      final ProjectModel project = file.getProject() != null ? file.getProject() : currentProject;
      if (project != null)
      {
         groovyCodeAssistant.setActiveProjectId(project.getId());
         factory.setProjectId(project.getId());
      }

      if (!file.isPersisted())
      {
         return;
      }

      if (file.getMimeType().equals(MimeType.APPLICATION_GROOVY) || file.getMimeType().equals(MimeType.GROOVY_SERVICE)
         || file.getMimeType().equals(MimeType.CHROMATTIC_DATA_OBJECT))
      {
         List<Token> classes = new ArrayList<Token>();
         FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(classes);
         service.findClassesByProject(file.getId(), project.getId(),
            new AsyncRequestCallback<List<Token>>(unmarshaller)
            {

               @Override
               protected void onSuccess(List<Token> result)
               {
                  groovyCodeValidator.setClassesFromProject(result);
                  ((CodeMirror)event.getEditor()).forceValidateCode();
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
