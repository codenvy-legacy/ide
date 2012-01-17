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
package org.exoplatform.ide.editor.java.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.copy.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesList;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistant;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaTokenWidgetFactory;
import org.exoplatform.ide.editor.java.client.codeassistant.services.JavaCodeAssistantService;
import org.exoplatform.ide.editor.java.client.codemirror.JavaAutocompleteHelper;
import org.exoplatform.ide.editor.java.client.codemirror.JavaCodeValidator;
import org.exoplatform.ide.editor.java.client.codemirror.JavaOutlineItemCreator;
import org.exoplatform.ide.editor.java.client.codemirror.JavaParser;
import org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter;
import org.exoplatform.ide.editor.java.client.create.NewJavaClassControl;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyEditorExtension Mar 10, 2011 3:48:59 PM evgen $
 * 
 */
public class JavaEditorExtension extends Extension implements InitializeServicesHandler, JavaCodeAssistantErrorHandler,
   EditorActiveFileChangedHandler, ProjectOpenedHandler
{

   public static final JavaConstants MESSAGES = GWT.create(JavaConstants.class);
   
   public static final JavaCodeAssistantAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(JavaCodeAssistantAutoBeanFactory.class);

   private JavaCodeAssistant javaCodeAssistant;

   private JavaCodeValidator javaCodeValidator;

   private JavaTokenWidgetFactory factory;

   private JavaCodeAssistantService service;

   private String projectId;

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);

      IDE.getInstance().addControl(new NewJavaClassControl());

      JavaClientBundle.INSTANCE.css().ensureInjected();

      new CreateJavaClassPresenter();
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {

      if (JavaCodeAssistantService.get() == null)
         service = new JavaCodeAssistantService(event.getApplicationConfiguration().getContext(), event.getLoader());
      else
         service = JavaCodeAssistantService.get();

      factory =
         new JavaTokenWidgetFactory(event.getApplicationConfiguration().getContext()
            + "/ide/code-assistant/java/class-doc?fqn=");
      javaCodeAssistant = new JavaCodeAssistant(service, factory, this);

      javaCodeValidator = new JavaCodeValidator(service, this);

      IDE.getInstance().addEditor(
         new CodeMirrorProducer(MimeType.APPLICATION_JAVA, "CodeMirror Java file editor", "java",
            JavaClientBundle.INSTANCE.java(), true, new CodeMirrorConfiguration()
               .setGenericParsers("['parsejava.js', 'tokenizejava.js']")
               .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/javacolors.css']")
               .setParser(new JavaParser()).setCanBeOutlined(true).setAutocompleteHelper(new JavaAutocompleteHelper())
               .setCodeAssistant(javaCodeAssistant).setCodeValidator(javaCodeValidator)));

      IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_JAVA, new JavaOutlineItemCreator());
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
      if (event.getFile() != null && event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA))
      {
         AutoBean<TypesList> autoBean = JavaEditorExtension.AUTO_BEAN_FACTORY.types();
         AutoBeanUnmarshaller<TypesList> unmarshaller = new AutoBeanUnmarshaller<TypesList>(autoBean);
         service.findClassesByProject(event.getFile().getId(), projectId, new AsyncRequestCallback<TypesList>(unmarshaller)
         {
            @Override
            protected void onSuccess(TypesList result)
            {
               javaCodeValidator.setClassesFromProject(JavaCodeAssistantUtils.types2tokens(result)); 
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

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      projectId = event.getProject().getId();
      javaCodeAssistant.setActiveProjectId(projectId);
      factory.setProjectId(projectId);
   }

}
