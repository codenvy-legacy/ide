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
package com.codenvy.ide.extension.cloudfoundry.client.services;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The implementation of {@link CreateServiceView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateServiceViewImpl extends DialogBox implements CreateServiceView
{
   private static CreateServiceViewImplUiBinder uiBinder = GWT.create(CreateServiceViewImplUiBinder.class);

   @UiField
   ListBox servicesField;

   @UiField
   TextBox nameField;

   @UiField
   Button btnCreate;

   @UiField
   Button btnCancel;

   @UiField
   Label serviceTypeLabel;

   @UiField
   Label nameLabel;

   @UiField
   Label optionalLabel;

   interface CreateServiceViewImplUiBinder extends UiBinder<Widget, CreateServiceViewImpl>
   {
   }

   private ActionDelegate delegate;

   /**
    * Create view.
    * 
    * @param resources
    * @param constant
    */
   @Inject
   protected CreateServiceViewImpl(CloudFoundryResources resources, CloudFoundryLocalizationConstant constant)
   {
      Widget widget = uiBinder.createAndBindUi(this);

      this.setWidget(widget);
      this.setText("Create service");

      this.addStyleName(resources.cloudFoundryCss().createService());
      serviceTypeLabel.addStyleName(resources.cloudFoundryCss().serviceLabel());
      nameLabel.addStyleName(resources.cloudFoundryCss().serviceLabel());
      optionalLabel.addStyleName(resources.cloudFoundryCss().serviceLabel());

      btnCreate.setHTML(new Image(resources.okButton()) + " " + constant.createButton());
      btnCancel.setHTML(new Image(resources.cancelButton()) + " " + constant.cancelButton());
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
   public String getSystemServices()
   {
      int selectedIndex = servicesField.getSelectedIndex();
      return selectedIndex != -1 ? servicesField.getValue(selectedIndex) : "";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setServices(LinkedHashMap<String, String> values)
   {
      servicesField.clear();
      Set<Entry<String, String>> s = values.entrySet();

      Iterator<Entry<String, String>> it = s.iterator();

      while (it.hasNext())
      {
         Entry<String, String> en = it.next();
         servicesField.addItem(en.getValue(), en.getKey());
      }
      servicesField.setSelectedIndex(0);
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

   @UiHandler("btnCreate")
   void onBtnCreateClick(ClickEvent event)
   {
      delegate.onCreateClicked();
   }

   @UiHandler("btnCancel")
   void onBtnCancelClick(ClickEvent event)
   {
      delegate.onCancelClicked();
   }
}