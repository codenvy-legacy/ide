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
 * View for generating node type definition.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class GenerateNodeTypeView extends ViewImpl implements GenerateNodeTypePresenter.Display
{

   private static GenerateNodeTypeViewUiBinder uiBinder = GWT.create(GenerateNodeTypeViewUiBinder.class);
   
   interface GenerateNodeTypeViewUiBinder extends UiBinder<Widget, GenerateNodeTypeView>
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
    * Button for generate operation.
    */
   @UiField
   ImageButton generateButton;

   /**
    * Button to cancel and close window.
    */
   @UiField
   ImageButton cancelButton;
   
   public static final int WIDTH = 450;

   public static final int HEIGHT = 160;

   //IDs for Selenium tests:

   public static final String ID = "ideGenerateNodeTypeView";
   
   private final String CANCEL_BUTTON_ID = "ideGenerateNodeTypeViewCancelButton";

   private final String GENERATE_BUTTON_ID = "ideGenerateNodeTypeViewGenerateButton";

   private final String DYNAMIC_FORM_ID = "ideGenerateNodeTypeViewDynamicForm";

   private final String FORMAT_FIELD_ID = "ideGenerateNodeTypeViewFormatField";

   public GenerateNodeTypeView()
   {
      super(ID, ViewType.MODAL, ChromatticExtension.LOCALIZATION_CONSTANT.generateNodeTypeViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      form.getElement().setId(DYNAMIC_FORM_ID);
      
      formatField.setName(FORMAT_FIELD_ID);
      formatField.setShowTitle(false);
      
      generateButton.setButtonId(GENERATE_BUTTON_ID);     
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GenerateNodeTypePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GenerateNodeTypePresenter.Display#getGenerateButton()
    */
   @Override
   public HasClickHandlers getGenerateButton()
   {
      return generateButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GenerateNodeTypePresenter.Display#getNodeTypeFormat()
    */
   @Override
   public HasValue<String> getNodeTypeFormat()
   {
      return formatField;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GenerateNodeTypePresenter.Display#setNodeTypeFormatValues()
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
}
