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
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.java.client.codeassistant.services.JavaCodeAssistantService;
import org.exoplatform.ide.editor.java.client.control.NewJavaClassControl;
import org.exoplatform.ide.editor.java.client.create.CreateJavaPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

import com.google.collide.client.CollabEditor;
import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyEditorExtension Mar 10, 2011 3:48:59 PM evgen $
 * 
 */
public class JavaEditorExtension extends Extension implements InitializeServicesHandler, JavaCodeAssistantErrorHandler
{

   public static final JavaConstants MESSAGES = GWT.create(JavaConstants.class);

   public static final JavaCodeAssistantAutoBeanFactory AUTO_BEAN_FACTORY = GWT
      .create(JavaCodeAssistantAutoBeanFactory.class);

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);

      IDE.getInstance().addControl(new NewJavaClassControl());

      JavaClientBundle.INSTANCE.css().ensureInjected();

//      new CreateJavaClassPresenter();
      
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {

      if (JavaCodeAssistantService.get() == null)
         new JavaCodeAssistantService(event.getApplicationConfiguration().getContext(), event.getLoader());

      IDE.getInstance().getFileTypeRegistry().addFileType(
         new FileType(MimeType.APPLICATION_JAVA, "java", JavaClientBundle.INSTANCE.java()),
         new EditorCreator()
         {
            @Override
            public Editor createEditor()
            {
//               return new CodeMirror(MimeType.APPLICATION_JAVA, new CodeMirrorConfiguration()
//               .setGenericParsers("['parsejava.js', 'tokenizejava.js']")
//               .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/javacolors.css']")
//               .setCanBeOutlined(true));
               return new CollabEditor(MimeType.APPLICATION_JAVA);
            }
         });
      
//      IDE.getInstance().addEditor(
//         new CodeMirrorProducer(MimeType.APPLICATION_JAVA, "CodeMirror Java file editor", "java",
//            JavaClientBundle.INSTANCE.java(), true, new CodeMirrorConfiguration()
//               .setGenericParsers("['parsejava.js', 'tokenizejava.js']")
//               .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/javacolors.css']").setCanBeOutlined(true),
//            true));
      
//      IDE.getInstance().addEditor(new CodeMirror(MimeType.APPLICATION_JAVA, "CodeMirror Java file editor", "java", 
//         new CodeMirrorConfiguration()
//            .setGenericParsers("['parsejava.js', 'tokenizejava.js']")
//            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/javacolors.css']")
//            .setCanBeOutlined(true)
//      ));
      
      IDE.fireEvent(new AddCommentsModifierEvent(MimeType.APPLICATION_JAVA, new JavaCommentsModifier()));
      new CreateJavaPresenter(IDE.eventBus(),VirtualFileSystem.getInstance(), IDE.getInstance());
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

}
