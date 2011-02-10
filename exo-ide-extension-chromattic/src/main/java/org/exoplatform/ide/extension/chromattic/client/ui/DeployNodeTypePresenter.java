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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.event.shared.HandlerManager;

import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.extension.chromattic.client.event.DeployNodeTypeEvent;
import org.exoplatform.ide.extension.chromattic.client.event.DeployNodeTypeHandler;
import org.exoplatform.ide.extension.chromattic.client.model.EnumAlreadyExistsBehaviour;
import org.exoplatform.ide.extension.chromattic.client.model.EnumNodeTypeFormat;
import org.exoplatform.ide.extension.chromattic.client.model.service.ChrommaticService;
import org.exoplatform.ide.extension.chromattic.client.model.service.event.NodeTypeCreationResultReceivedEvent;
import org.exoplatform.ide.extension.chromattic.client.model.service.event.NodeTypeCreationResultReceivedHandler;
import org.exoplatform.ide.extension.chromattic.client.model.service.event.NodeTypeGenerationResultReceivedEvent;
import org.exoplatform.ide.extension.chromattic.client.model.service.event.NodeTypeGenerationResultReceivedHandler;

import java.util.LinkedHashMap;

import com.google.gwt.event.dom.client.HasClickHandlers;

/**
 * Presenter for deploy node type view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 9, 2010 $
 *
 */
public class DeployNodeTypePresenter implements DeployNodeTypeHandler, NodeTypeCreationResultReceivedHandler,
   NodeTypeGenerationResultReceivedHandler, EditorActiveFileChangedHandler
{
   interface Display
   {
      /**
       * Get deploy button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getDeployButton();

      /**
       * Get cancel button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();

      /**
       * Get node type format.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getNodeTypeFormat();

      /**
       * Get action if node type exist.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getActionIfExist();

      /**
       * Set available values of node type format.
       * 
       * @param values values
       */
      void setNodeTypeFormatValues(String[] values);

      /**
       * Set values to the field with
       * available behavior if node exists.
       * 
       * @param values
       */
      void setBehaviorIfExistValues(LinkedHashMap<String, String> values);

      /**
       * Close view.
       */
      void closeView();
   }

   /**
    * The binded view.
    */
   private Display display;

   /**
    * Handler manager.
    */
   private HandlerManager eventBus;

   /**
    * List of handlers for this presenter.
    */
   private Handlers handlers;

   /**
    * Active file at the moment.
    */
   private File activeFile;

   public DeployNodeTypePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(DeployNodeTypeEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * Bind view with presenter.
    * 
    * @param d display
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            display.closeView();
         }
      });

      display.getDeployButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doGenerate();
         }
      });

   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeCreationResultReceivedHandler#onNodeTypeCreationResultReceived(org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeCreationResultReceivedEvent)
    */
   @Override
   public void onNodeTypeCreationResultReceived(NodeTypeCreationResultReceivedEvent event)
   {
      handlers.removeHandler(NodeTypeCreationResultReceivedEvent.TYPE);
      display.closeView();
      if (event.getException() != null)
      {
         Dialogs.getInstance().showError(getErrorMessage(event.getException()));
         return;
      }
      else
      {
         Dialogs.getInstance().showInfo("Node type successfully deployed.");
      }

   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.event.DeployNodeTypeHandler#onDeployNodeType(org.exoplatform.ide.client.module.chromattic.event.DeployNodeTypeEvent)
    */
   @Override
   public void onDeployNodeType(DeployNodeTypeEvent event)
   {
      if (activeFile == null)
         return;

      Display display = new DeployNodeTypeForm(eventBus);
      bindDisplay(display);

      display.setNodeTypeFormatValues(EnumNodeTypeFormat.getValues());
      LinkedHashMap<String, String> alreadyExistsBehaviourValues = new LinkedHashMap<String, String>();
      for (EnumAlreadyExistsBehaviour value : EnumAlreadyExistsBehaviour.values())
      {
         alreadyExistsBehaviourValues.put(String.valueOf(value.getCode()), value.getDisplayName());
      }

      display.setBehaviorIfExistValues(alreadyExistsBehaviourValues);

   }

   /**
    * Deploys generated node type definition to repository.
    * 
    * @param generatedNodeType generated node type definition
    */
   private void doDeploy(String generatedNodeType)
   {
      handlers.addHandler(NodeTypeCreationResultReceivedEvent.TYPE, this);
      EnumNodeTypeFormat nodeTypeFormat = EnumNodeTypeFormat.valueOf(display.getNodeTypeFormat().getValue());
      EnumAlreadyExistsBehaviour alreadyExistsBehaviour =
         EnumAlreadyExistsBehaviour.fromCode(Integer.valueOf(display.getActionIfExist().getValue()));
      ChrommaticService.getInstance().createNodeType(generatedNodeType, nodeTypeFormat, alreadyExistsBehaviour);
   }

   /**
    * Generates node type definition.
    */
   private void doGenerate()
   {
      if (activeFile == null)
         return;
      handlers.addHandler(NodeTypeGenerationResultReceivedEvent.TYPE, this);
      EnumNodeTypeFormat nodeTypeFormat = EnumNodeTypeFormat.valueOf(display.getNodeTypeFormat().getValue());
      ChrommaticService.getInstance().generateNodeType(activeFile.getHref(), nodeTypeFormat);
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

      String generatedNodeType = event.getGenerateNodeTypeResult().getNodeTypeDefinition();
      doDeploy(generatedNodeType);
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
    * Show error in Output form.
    * 
    * @param errorMessage message with error
    */
   private void showErrorInOutput(String errorMessage)
   {
      errorMessage = errorMessage.replace("\n", "<br>");
      eventBus.fireEvent(new OutputEvent(errorMessage, OutputMessage.Type.ERROR));
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

}
