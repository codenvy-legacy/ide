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
package org.exoplatform.ide.extension.appfog.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;

/**
 * View for creating application on Appfog.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateApplicationView extends ViewImpl implements CreateApplicationPresenter.Display
{
   private static final String ID = "ideAppfogCreateAppView";

   private static final int WIDTH = 500;

   private static final int HEIGHT = 370;

   private static final String CREATE_BUTTON_ID = "ideAppfogAppViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideAppfogAppViewCancelButton";

   private static final String TYPE_FIELD_ID = "ideAppfogAppViewTypeField";

   private static final String NAME_FIELD_ID = "ideAppfogAppViewNameField";

   private static final String URL_FIELD_ID = "ideAppfogAppViewUrlField";

   private static final String INSTANCES_FIELD_ID = "ideAppfogAppViewInstancesField";

   private static final String MEMORY_FIELD_ID = "ideAppfogAppViewMemoryField";

   private static final String SERVER_FIELD_ID = "ideAppfogAppViewServerField";

   private static final String INFRA_FIELD_ID = "ideAppfogAppViewInfraField";

   private static CreateApplicationViewUiBinder uiBinder = GWT.create(CreateApplicationViewUiBinder.class);

   interface CreateApplicationViewUiBinder extends UiBinder<Widget, CreateApplicationView>
   {
   }

   /**
    * Server field (location of Appfog instance where application must be created, e.g.
    *           http://api.Appfog.com)
    */
   @UiField
   ComboBoxField serverField;

   @UiField
   ComboBoxField infraField;

   /**
    * Application type field.
    */
   @UiField
   SelectItem typeField;

   /**
    * Checkbox, that indicate, is type will be detected automatically
    * or selected by user.
    */
   @UiField
   CheckBox changeTypeField;

   /**
    * Application name field.
    */
   @UiField
   TextInput nameField;

   /**
    * Application URL field.
    */
   @UiField
   TextInput urlField;

   /**
    * Checkbox, that indicate, is URL will be added automatically
    * or set by user.
    */
   @UiField
   CheckBox customUrlField;

   /**
    * Number of instanses of application field.
    */
   @UiField
   TextInput instansesField;

   /**
    * Memory field (needed for application).
    */
   @UiField
   TextInput memoryField;

   /**
    * Is start application after creation.
    */
   @UiField
   CheckBox startAfterCreationField;

   /**
    * Create application button.
    */
   @UiField
   ImageButton createButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   public CreateApplicationView()
   {
      super(ID, ViewType.MODAL, AppfogExtension.LOCALIZATION_CONSTANT.createApplicationTitle(), null, WIDTH,
         HEIGHT);
      add(uiBinder.createAndBindUi(this));

      serverField.setName(SERVER_FIELD_ID);
      typeField.setName(TYPE_FIELD_ID);
      nameField.setName(NAME_FIELD_ID);
      urlField.setName(URL_FIELD_ID);
      instansesField.setName(INSTANCES_FIELD_ID);
      memoryField.setName(MEMORY_FIELD_ID);
      createButton.setButtonId(CREATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
      infraField.setName(INFRA_FIELD_ID);
   }

   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public HasValue<String> getTypeField()
   {
      return typeField;
   }

   @Override
   public HasValue<String> getNameField()
   {
      return nameField;
   }

   @Override
   public void enableCreateButton(boolean enable)
   {
      createButton.setEnabled(enable);
   }

   @Override
   public void focusInNameField()
   {
      nameField.focus();
   }

   @Override
   public HasValue<String> getUrlField()
   {
      return urlField;
   }

   @Override
   public HasValue<String> getInfraField()
   {
      return infraField;
   }

   @Override
   public void setTypeValues(String[] domains)
   {
      typeField.clearValue();
      typeField.setValueMap(domains);
   }

   @Override
   public HasValue<String> getInstancesField()
   {
      return instansesField;
   }

   @Override
   public HasValue<String> getMemoryField()
   {
      return memoryField;
   }

   @Override
   public HasValue<Boolean> getAutodetectTypeCheckItem()
   {
      return changeTypeField;
   }

   @Override
   public HasValue<Boolean> getUrlCheckItem()
   {
      return customUrlField;
   }

   @Override
   public HasValue<Boolean> getIsStartAfterCreationCheckItem()
   {
      return startAfterCreationField;
   }

   @Override
   public void setIsStartAfterCreationCheckItem(boolean start)
   {
      startAfterCreationField.setValue(start);
   }

   @Override
   public void enableTypeField(boolean enable)
   {
      typeField.setEnabled(enable);
   }

   @Override
   public void enableUrlField(boolean enable)
   {
      urlField.setEnabled(enable);
   }

   @Override
   public void enableMemoryField(boolean enable)
   {
      memoryField.setEnabled(enable);
   }

   @Override
   public void setSelectedIndexForTypeSelectItem(int index)
   {
      typeField.setSelectedIndex(index);
   }

   @Override
   public void focusInUrlField()
   {
      urlField.setFocus(true);
   }

   @Override
   public HasValue<String> getServerField()
   {
      return serverField;
   }

   @Override
   public void setServerValues(String[] servers)
   {
      serverField.setValueMap(servers);
   }

   @Override
   public void setInfraValues(String[] infras)
   {
      infraField.setValueMap(infras);
   }

   @Override
   public void enableAutodetectTypeCheckItem(boolean enable)
   {
      if (changeTypeField.getValue() != enable)
      {
         changeTypeField.setValue(enable, true);
      }
   }

}
