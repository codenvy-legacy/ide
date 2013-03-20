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
package com.codenvy.ide.extension.cloudfoundry.client.deploy;

import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link DeployApplicationView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class DeployApplicationViewImpl extends Composite implements DeployApplicationView
{
   private static DeployApplicationViewImplUiBinder uiBinder = GWT.create(DeployApplicationViewImplUiBinder.class);

   @UiField
   DockLayoutPanel cloudFoundryPanel;

   @UiField
   ListBox targetField;

   @UiField
   TextBox nameField;

   @UiField
   TextBox urlField;

   interface DeployApplicationViewImplUiBinder extends UiBinder<Widget, DeployApplicationViewImpl>
   {
   }

   private ActionDelegate delegate;

   /**
    * Create view.
    */
   @Inject
   protected DeployApplicationViewImpl()
   {
      initWidget(uiBinder.createAndBindUi(this));
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
   public String getUrl()
   {
      return urlField.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setUrl(String url)
   {
      urlField.setText(url);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getServer()
   {
      int selectedIndex = targetField.getSelectedIndex();
      return selectedIndex != -1 ? targetField.getItemText(selectedIndex) : "";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setServer(String server)
   {
      int count = this.targetField.getItemCount();
      boolean isItemFound = false;

      // Looks up entered server into available list of servers
      int i = 0;
      while (i < count && !isItemFound)
      {
         String item = this.targetField.getItemText(i);
         isItemFound = item.equals(server);

         i++;
      }

      // If item was found then it will be shown otherwise do nothing
      if (isItemFound)
      {
         this.targetField.setSelectedIndex(i - 1);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setServerValues(JsonArray<String> servers)
   {
      targetField.clear();
      for (int i = 0; i < servers.size(); i++)
      {
         targetField.addItem(servers.get(i));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Composite getView()
   {
      return this;
   }

   @UiHandler("targetField")
   void onTargetFieldChange(ChangeEvent event)
   {
      delegate.onServerChanged();
   }

   @UiHandler("nameField")
   void onNameFieldKeyUp(KeyUpEvent event)
   {
      delegate.onNameChanged();
   }

   @UiHandler("urlField")
   void onUrlFieldKeyUp(KeyUpEvent event)
   {
      delegate.onUrlChanged();
   }
}