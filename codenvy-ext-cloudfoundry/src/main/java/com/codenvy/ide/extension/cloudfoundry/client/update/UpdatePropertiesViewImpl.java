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
package com.codenvy.ide.extension.cloudfoundry.client.update;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class UpdatePropertiesViewImpl extends DialogBox implements UpdatePropertiesView
{
   private static UpdatePropertiesViewImplUiBinder uiBinder = GWT.create(UpdatePropertiesViewImplUiBinder.class);

   @UiField
   Label title;

   @UiField
   TextBox property;

   @UiField
   Button btnOk;

   @UiField
   Button btnCancel;

   interface UpdatePropertiesViewImplUiBinder extends UiBinder<Widget, UpdatePropertiesViewImpl>
   {
   }

   private UpdatePropertiesView.ActionDelegate delegate;

   @Inject
   protected UpdatePropertiesViewImpl()
   {
      Widget widget = uiBinder.createAndBindUi(this);

      this.setWidget(widget);
   }

   @UiHandler("btnOk")
   void onBtnOkClick(ClickEvent event)
   {
      delegate.onOkClicked();
   }

   @UiHandler("btnCancel")
   void onBtnCancelClick(ClickEvent event)
   {
      delegate.onCancelClicked();
   }

   @UiHandler("property")
   void onPropertyKeyUp(KeyUpEvent event)
   {
      delegate.onPropertyChanged();
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
   public String getProperty()
   {
      return property.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setProperty(String property)
   {
      this.property.setText(property);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setMessage(String message)
   {
      title.setText(message);

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDialogTitle(String title)
   {
      this.setText(title);
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

   /**
    * {@inheritDoc}
    */
   @Override
   public void setEnableOkButton(boolean isEnable)
   {
      btnOk.setEnabled(isEnable);
   }
}