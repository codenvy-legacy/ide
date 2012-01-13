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

import java.util.LinkedHashMap;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.chromattic.client.ChromatticExtension;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 * 
 */
public class DeployNodeTypeView extends ViewImpl implements DeployNodeTypePresenter.Display
{

   private static DeployNodeTypeViewUiBinder uiBinder = GWT.create(DeployNodeTypeViewUiBinder.class);

   interface DeployNodeTypeViewUiBinder extends UiBinder<Widget, DeployNodeTypeView>
   {
   }

   @UiField
   VerticalPanel form;

   /**
    * Node type format select field.
    */
   @UiField
   SelectItem formatField;

   /**
    * Already exist behavior select field.
    */
   @UiField
   SelectItem alreadyExistBehaviorField;

   /**
    * Button for deploy operation.
    */
   @UiField
   ImageButton deployButton;

   /**
    * Button to cancel and close deploy window.
    */
   @UiField
   ImageButton cancelButton;

   public static final int WIDTH = 475;

   public static final int HEIGHT = 180;

   // IDs for Selenium tests
   private static final String ID = "ideDeployNodeTypeView";

   private final String CANCEL_BUTTON_ID = "ideDeployNodeTypeViewCancelButton";

   private final String DEPLOY_BUTTON_ID = "ideDeployNodeTypeViewDeployButton";

   private final String DYNAMIC_FORM_ID = "ideDeployNodeTypeViewDynamicForm";

   private final String FORMAT_FIELD_ID = "ideDeployNodeTypeViewFormatField";

   private final String ALREADY_EXIST_BEHAVIOR_FIELD_ID = "ideDeployNodeTypeViewAlreadyExistBehaviorField";

   public DeployNodeTypeView()
   {
      super(ID, ViewType.MODAL, ChromatticExtension.LOCALIZATION_CONSTANT.deployNodeTypeViewTitle(), null, WIDTH,
         HEIGHT);
      add(uiBinder.createAndBindUi(this));

      form.getElement().setId(DYNAMIC_FORM_ID);

      formatField.setName(FORMAT_FIELD_ID);

      alreadyExistBehaviorField.setName(ALREADY_EXIST_BEHAVIOR_FIELD_ID);

      deployButton.setButtonId(DEPLOY_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getDeployButton()
    */
   @Override
   public HasClickHandlers getDeployButton()
   {
      return deployButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getNodeTypeFormat()
    */
   @Override
   public HasValue<String> getNodeTypeFormat()
   {
      return formatField;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getActionIfExist()
    */
   @Override
   public HasValue<String> getActionIfExist()
   {
      return alreadyExistBehaviorField;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#setNodeTypeFormatValues(java.lang.String[])
    */
   @Override
   public void setNodeTypeFormatValues(String[] values)
   {
      formatField.setValueMap(values);
      if (values.length > 0)
      {
         formatField.setValue(values[0]);
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#setBehaviorIfExistValues(java.lang.String[])
    */
   @Override
   public void setBehaviorIfExistValues(LinkedHashMap<String, String> values)
   {
      alreadyExistBehaviorField.setValueMap(values);
   }

}
