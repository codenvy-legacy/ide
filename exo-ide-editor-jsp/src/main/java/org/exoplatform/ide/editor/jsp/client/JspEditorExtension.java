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
package org.exoplatform.ide.editor.jsp.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlOutlineItemCreator;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.java.client.codeassistant.JavaTokenWidgetFactory;
import org.exoplatform.ide.editor.java.client.codeassistant.services.CodeAssistantService;
import org.exoplatform.ide.editor.java.client.codeassistant.services.JavaCodeAssistantService;
import org.exoplatform.ide.editor.jsp.client.codeassistant.JspCodeAssistant;
import org.exoplatform.ide.editor.jsp.client.codemirror.JspAutocompleteHelper;
import org.exoplatform.ide.editor.jsp.client.codemirror.JspCodeValidator;
import org.exoplatform.ide.editor.jsp.client.codemirror.JspParser;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyEditorExtension Mar 10, 2011 3:48:59 PM evgen $
 *
 */
public class JspEditorExtension extends Extension implements InitializeServicesHandler, JavaCodeAssistantErrorHandler,
   EditorActiveFileChangedHandler
{

   private JspCodeAssistant jspCodeAssistant;

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.EVENT_BUS.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      IDE.getInstance().addControl(
         new NewItemControl("File/New/New JSP File", "JSP", "Create JSP", Images.JSP, MimeType.APPLICATION_JSP).setGroup(2),
         DockTarget.NONE, false);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      CodeAssistantService service;
      if (JavaCodeAssistantService.get() == null)
         service =
            new JavaCodeAssistantService(IDE.EVENT_BUS, event.getApplicationConfiguration().getContext(),
               event.getLoader());
      else
         service = JavaCodeAssistantService.get();

      jspCodeAssistant =
         new JspCodeAssistant(service, new JavaTokenWidgetFactory(event.getApplicationConfiguration().getContext()
            + "/ide/code-assistant/java/class-doc?fqn="), this);

      IDE.getInstance()
         .addEditor(
            new CodeMirrorProducer(
               MimeType.APPLICATION_JSP,
               "CodeMirror JSP file editor",
               "jsp",
               Images.INSTANCE.jsp(),
               true,
               new CodeMirrorConfiguration().
                  setGenericParsers("['parsejsp.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'tokenizejava.js', 'parsejava.js', 'parsejspmixed.js']").
                  setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jspcolors.css', '" + CodeMirrorConfiguration.PATH
                     + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css', '"
                     + CodeMirrorConfiguration.PATH + "css/javacolors.css']").
                  setParser(new JspParser()).
                  setCanBeOutlined(true).
                  setAutocompleteHelper(new JspAutocompleteHelper()).
                  setCodeAssistant(jspCodeAssistant).
                  setCodeValidator(new JspCodeValidator()).
                  setCanHaveSeveralMimeTypes(true)
            )
         );

      IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_JSP, new HtmlOutlineItemCreator());
   }

   /**
    * @see org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistantErrorHandler#handleError(java.lang.Throwable)
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

         IDE.EVENT_BUS.fireEvent(new OutputEvent(outputContent, OutputMessage.Type.ERROR));
      }
      else
      {
         IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exc.getMessage()));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() != null)
         jspCodeAssistant.setactiveFileHref(event.getFile().getId());
   }

}
