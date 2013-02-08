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
package org.exoplatform.ide.extension.cloudfoundry.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.exoplatform.ide.json.JsonArray;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class CreateApplicationViewImpl extends DialogBox implements CreateApplicationView
{
   private static CreateApplicationViewImplUiBinder uiBinder = GWT.create(CreateApplicationViewImplUiBinder.class);

   private static final String ID = "ideCloudFoundryCreateAppView";

   private static final String CREATE_BUTTON_ID = "ideCloudFoundryAppViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideCloudFoundryAppViewCancelButton";

   private static final String TYPE_FIELD_ID = "ideCloudFoundryAppViewTypeField";

   private static final String NAME_FIELD_ID = "ideCloudFoundryAppViewNameField";

   private static final String URL_FIELD_ID = "ideCloudFoundryAppViewUrlField";

   private static final String INSTANCES_FIELD_ID = "ideCloudFoundryAppViewInstancesField";

   private static final String MEMORY_FIELD_ID = "ideCloudFoundryAppViewMemoryField";

   private static final String SERVER_FIELD_ID = "ideCloudFoundryAppViewServerField";

   @UiField
   TextBox name;

   @UiField
   TextBox url;

   @UiField
   CheckBox customUrl;

   @UiField
   TextBox instances;

   @UiField
   TextBox memory;

   @UiField
   ListBox server;

   @UiField
   ListBox type;

   @UiField
   SimpleCheckBox startAfterCreation;
   
   @UiField
   CheckBox autodetectType;

   @UiField
   Button btnCreate;

   @UiField
   Button btnCancel;

   private ActionDelegate delegate;

   interface CreateApplicationViewImplUiBinder extends UiBinder<Widget, CreateApplicationViewImpl>
   {
   }

   @Inject
   public CreateApplicationViewImpl()
   {
      Widget widget = uiBinder.createAndBindUi(this);

      this.setText("Create Application");
      this.setWidget(widget);

      server.setName(SERVER_FIELD_ID);
      type.setName(TYPE_FIELD_ID);
      name.setName(NAME_FIELD_ID);
      url.setName(URL_FIELD_ID);
      instances.setName(INSTANCES_FIELD_ID);
      memory.setName(MEMORY_FIELD_ID);
      //      btnCreate.setButtonId(CREATE_BUTTON_ID);
      //      btnCancel.setButtonId(CANCEL_BUTTON_ID);
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
   public String getType()
   {
      int selectedItem = type.getSelectedIndex();
      return selectedItem != -1 ? type.getItemText(selectedItem) : "";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isAutodetectType()
   {
      return autodetectType.getValue();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setAutodetectType(boolean autodetected)
   {
      autodetectType.setValue(autodetected);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName()
   {
      return name.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setName(String name)
   {
      this.name.setText(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getUrl()
   {
      return url.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setUrl(String url)
   {
      this.url.setText(url);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isCustomUrl()
   {
      return customUrl.getValue();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getInstances()
   {
      return instances.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setInstances(String instances)
   {
      this.instances.setText(instances);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getMemory()
   {
      return memory.getText();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setMemory(String memory)
   {
      this.memory.setText(memory);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getServer()
   {
      int serverIndex = server.getSelectedIndex();
      return serverIndex != -1 ? server.getItemText(serverIndex) : "";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setServer(String server)
   {
      int count = this.server.getItemCount();
      boolean isItemFound = false;

      int i = 0;
      while (i < count && !isItemFound)
      {
         String item = this.server.getItemText(i);
         isItemFound = item.equals(server);
         
         i++;
      }

      if (isItemFound)
      {
         this.server.setSelectedIndex(i - 1);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isStartAfterCreation()
   {
      return startAfterCreation.getValue();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setStartAfterCreation(boolean start)
   {
      this.startAfterCreation.setValue(start);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void enableCreateButton(boolean enable)
   {
      btnCreate.setEnabled(enable);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void focusInNameField()
   {
      name.setFocus(true);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setTypeValues(JsonArray<String> types)
   {
      type.clear();

      for (int i = 0; i < types.size(); i++)
      {
         type.addItem(types.get(i));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void enableTypeField(boolean enable)
   {
      type.setEnabled(enable);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void enableUrlField(boolean enable)
   {
      url.setEnabled(enable);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void enableMemoryField(boolean enable)
   {
      memory.setEnabled(enable);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setSelectedIndexForTypeSelectItem(int index)
   {
      type.setSelectedIndex(index);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void focusInUrlField()
   {
      url.setFocus(true);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void enableAutodetectTypeCheckItem(boolean enable)
   {
      autodetectType.setEnabled(enable);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setServerValues(JsonArray<String> servers)
   {
      server.clear();

      for (int i = 0; i < servers.size(); i++)
      {
         server.addItem(servers.get(i));
      }
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
   public void showDialog()
   {
      this.center();
      this.show();
   }

   @UiHandler("btnCancel")
   void onBtnCancelClick(ClickEvent event)
   {
      delegate.doCancel();
   }

   @UiHandler("btnCreate")
   void onBtnCreateClick(ClickEvent event)
   {
      delegate.doCreate();
   }

   //   @SuppressWarnings("rawtypes")
   //   @UiHandler("autodetectType")
   //   void onAutodetectTypeValueChange(ValueChangeEvent event)
   //   {
   //      //      delegate.autoDetectTypeChanged((Boolean)event.getValue());
   //   }
   //
   //   @SuppressWarnings("rawtypes")
   //   @UiHandler("customUrl")
   //   void onCustomUrlValueChange(ValueChangeEvent event)
   //   {
   //      //      delegate.customUrlChanged((Boolean)event.getValue());
   //   }
   //
   //   @SuppressWarnings("rawtypes")
   //   @UiHandler("name")
   //   void onNameValueChange(ValueChangeEvent event)
   //   {
   //      //      delegate.applicationNameChanged();
   //   }
   //
   @UiHandler("server")
   void onServerChange(ChangeEvent event)
   {
      delegate.serverChanged();
   }

   @UiHandler("autodetectType")
   void onAutodetectTypeClick(ClickEvent event)
   {
      delegate.autoDetectTypeChanged(autodetectType.getValue());
   }

   @UiHandler("customUrl")
   void onCustomUrlClick(ClickEvent event)
   {
      delegate.customUrlChanged(customUrl.getValue());
   }

   @UiHandler("name")
   void onNameKeyUp(KeyUpEvent event)
   {
      delegate.applicationNameChanged();
   }
}