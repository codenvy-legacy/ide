/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.beanstalk.application;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 12:14:23 PM anya $
 * 
 */
public class MainTabPain extends Composite
{

   private static MainTabPainUiBinder uiBinder = GWT.create(MainTabPainUiBinder.class);

   interface MainTabPainUiBinder extends UiBinder<Widget, MainTabPain>
   {
   }

   private static final String NAME_FIELD_ID = "ideMainTabPainNameField";

   private static final String URL_FIELD_ID = "ideMainTabPainURLField";

   private static final String DESCRIPTION_FIELD_ID = "ideMainTabPainDescriptionField";

   private static final String CREATION_DATE_FIELD_ID = "ideMainTabPainCreateDateField";

   private static final String UPDATED_DATE_FIELD_ID = "ideMainTabPainUpdatedDateField";

   private static final String EDIT_DESCRIPTION_BUTTON_ID = "ideMainTabPainEditDescriptionButton";

   private static final String DELETE_APPLICATION_BUTTON_ID = "ideMainTabPainDeleteApplicationButton";

   private static final String CREATE_VERSION_BUTTON_ID = "ideMainTabPainCreateVersionButton";

   private static final String LAUNCH_ENVIRONMENT_BUTTON_ID = "ideMainTabPainLaunchEnvButton";

   @UiField
   TextInput nameField;

   @UiField
   Label urlField;

   @UiField
   TextInput descriptionField;

   @UiField
   ImageButton editDescriptionButton;

   @UiField
   ImageButton deleteApplicationButton;

   @UiField
   ImageButton createVersionButton;

   @UiField
   ImageButton launchEnvironmentButton;

   @UiField
   Label creationDateField;

   @UiField
   Label updatedDateField;

   public MainTabPain()
   {
      initWidget(uiBinder.createAndBindUi(this));

      nameField.setName(NAME_FIELD_ID);
      urlField.setID(URL_FIELD_ID);
      urlField.setIsHTML(true);
      descriptionField.setName(DESCRIPTION_FIELD_ID);
      creationDateField.setID(CREATION_DATE_FIELD_ID);
      updatedDateField.setID(UPDATED_DATE_FIELD_ID);

      editDescriptionButton.setButtonId(EDIT_DESCRIPTION_BUTTON_ID);
      deleteApplicationButton.setButtonId(DELETE_APPLICATION_BUTTON_ID);
      createVersionButton.setButtonId(CREATE_VERSION_BUTTON_ID);
      launchEnvironmentButton.setButtonId(LAUNCH_ENVIRONMENT_BUTTON_ID);
   }

   /**
    * @return the nameField
    */
   public TextInput getNameField()
   {
      return nameField;
   }

   /**
    * @return the urlField
    */
   public Label getUrlField()
   {
      return urlField;
   }

   /**
    * @return the descriptionField
    */
   public TextInput getDescriptionField()
   {
      return descriptionField;
   }

   /**
    * @return the editDescriptionButton
    */
   public ImageButton getEditDescriptionButton()
   {
      return editDescriptionButton;
   }

   /**
    * @return the deleteApplicationButton
    */
   public ImageButton getDeleteApplicationButton()
   {
      return deleteApplicationButton;
   }

   /**
    * @return the creationDateField
    */
   public Label getCreationDateField()
   {
      return creationDateField;
   }

   /**
    * @return the updatedDateField
    */
   public Label getUpdatedDateField()
   {
      return updatedDateField;
   }

   /**
    * @return the createVersionButton
    */
   public ImageButton getCreateVersionButton()
   {
      return createVersionButton;
   }

   /**
    * @return the launchEnvironmentButton
    */
   public ImageButton getLaunchEnvironmentButton()
   {
      return launchEnvironmentButton;
   }
}
