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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;

import org.exoplatform.ide.json.JsonArray;

/**
 * Default implementation for new Java package View.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NewPackagePageViewImpl extends Composite implements NewPackagePageView
{
   interface NewPackagePageViewImplUiBinder
      extends UiBinder<DockLayoutPanel, NewPackagePageViewImpl>
   {
   }

   private static NewPackagePageViewImplUiBinder ourUiBinder = GWT.create(NewPackagePageViewImplUiBinder.class);

   @UiField
   TextBox packageName;

   @UiField
   ListBox parents;

   private ActionDelegate delegate;

   @Inject
   public NewPackagePageViewImpl()
   {
      initWidget(ourUiBinder.createAndBindUi(this));
   }

   /**{@inheritDoc}*/
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   /**{@inheritDoc}*/
   @Override
   public void setParents(JsonArray<String> parents)
   {
      for (String s : parents.asIterable())
      {
         this.parents.addItem(s);
      }
   }

   /**{@inheritDoc}*/
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