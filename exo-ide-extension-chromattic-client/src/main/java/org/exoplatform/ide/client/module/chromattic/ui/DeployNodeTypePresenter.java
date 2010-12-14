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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.event.shared.HandlerManager;

import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.module.chromattic.event.DeployNodeTypeEvent;
import org.exoplatform.ide.client.module.chromattic.event.DeployNodeTypeHandler;
import org.exoplatform.ide.client.module.chromattic.model.EnumAlreadyExistsBehaviour;
import org.exoplatform.ide.client.module.chromattic.model.EnumNodeTypeFormat;
import org.exoplatform.ide.client.module.chromattic.model.service.ChrommaticService;
import org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeDeployResultReceivedEvent;
import org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeDeployResultReceivedHandler;
import org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeGenerationResultReceivedEvent;
import org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeGenerationResultReceivedHandler;

import java.util.LinkedHashMap;

import com.google.gwt.event.dom.client.HasClickHandlers;

/**
 * Presenter for deploy node type view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 9, 2010 $
 *
 */
public class DeployNodeTypePresenter implements DeployNodeTypeHandler, NodeTypeDeployResultReceivedHandler,
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
       * Get generate button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getGenerateButton();

      /**
       * Get dependency location.
       * 
       * @return {@link TextFieldItem}
       */
      TextFieldItem getDependencyLocation();

      /**
       * Get groovy location.
       * 
       * @return {@link TextFieldItem}
       */
      TextFieldItem getLocation();

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

      void setActionIfExistValues(LinkedHashMap<String, String> values);

      void updateDeployButtonState(boolean isEnabled);

      void closeView();
   }

   private Display display;

   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   private String generatedNodeType;

   public DeployNodeTypePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(DeployNodeTypeEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

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

      display.getGenerateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doGenerate();
         }
      });

      display.getDeployButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doDeploy();
         }
      });

   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeDeployResultReceivedHandler#onNodeTypeDeployResultReceived(org.exoplatform.ide.client.module.chromattic.model.service.event.NodeTypeDeployResultReceivedEvent)
    */
   @Override
   public void onNodeTypeDeployResultReceived(NodeTypeDeployResultReceivedEvent event)
   {
      handlers.removeHandler(NodeTypeDeployResultReceivedEvent.TYPE);
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
      display.updateDeployButtonState(false);

      display.getLocation().setValue(activeFile.getHref());
      display.setNodeTypeFormatValues(EnumNodeTypeFormat.getValues());
      LinkedHashMap<String, String> alreadyExistsBehaviourValues = new LinkedHashMap<String, String>();
      for (EnumAlreadyExistsBehaviour value : EnumAlreadyExistsBehaviour.values())
      {
         alreadyExistsBehaviourValues.put(String.valueOf(value.getCode()), value.getDisplayName());
      }

      display.setActionIfExistValues(alreadyExistsBehaviourValues);

   }

   private void doDeploy()
   {
      handlers.addHandler(NodeTypeDeployResultReceivedEvent.TYPE, this);
      EnumNodeTypeFormat nodeTypeFormat = EnumNodeTypeFormat.valueOf(display.getNodeTypeFormat().getValue());
      EnumAlreadyExistsBehaviour alreadyExistsBehaviour =
         EnumAlreadyExistsBehaviour.fromCode(Integer.valueOf(display.getActionIfExist().getValue()));
      ChrommaticService.getInstance().createNodeType(generatedNodeType, nodeTypeFormat, alreadyExistsBehaviour);
   }

   private void doGenerate()
   {
      handlers.addHandler(NodeTypeGenerationResultReceivedEvent.TYPE, this);
      String dependencyLocation = display.getDependencyLocation().getValue();
      EnumNodeTypeFormat nodeTypeFormat = EnumNodeTypeFormat.valueOf(display.getNodeTypeFormat().getValue());
      ChrommaticService.getInstance().generateNodeType(display.getLocation().getValue(), dependencyLocation,
         nodeTypeFormat);
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

      display.updateDeployButtonState(true);
      generatedNodeType = event.getGenerateNodeTypeResult().getNodeTypeDefinition();
   }

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

            if (serverException != null)
            {
               html += "<br><hr><br>Possible reasons:<br>" + serverException.getMessage();
            }
            return html;
         }
      }
      else
      {
         return exception.getMessage();
      }
   }

   /**
    * 
    * 
    * @param errorMessage message with error
    */
   private void showErrorInOutput(String errorMessage)
   {
      errorMessage =  errorMessage.replace("\n", "<br>");
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
