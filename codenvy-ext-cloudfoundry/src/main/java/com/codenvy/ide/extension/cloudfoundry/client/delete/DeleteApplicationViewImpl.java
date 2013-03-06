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
package com.codenvy.ide.extension.cloudfoundry.client.delete;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationView;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class DeleteApplicationViewImpl extends DialogBox implements DeleteApplicationView
{
   private static DeleteApplicationViewImplUiBinder uiBinder = GWT.create(DeleteApplicationViewImplUiBinder.class);

   @UiField
   Button btnCancel;

   @UiField
   Button btnDelete;

   @UiField
   Label askDeleteServicesLabel;

   @UiField
   CheckBox deleteServicesField;

   @UiField
   Label askLabel;

   interface DeleteApplicationViewImplUiBinder extends UiBinder<Widget, DeleteApplicationViewImpl>
   {
   }

   private ActionDelegate delegate;

   @Inject
   protected DeleteApplicationViewImpl(CloudFoundryLocalizationConstant constants, CloudFoundryResources resources)
   {
      Widget widget = uiBinder.createAndBindUi(this);

      this.setText("Delete application from CloudFoundry");
      this.setWidget(widget);

      btnCancel.setText(new Image(resources.cancelButton()) + " " + constants.cancelButton());
      btnDelete.setText(new Image(resources.okButton()) + " " + constants.deleteButton());
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
   public boolean isDeleteServices()
   {
      return deleteServicesField.getValue();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setAskMessage(String message)
   {
      // TODO set HTML
      askLabel.setText(message);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setAskDeleteServices(String text)
   {
      // TODO set HTML
      askDeleteServicesLabel.setText(text);
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

   @UiHandler("btnDelete")
   void onBtnDeleteClick(ClickEvent event)
   {
      delegate.onDeleteClicked();
   }

   @UiHandler("btnCancel")
   void onBtnCancelClick(ClickEvent event)
   {
      delegate.onCancelClicked();
   }
}