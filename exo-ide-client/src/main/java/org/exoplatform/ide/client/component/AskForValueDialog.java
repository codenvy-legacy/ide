/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.component;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.KeyNames;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.ide.client.Images;

/**
 * Dialog window for asking value.
 * 
 * Contains title, prompt, text field and buttons:
 * "Yes", "No" (optional), "Cancel"
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class AskForValueDialog extends Window
{

   public static final int FORM_MARGINTOP = 15;

   public static final int FORM_MARGINSIDE = 25;
   
   public static final String ID = "ideAskForValueDialog";
   
   public static final String ID_OK_BUTTON = "ideAskForValueDialogOkButton";
   
   public static final String ID_CANCEL_BUTTON = "ideAskForValueDialogCancelButton";
   
   public static final String ID_NO_BUTTON = "ideAskForValueDialogNoButton";
   
   public static final String VALUE_FIELD = "ideAskForValueDialogValueField";

   private ValueCallback valueCallback;
   
   private ValueDiscardCallback valueDiscardCallback;

   protected TextItem textItem;
   
   private IButton okButton;

   public AskForValueDialog(String title, String prompt, String defaultValue, int dialogWidth, ValueCallback callback)
   {
      new AskForValueDialog(title, prompt, defaultValue, dialogWidth, callback, null);
   }
   
   public AskForValueDialog(String title, String prompt, String defaultValue, int dialogWidth, ValueCallback callback,
      ValueDiscardCallback discardCallback)
   {
      valueCallback = callback;
      valueDiscardCallback = discardCallback;
      
      setID(ID);
      
      setShowShadow(true);
      setTitle(title);
      setShowMinimizeButton(false);

      setWidth(dialogWidth);
      setHeight(160);

      createPromptForm(dialogWidth, prompt, defaultValue);
      createButtonsForm(defaultValue);

      setIsModal(true);
      centerInPage();
      show();

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
            valueCallback.execute(null);
         }
      });

      //textItem.focusInItem();
      textItem.selectValue();
   }
   
   private void createPromptForm(int dialogWidth, String prompt, String defaultValue)
   {
      DynamicForm form = new DynamicForm();
      //form.setCellBorder(1);
      //form.setWidth(dialogWidth - FORM_MARGINSIDE - FORM_MARGINSIDE);
      form.setLayoutAlign(Alignment.CENTER);
      form.setMargin(FORM_MARGINTOP);

      StaticTextItem promptItem = new StaticTextItem();
      promptItem.setDefaultValue(prompt);
      promptItem.setShowTitle(false);
      promptItem.setColSpan(2);

      SpacerItem spacer1 = new SpacerItem();
      spacer1.setHeight(5);
      spacer1.setColSpan(2);

      textItem = new TextItem();
      textItem.setName(VALUE_FIELD);
      textItem.setDefaultValue(defaultValue);
      textItem.setShowTitle(false);
      textItem.setColSpan(2);
      textItem.setWidth(dialogWidth - FORM_MARGINSIDE - FORM_MARGINSIDE);
      
      textItem.addKeyPressHandler(new KeyPressHandler(){

         public void onKeyPress(KeyPressEvent event)
         {
            if(event.getKeyName() == null)
               return;
            if (event.getKeyName().equals(KeyNames.ENTER)) 
            {
               if (textItem.getValue() == null || textItem.getValue().toString().length() == 0)
               {
                  return;
               }
               onOk();
            }               
         }
         
      });
      
      textItem.addChangeHandler(new ChangeHandler()
      {
         public void onChange(ChangeEvent event)
         {
            if (event.getValue() == null)
            {
               okButton.setDisabled(true);
               return;
            }
            if (event.getValue() instanceof String)
            {
               final String value = (String)event.getValue();
               if (value.length() == 0)
               {
                  okButton.setDisabled(true);
               }
               else
               {
                  okButton.setDisabled(false);
               }
            }
         }
      });
      
      form.setItems(promptItem, spacer1, textItem);

      form.setAutoWidth();

      addItem(form);
   }

   private void createButtonsForm(String defaultValue)
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(5);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      okButton = new IButton("Yes");
      okButton.setID(ID_OK_BUTTON);
      okButton.setWidth(90);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.OK);
      if (defaultValue == null || defaultValue.length() == 0)
      {
         okButton.setDisabled(true);
      }

      IButton cancelButton = new IButton("Cancel");
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.CANCEL);
      
      ToolbarItem tbi = new ToolbarItem();
      
      if (valueDiscardCallback == null)
      {
         StatefulCanvas delimiter1 = new StatefulCanvas();
         delimiter1.setWidth(3);
         tbi.setButtons(okButton, delimiter1, cancelButton);
      }
      else
      {
         IButton noButton = new IButton("No");
         noButton.setID(ID_NO_BUTTON);
         noButton.setWidth(90);
         noButton.setHeight(22);
         noButton.setIcon(Images.Buttons.NO);
         
         noButton.addClickHandler(new ClickHandler()
         {
            public void onClick(ClickEvent event)
            {
               destroy();
               valueDiscardCallback.discard();
            }
         });
         
         StatefulCanvas delimiter1 = new StatefulCanvas();
         delimiter1.setWidth(3);
         StatefulCanvas delimiter2 = new StatefulCanvas();
         delimiter2.setWidth(3);
         tbi.setButtons(okButton, delimiter1, noButton, delimiter2, cancelButton);
      }
      
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();

      addItem(buttonsForm);

      okButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            onOk();
         }
      });

      cancelButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            destroy();
            valueCallback.execute(null);
         }
      });

   }

   private void onOk()
   {
      destroy();
      String value = textItem.getValue().toString();
      valueCallback.execute(value);
   }

}
