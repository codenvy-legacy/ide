/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client.rename;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class RenameApplicationViewImpl extends DialogBox implements RenameApplicationView
{
   private static RenameApplicationViewImplUiBinder uiBinder = GWT.create(RenameApplicationViewImplUiBinder.class);

   @UiField
   Button btnRename;

   @UiField
   Button btnCancel;

   @UiField
   TextBox nameField;

   interface RenameApplicationViewImplUiBinder extends UiBinder<Widget, RenameApplicationViewImpl>
   {
   }

   private ActionDelegate delegate;

   @Inject
   protected RenameApplicationViewImpl(CloudFoundryLocalizationConstant constants)
   {
      Widget widget = uiBinder.createAndBindUi(this);

      this.setText(constants.renameApplicationViewTitle());
      this.setWidget(widget);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void selectValueInRenameField()
   {
      nameField.selectAll();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void enableRenameButton(boolean isEnabled)
   {
      btnRename.setEnabled(isEnabled);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName()
   {
      return nameField.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setName(String name)
   {
      nameField.setText(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void showDialog()
   {
      this.center();
      this.show();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close()
   {
      this.hide();
   }

   @UiHandler("btnRename")
   void onBtnRenameClick(ClickEvent event)
   {
      delegate.onRenameClicked();
   }

   @UiHandler("btnCancel")
   void onBtnCancelClick(ClickEvent event)
   {
      delegate.onCancelClicked();
   }

   @UiHandler("nameField")
   void onNameFieldKeyUp(KeyUpEvent event)
   {
      delegate.onNameChanged();
   }
}