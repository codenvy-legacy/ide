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
package org.exoplatform.ide.java.client.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.exoplatform.ide.json.JsonArray;

public class NewPackagePageViewImpl implements NewPackagePageView
{
   interface NewPackagePageViewImplUiBinder
      extends UiBinder<DockLayoutPanel, NewPackagePageViewImpl>
   {
   }

   private static NewPackagePageViewImplUiBinder ourUiBinder = GWT.create(NewPackagePageViewImplUiBinder.class);

   private final DockLayoutPanel rootElement;

   @UiField
   Label errorMessage;

   @UiField
   TextBox packageName;

   @UiField
   ListBox parents;

   private ActionDelegate delegate;

   @Inject
   public NewPackagePageViewImpl()
   {
      rootElement = ourUiBinder.createAndBindUi(this);

   }

   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public Widget asWidget()
   {
      return rootElement;
   }

   @Override
   public void setParents(JsonArray<String> parents)
   {
      for (String s : parents.asIterable())
      {
         this.parents.addItem(s);
      }
   }

   @Override
   public String getPackageName()
   {
      return packageName.getText();
   }

   @UiHandler("parents")
   void handleParentChanged(ChangeEvent event)
   {
      delegate.parentChanged(parents.getSelectedIndex());
   }

   @UiHandler(value = {"packageName"})
   void handleKeyUpEvent(KeyUpEvent event)
   {
      delegate.checkPackageName();
   }

}