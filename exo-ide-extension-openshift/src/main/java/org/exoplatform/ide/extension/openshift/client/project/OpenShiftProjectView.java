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
package org.exoplatform.ide.extension.openshift.client.project;

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
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;

/**
 * View for managing application, deployed on OpenShift. View must be pointed in <b>Views.gwt.xml</b>
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 8, 2011 9:40:02 AM anya $
 * 
 */
public class OpenShiftProjectView extends ViewImpl implements OpenShiftProjectPresenter.Display
{
   private static final int WIDTH = 580;

   private static final int HEIGHT = 240;

   private static final String ID = "ideOpenShiftProjectView";

   private static final String DELETE_BUTTON_ID = "ideOpenShiftProjectViewDeleteButton";

   private static final String PREVIEW_BUTTON_ID = "ideOpenShiftProjectViewPreviewButton";

   private static final String INFO_BUTTON_ID = "ideOpenShiftProjectViewInfoButton";

   private static final String CLOSE_BUTTON_ID = "ideOpenShiftProjectViewCloseButton";

   private static final String NAME_FIELD_ID = "ideOpenShiftProjectViewNameField";

   private static final String URL_FIELD_ID = "ideOpenShiftProjectViewUrlField";

   private static final String TYPE_FIELD_ID = "ideOpenShiftProjectViewTypeField";

   private static OpenShiftProjectViewUiBinder uiBinder = GWT.create(OpenShiftProjectViewUiBinder.class);

   @UiField
   Button deleteButton;

   @UiField
   Button previewButton;

   @UiField
   ImageButton infoButton;

   @UiField
   ImageButton closeButton;

   @UiField
   TextInput nameField;

   @UiField
   Anchor urlField;

   @UiField
   Label typeField;

   interface OpenShiftProjectViewUiBinder extends UiBinder<Widget, OpenShiftProjectView>
   {
   }

   public OpenShiftProjectView()
   {
      super(ID, ViewType.MODAL, OpenShiftExtension.LOCALIZATION_CONSTANT.manageProjectViewTitle(), new Image(
         OpenShiftClientBundle.INSTANCE.openShiftControl()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      deleteButton.getElement().setId(DELETE_BUTTON_ID);
      previewButton.getElement().setId(PREVIEW_BUTTON_ID);
      closeButton.setButtonId(CLOSE_BUTTON_ID);
      infoButton.setButtonId(INFO_BUTTON_ID);
      nameField.setName(NAME_FIELD_ID);
      urlField.setName(URL_FIELD_ID);
      typeField.setID(TYPE_FIELD_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getDeleteButton()
    */
   @Override
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getApplicationName()
    */
   @Override
   public HasValue<String> getApplicationName()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#setApplicationURL(java.lang.String)
    */
   @Override
   public void setApplicationURL(String URL)
   {
      urlField.setHref(URL);
      urlField.setText(URL);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getInfoButton()
    */
   @Override
   public HasClickHandlers getInfoButton()
   {
      return infoButton;
   }

   @Override
   public HasClickHandlers getPreviewButton()
   {
      return previewButton;
   }

   @Override
   public HasValue<String> getApplicationType()
   {
      return typeField;
   }
}
