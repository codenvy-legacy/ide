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
package org.exoplatform.ide.client.operation.overwrite.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.messages.IdeOverwriteLocalizationConstant;

/**
 * Dialog with one text field and three buttons: Rename, Overwrite, Cancel.
 * <p/>
 * Can be used when need to ask user: do you want to overwrite item?
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: OverwriteDialog.java Nov 11, 2011 9:38:33 AM vereshchaka $
 */
public abstract class AbstarctOverwriteDialog extends ViewImpl
{
   public static final IdeOverwriteLocalizationConstant LOCALIZATION_CONSTANT = GWT
      .create(IdeOverwriteLocalizationConstant.class);

   public static final String ID = "ideOverwriteForm";

   public static final int WIDTH = 400;

   public static final int HEIGHT = 180;

   public static final String TITLE = LOCALIZATION_CONSTANT.dialogTitle();

   interface AbstarctOverwriteDialogUiBinder extends UiBinder<Widget, AbstarctOverwriteDialog>
   {
   }

   private static AbstarctOverwriteDialogUiBinder uiBinder = GWT.create(AbstarctOverwriteDialogUiBinder.class);

   @UiField
   TextInput renameField;

   @UiField
   ImageButton renameButton;

   @UiField
   ImageButton overwriteButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   Label errMsgLabel;

   private String defaultItemName;

   /**
    * @param defaultName item name, that will be displayed in name field
    * @param errMsg message with error, that occurs
    */
   public AbstarctOverwriteDialog(String defaultName, String errMsg)
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      renameButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            onRename(renameField.getValue());
            closeDialog();
         }
      });

      overwriteButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            onOverwrite();
            closeDialog();
         }
      });

      cancelButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            onCancel();
            closeDialog();
         }
      });

      renameField.addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String newName = event.getValue();
            if (newName == null || newName.isEmpty() || newName.equals(defaultItemName))
            {
               renameButton.setEnabled(false);
               overwriteButton.setEnabled(true);
            }
            else
            {
               renameButton.setEnabled(true);
               overwriteButton.setEnabled(false);
            }
         }
      });

      defaultItemName = defaultName;
      renameField.setValue(defaultName);
      renameButton.setEnabled(false);
      errMsgLabel.setText(errMsg);
   }

   private void closeDialog()
   {
      IDE.getInstance().closeView(getId());
   }

   public void setDealutItemName(String value)
   {
      defaultItemName = value;
      renameField.setValue(value);
   }

   public abstract void onOverwrite();

   public abstract void onRename(String value);

   public abstract void onCancel();
}
