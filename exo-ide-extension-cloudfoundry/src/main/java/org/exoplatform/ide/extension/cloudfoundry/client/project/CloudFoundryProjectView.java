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
package org.exoplatform.ide.extension.cloudfoundry.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientBundle;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * View for managing project, deployed on CloudFoundry.
 * View must be pointed in <b>Views.gwt.xml</b>
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 7, 2011 3:10:55 PM anya $
 *
 */
public class CloudFoundryProjectView extends ViewImpl implements CloudFoundryProjectPresenter.Display
{
   private static final int WIDTH = 600;

   private static final int HEIGHT = 360;

   private static final String ID = "ideCloudFoundryProjectView";

   private static final String DELETE_BUTTON_ID = "ideCloudFoundryProjectViewDeleteButton";

   private static final String UPDATE_BUTTON_ID = "ideCloudFoundryProjectViewUpdateButton";

   private static final String INFO_BUTTON_ID = "ideCloudFoundryProjectViewInfoButton";

   private static final String CLOSE_BUTTON_ID = "ideCloudFoundryProjectViewCloseButton";

   private static final String START_BUTTON_ID = "ideCloudFoundryProjectViewStartButton";

   private static final String STOP_BUTTON_ID = "ideCloudFoundryProjectViewStopButton";

   private static final String RESTART_BUTTON_ID = "ideCloudFoundryProjectViewRestartButton";

   private static final String EDIT_MEMORY_BUTTON_ID = "ideCloudFoundryProjectViewEditMemoryButton";

   private static final String EDIT_INSTANCES_BUTTON_ID = "ideCloudFoundryProjectViewEditInstancesButton";

   private static final String EDIT_URL_BUTTON_ID = "ideCloudFoundryProjectViewEditUrlButton";

   private static final String NAME_FIELD_ID = "ideCloudFoundryProjectViewNameField";

   private static final String URL_FIELD_ID = "ideCloudFoundryProjectViewUrlField";

   private static final String MODEL_FIELD_ID = "ideCloudFoundryProjectViewModelField";

   private static final String INSTANCES_FIELD_ID = "ideCloudFoundryProjectViewInstancesField";

   private static final String MEMORY_FIELD_ID = "ideCloudFoundryProjectViewMemoryField";

   private static final String STACK_FIELD_ID = "ideCloudFoundryProjectViewStackField";

   private static CloudFoundryProjectViewUiBinder uiBinder = GWT.create(CloudFoundryProjectViewUiBinder.class);

   interface CloudFoundryProjectViewUiBinder extends UiBinder<Widget, CloudFoundryProjectView>
   {
   }

   @UiField
   Button deleteButton;

   @UiField
   Button updateButton;

   @UiField
   ImageButton infoButton;

   @UiField
   ImageButton closeButton;

   @UiField
   TextInput nameField;

   @UiField
   Anchor urlField;

   @UiField
   Label modelField;

   @UiField
   Label stackField;

   @UiField
   TextInput instancesField;

   @UiField
   TextInput memoryField;

   @UiField
   ImageButton startButton;

   @UiField
   ImageButton stopButton;

   @UiField
   ImageButton restartButton;

   @UiField
   ImageButton editUrlsButton;

   @UiField
   ImageButton editMemoryButton;

   @UiField
   ImageButton editInstancesButton;

   public CloudFoundryProjectView()
   {
      super(ID, ViewType.MODAL, CloudFoundryExtension.LOCALIZATION_CONSTANT.manageProjectViewTitle(), new Image(
         CloudFoundryClientBundle.INSTANCE.cloudFoundry()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      deleteButton.getElement().setId(DELETE_BUTTON_ID);
      updateButton.getElement().setId(UPDATE_BUTTON_ID);
      closeButton.setButtonId(CLOSE_BUTTON_ID);
      infoButton.setButtonId(INFO_BUTTON_ID);

      startButton.setButtonId(START_BUTTON_ID);
      stopButton.setButtonId(STOP_BUTTON_ID);
      restartButton.setButtonId(RESTART_BUTTON_ID);

      editInstancesButton.setButtonId(EDIT_INSTANCES_BUTTON_ID);
      editMemoryButton.setButtonId(EDIT_MEMORY_BUTTON_ID);
      editUrlsButton.setButtonId(EDIT_URL_BUTTON_ID);

      nameField.setName(NAME_FIELD_ID);
      urlField.setName(URL_FIELD_ID);
      modelField.setID(MODEL_FIELD_ID);
      stackField.setID(STACK_FIELD_ID);
      instancesField.setName(INSTANCES_FIELD_ID);
      memoryField.setName(MEMORY_FIELD_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getUpdateButton()
    */
   @Override
   public HasClickHandlers getUpdateButton()
   {
      return updateButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getDeleteButton()
    */
   @Override
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getInfoButton()
    */
   @Override
   public HasClickHandlers getInfoButton()
   {
      return infoButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getApplicationName()
    */
   @Override
   public HasValue<String> getApplicationName()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#setApplicationURLs(java.util.List)
    */
   @Override
   public void setApplicationURL(String url)
   {
      url = (url != null && url.startsWith("http://")) ? url : "http://" + url;
      urlField.setHref(url);
      urlField.setText(url);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getApplicationModel()
    */
   @Override
   public HasValue<String> getApplicationModel()
   {
      return modelField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getApplicationStack()
    */
   @Override
   public HasValue<String> getApplicationStack()
   {
      return stackField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getApplicationInstances()
    */
   @Override
   public HasValue<String> getApplicationInstances()
   {
      return instancesField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getApplicationMemory()
    */
   @Override
   public HasValue<String> getApplicationMemory()
   {
      return memoryField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getStartButton()
    */
   @Override
   public HasClickHandlers getStartButton()
   {
      return startButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getStopButton()
    */
   @Override
   public HasClickHandlers getStopButton()
   {
      return stopButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getRestartButton()
    */
   @Override
   public HasClickHandlers getRestartButton()
   {
      return restartButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getEditMemoryButton()
    */
   @Override
   public HasClickHandlers getEditMemoryButton()
   {
      return editMemoryButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getEditURLButton()
    */
   @Override
   public HasClickHandlers getEditURLButton()
   {
      return editUrlsButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getEditInstancesButton()
    */
   @Override
   public HasClickHandlers getEditInstancesButton()
   {
      return editInstancesButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#setStartButtonEnabled(boolean)
    */
   @Override
   public void setStartButtonEnabled(boolean enabled)
   {
      startButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#setStopButtonEnabled(boolean)
    */
   @Override
   public void setStopButtonEnabled(boolean enabled)
   {
      stopButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#setRestartButtonEnabled(boolean)
    */
   @Override
   public void setRestartButtonEnabled(boolean enabled)
   {
      restartButton.setEnabled(enabled);
   }
}
