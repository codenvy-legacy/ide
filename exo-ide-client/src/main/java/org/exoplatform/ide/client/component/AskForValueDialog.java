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
package org.exoplatform.ide.client.component;

import com.google.gwt.user.client.ui.HasVerticalAlignment;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;

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

public class AskForValueDialog extends DialogWindow
{

   public static final int FORM_MARGINTOP = 15;

   public static final int FORM_MARGINSIDE = 25;

   public static final String ID = "ideAskForValueDialog";

   public static final int DIALOG_HEIGHT = 160;

   public static final String ID_OK_BUTTON = "ideAskForValueDialogOkButton";

   public static final String ID_CANCEL_BUTTON = "ideAskForValueDialogCancelButton";

   public static final String ID_NO_BUTTON = "ideAskForValueDialogNoButton";

   public static final String VALUE_FIELD = "ideAskForValueDialogValueField";

   private ValueCallback valueCallback;

   private ValueDiscardCallback valueDiscardCallback;

   protected TextField textItem;

   //private IButton okButton;
   private ImageButton okButton;

   public AskForValueDialog(String title, String prompt, String defaultValue, int dialogWidth, ValueCallback callback,
      ValueDiscardCallback discardCallback)
   {
      super(dialogWidth, DIALOG_HEIGHT, ID);
      valueCallback = callback;
      valueDiscardCallback = discardCallback;
      setTitle(title);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      mainLayout.setSpacing(15);

      mainLayout.add(createPromptForm(dialogWidth, prompt, defaultValue));
      mainLayout.add(createButtonsForm(defaultValue));
      setWidget(mainLayout);

      show();

      addCloseClickHandler(new CloseClickHandler()
      {
         @Override
         public void onCloseClick()
         {
            destroy();
            valueCallback.execute(null);
         }
      });
      
      textItem.focusInItem();
      textItem.selectValue();
   }

   public AskForValueDialog(String title, String prompt, String defaultValue, int dialogWidth, ValueCallback callback)
   {
      this(title, prompt, defaultValue, dialogWidth, callback, null);
   }

   private VerticalPanel createPromptForm(int dialogWidth, String prompt, String defaultValue)
   {
      VerticalPanel form = new VerticalPanel();

      textItem = new TextField(VALUE_FIELD, prompt);
      textItem.setWidth(dialogWidth - FORM_MARGINSIDE - FORM_MARGINSIDE);
      textItem.setHeight(22);
      textItem.setValue(defaultValue);
      textItem.setTitleOrientation(TitleOrientation.TOP);
      textItem.addKeyUpHandler(new KeyUpHandler()
      {

         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (event.getNativeKeyCode() == 13)
            {
               if (textItem.getValue() == null || textItem.getValue().toString().length() == 0)
               {
                  return;
               }
               onOk();
            }
         }
      });

      textItem.addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue() == null)
            {
               //okButton.setDisabled(true);
               okButton.setEnabled(false);
               return;
            }
            if (event.getValue() instanceof String)
            {
               final String value = (String)event.getValue();
               if (value.length() == 0)
               {
                  //okButton.setDisabled(true);
                  okButton.setEnabled(false);
               }
               else
               {
                  //okButton.setDisabled(false);
                  okButton.setEnabled(true);
               }
            }
         }
      });

      form.add(textItem);

      return form;
   }

   private HorizontalPanel createButtonsForm(String defaultValue)
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(22 + "px");
      buttonsLayout.setSpacing(5);

      okButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.askValueDialogYesButton(), "yes");
      okButton.setId(ID_OK_BUTTON);
      if (defaultValue == null || defaultValue.length() == 0)
      {
         okButton.setEnabled(false);
      }      

      IButton cancelButton = new IButton(IDE.IDE_LOCALIZATION_CONSTANT.askValueDialogCancelButton());
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.CANCEL);

      if (valueDiscardCallback == null)
      {
         buttonsLayout.add(okButton);
         buttonsLayout.add(cancelButton);
      }
      else
      {
         IButton noButton = new IButton(IDE.IDE_LOCALIZATION_CONSTANT.askValueDialogNoButton());
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

         buttonsLayout.add(okButton);
         buttonsLayout.add(noButton);
         buttonsLayout.add(cancelButton);
      }

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

      return buttonsLayout;
   }

   private void onOk()
   {
      destroy();
      String value = textItem.getValue().toString();
      valueCallback.execute(value);
   }

}
