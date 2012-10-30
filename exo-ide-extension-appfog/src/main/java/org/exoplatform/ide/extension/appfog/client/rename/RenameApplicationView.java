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
package org.exoplatform.ide.extension.appfog.client.rename;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;

/**
 * View for renaming Appfog application.
 * View must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class RenameApplicationView extends ViewImpl implements RenameApplicationPresenter.Display
{
   private static final String ID = "ideRenameApplicationView";

   private static final int WIDTH = 410;

   private static final int HEIGHT = 160;

   private static final String RENAME_BUTTON_ID = "ideRenameApplicationViewRenameButton";

   private static final String CANCEL_BUTTON_ID = "ideRenameApplicationViewCancelButton";

   private static final String NAME_FIELD_ID = "ideRenameApplicationViewNameField";

   /**
    * Application name field.
    */
   @UiField
   TextField nameField;

   /**
    * Rename button.
    */
   @UiField
   ImageButton renameButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   private static RenameApplicationViewUiBinder uiBinder = GWT.create(RenameApplicationViewUiBinder.class);

   interface RenameApplicationViewUiBinder extends UiBinder<Widget, RenameApplicationView>
   {
   }

   public RenameApplicationView()
   {
      super(ID, ViewType.MODAL, AppfogExtension.LOCALIZATION_CONSTANT.renameApplicationViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      nameField.setName(NAME_FIELD_ID);
      nameField.setHeight(22);
      renameButton.setButtonId(RENAME_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   @Override
   public TextFieldItem getRenameField()
   {
      return nameField;
   }

   @Override
   public HasClickHandlers getRenameButton()
   {
      return renameButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public void selectValueInRenameField()
   {
      nameField.selectValue();
   }

   @Override
   public void enableRenameButton(boolean isEnabled)
   {
      renameButton.setEnabled(isEnabled);
   }

}
