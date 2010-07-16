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
package org.exoplatform.ideall.client.component;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.ideall.client.Images;

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
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class AskForValueDialog extends Window
{

   public static final int FORM_MARGINTOP = 15;

   public static final int FORM_MARGINSIDE = 25;

   private ValueCallback valueCallback;

   protected TextItem textItem;

   public AskForValueDialog(String title, String prompt, String defaultValue, int dialogWidth, ValueCallback callback)
   {
      valueCallback = callback;

      setShowShadow(true);
      setTitle(title);
      setShowMinimizeButton(false);

      setWidth(dialogWidth);
      setHeight(160);

      createPromptForm(dialogWidth, prompt, defaultValue);
      createButtonsForm();

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
      textItem.setDefaultValue(defaultValue);
      textItem.setShowTitle(false);
      textItem.setColSpan(2);
      textItem.setWidth(dialogWidth - FORM_MARGINSIDE - FORM_MARGINSIDE);
      
      textItem.addKeyPressHandler(new KeyPressHandler(){

         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getKeyName().equals(KeyNames.ENTER)) 
            {
               onOk();            
            }               
         }
         
      });
      
      form.setItems(promptItem, spacer1, textItem);

      form.setAutoWidth();

      addItem(form);
   }

   private void createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(5);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      IButton okButton = new IButton("OK");
      okButton.setWidth(90);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.OK);

      IButton cancelButton = new IButton("Cancel");
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.CANCEL);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(okButton, delimiter1, cancelButton);
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
      valueCallback.execute(textItem.getValue().toString());
   }

}
