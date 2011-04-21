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
package org.exoplatform.ide.extension.chromattic.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.editor.event.EditorInitializedEvent;
import org.exoplatform.gwtframework.editor.event.EditorInitializedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.extension.chromattic.client.event.GenerateNodeTypeEvent;
import org.exoplatform.ide.extension.chromattic.client.event.GenerateNodeTypeHandler;
import org.exoplatform.ide.extension.chromattic.client.model.service.event.NodeTypeGenerationResultReceivedEvent;
import org.exoplatform.ide.extension.chromattic.client.model.service.event.NodeTypeGenerationResultReceivedHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

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

   interface Display extends IsView
   {

      String ID = "ideGeneratedTypePreviewView";

      /**
       * Set content to be displayed in editor. 
       * 
       * @param content
       */
      void setContent(String content);

      /**
       * Get editor.
       * 
       * @return {@link Editor} editor
       */
      Editor getEditor();
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
    * Used to remove handlers when they are no longer needed.
    */
   private Map<GwtEvent.Type<?>, HandlerRegistration> handlerRegistrations =
      new HashMap<GwtEvent.Type<?>, HandlerRegistration>();

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
      eventBus.addHandler(GenerateNodeTypeEvent.TYPE, this);
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
         Scheduler.get().scheduleDeferred(new ScheduledCommand()
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
      IDE.getInstance().closeView(GeneratedNodeTypePreviewForm.ID);
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      System.out.println("GeneratedNodeTypePreviewPresenter.onViewClosed()");
      
//      if (GeneratedNodeTypePreviewForm.ID.equals(event.getViewId()))
//      {
//         isOpened = false;
//         removeHandlers();
//      }
//      else if (GenerateNodeTypeForm.ID.equals(event.getViewId()))
//      {
//         handlerRegistrations.get(NodeTypeGenerationResultReceivedEvent.TYPE).removeHandler();
//      }
   }

   private void removeHandlers()
   {
      eventBus.removeHandler(EditorInitializedEvent.TYPE, this);
      eventBus.removeHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.removeHandler(ViewClosedEvent.TYPE, this);
      eventBus.removeHandler(NodeTypeGenerationResultReceivedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeGenerationResultReceivedHandler#onNodeTypeGenerationResultReceived(org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeGenerationResultReceivedEvent)
    */
   @Override
   public void onNodeTypeGenerationResultReceived(NodeTypeGenerationResultReceivedEvent event)
   {
      handlerRegistrations.get(NodeTypeGenerationResultReceivedEvent.TYPE).removeHandler();
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
         handlerRegistrations.put(EditorInitializedEvent.TYPE, eventBus.addHandler(EditorInitializedEvent.TYPE, this));
         handlerRegistrations.put(EditorActiveFileChangedEvent.TYPE,
            eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this));
         handlerRegistrations.put(ViewClosedEvent.TYPE, eventBus.addHandler(ViewClosedEvent.TYPE, this));

         final Display view = new GeneratedNodeTypePreviewForm(eventBus);
         bindDisplay(view);
         IDE.getInstance().openView((View)view);
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
    * @see org.exoplatform.ide.client.framework.ui.api.event.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api.event.event.ViewOpenedEvent)
    */
   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (event.getView() instanceof Display)
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
      handlerRegistrations.put(NodeTypeGenerationResultReceivedEvent.TYPE,
         eventBus.addHandler(NodeTypeGenerationResultReceivedEvent.TYPE, this));
   }
}
