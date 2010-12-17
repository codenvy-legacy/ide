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
package org.exoplatform.ide.client.module.chromattic.ui;

import com.google.gwt.user.client.Command;

import com.google.gwt.user.client.DeferredCommand;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.gwtframework.editor.event.EditorInitializedEvent;
import org.exoplatform.gwtframework.editor.event.EditorInitializedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.event.CloseViewEvent;
import org.exoplatform.ide.client.framework.ui.event.OpenViewEvent;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.event.ViewOpenedHandler;
import org.exoplatform.ide.client.module.chromattic.event.GenerateNodeTypeEvent;
import org.exoplatform.ide.client.module.chromattic.event.GenerateNodeTypeHandler;
import org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeGenerationResultReceivedEvent;
import org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeGenerationResultReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Presenter for the preview of the generated node type definition.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 8, 2010 $
 *
 */
public class GeneratedNodeTypePreviewPresenter implements EditorInitializedHandler, EditorActiveFileChangedHandler,
   ViewClosedHandler, NodeTypeGenerationResultReceivedHandler, ViewOpenedHandler, GenerateNodeTypeHandler
{
   interface Display
   {
      /**
       * Close the view.
       */
      void closeView();

      /**
       * Set content to be displayed in editor. 
       * 
       * @param content
       */
      void setContent(String content);

      /**
       * Get editor.
       * 
       * @return {@link TextEditor} editor
       */
      TextEditor getEditor();
   }

   /**
    * Display.
    */
   private Display display;

   /**
    * Handler manager.
    */
   private HandlerManager eventBus;

   /**
    * The content of generated node type definition.
    */
   private String generatedNodeType;

   /**
    * Handlers of this presenter.
    */
   private Handlers handlers;

   /**
    * The view state : opened or not.
    */
   private boolean isOpened = false;

   /**
    * @param eventBus handler manager
    */
   public GeneratedNodeTypePreviewPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(GenerateNodeTypeEvent.TYPE, this);

      handlers = new Handlers(eventBus);
   }
   
   /**
    * Bind view with presenter.
    * 
    * @param d display
    */
   public void bindDisplay(Display d)
   {
      display = d;
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorInitializedHandler#onEditorInitialized(org.exoplatform.gwtframework.editor.event.EditorInitializedEvent)
    */
   @Override
   public void onEditorInitialized(EditorInitializedEvent event)
   {
      if (display.getEditor().getEditorId().equals(event.getEditorId()))
      {
         DeferredCommand.addCommand(new Command()
         {

            @Override
            public void execute()
            {
               display.setContent(generatedNodeType);
            }
         });
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      eventBus.fireEvent(new CloseViewEvent(GeneratedNodeTypePreviewForm.ID));
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (GeneratedNodeTypePreviewForm.ID.equals(event.getViewId()))
      {
         isOpened = false;
         handlers.removeHandlers();
      }
      else if (GenerateNodeTypeForm.ID.equals(event.getViewId()))
      {
         handlers.removeHandler(NodeTypeGenerationResultReceivedEvent.TYPE);
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeGenerationResultReceivedHandler#onNodeTypeGenerationResultReceived(org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeGenerationResultReceivedEvent)
    */
   @Override
   public void onNodeTypeGenerationResultReceived(NodeTypeGenerationResultReceivedEvent event)
   {
      handlers.removeHandler(NodeTypeGenerationResultReceivedEvent.TYPE);
      if (event.getException() != null)
      {
         if (event.getException().getMessage() != null
            && event.getException().getMessage().startsWith("startup failed"))
         {
            showErrorInOutput(event.getException().getMessage());
            return;
         }
         else
         {
            Dialogs.getInstance().showError(getErrorMessage(event.getException()));
            return;
         }

      }
      generatedNodeType = event.getGenerateNodeTypeResult().getNodeTypeDefinition();
      if (isOpened)
      {
         display.setContent(generatedNodeType);
      }
      else
      {
         handlers.addHandler(EditorInitializedEvent.TYPE, this);
         handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
         handlers.addHandler(ViewClosedEvent.TYPE, this);

         final Display view = new GeneratedNodeTypePreviewForm(eventBus);
         bindDisplay(view);
         eventBus.fireEvent(new OpenViewEvent((View)view));
      }
   }

   /**
   * Forms the error message to be displayed 
   * for user.
   * 
   * @param exception exception
   * @return {@link String} formed message to display
   */
   private String getErrorMessage(Throwable exception)
   {
      if (exception instanceof ServerException)
      {
         ServerException serverException = (ServerException)exception;
         if (serverException.isErrorMessageProvided())
         {
            String html =
               "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText() + "<br><br><hr><br>"
                  + serverException.getMessage();
            return html;
         }
         else
         {
            String html = "" + serverException.getHTTPStatus() + "&nbsp;" + serverException.getStatusText();
            return html;
         }
      }
      else
      {
         return exception.getMessage();
      }
   }

   /**
    * Show error message in output form.
    * 
    * @param errorMessage error message
    */
   private void showErrorInOutput(String errorMessage)
   {
      errorMessage = errorMessage.replace("\n", "<br>");
      eventBus.fireEvent(new OutputEvent(errorMessage, OutputMessage.Type.ERROR));
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.event.ViewOpenedEvent)
    */
   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (GeneratedNodeTypePreviewForm.ID.equals(event.getViewId()))
      {
         isOpened = true;
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.event.GenerateNodeTypeHandler#onGenerateNodeType(org.exoplatform.ide.client.module.chromattic.event.GenerateNodeTypeEvent)
    */
   @Override
   public void onGenerateNodeType(GenerateNodeTypeEvent event)
   {
      handlers.addHandler(NodeTypeGenerationResultReceivedEvent.TYPE, this);
   }
}
