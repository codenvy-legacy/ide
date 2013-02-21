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
 * Default implementation for {@link NewJavaClassPageView}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public class NewJavaClassPageViewImpl extends Composite implements NewJavaClassPageView
{
   interface NewJavaClassPageViewImplUiBinder extends UiBinder<DockLayoutPanel, NewJavaClassPageViewImpl>
   {
   }

   private static NewJavaClassPageViewImplUiBinder ourUiBinder = GWT.create(NewJavaClassPageViewImplUiBinder.class);

   private ActionDelegate delegate;

   @UiField
   ListBox parents;

   @UiField
   TextBox typeName;

   @UiField
   ListBox types;

   @Inject
   public NewJavaClassPageViewImpl()
   {
      initWidget(ourUiBinder.createAndBindUi(this));
   }

   /**{@inheritDoc}*/
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   /**
    * Handler for ChangeEvent
    * @param event the ChangeEvent
    */
   @UiHandler("parents")
   void handleParentChanged(ChangeEvent event)
   {
      delegate.parentChanged(parents.getSelectedIndex());
   }

   /**
    * Handler for KeyUpEvent
    * @param event the KeyUpEvent
    */
   @UiHandler(value = {"typeName"})
   void handleKeyUpEvent(KeyUpEvent event)
   {
      delegate.checkTypeName();
   }

   /**{@inheritDoc}*/
   @Override
   public String getClassName()
   {
      return typeName.getText();
   }

   /**{@inheritDoc}*/
   @Override
   public String getClassType()
   {
      return types.getItemText(types.getSelectedIndex());
   }

   /**{@inheritDoc}*/
   @Override
   public void setClassTypes(JsonArray<String> classTypes)
   {
      for (String s : classTypes.asIterable())
      {
         types.addItem(s);
      }
   }

   /**{@inheritDoc}*/
   @Override
   public void setParents(JsonArray<String> parentNames)
   {
      for (String s : parentNames.asIterable())
      {
         parents.addItem(s);
      }
   }

   /**{@inheritDoc}*/
   @Override
   public void disableAllUi()
   {
      parents.setEnabled(false);
      types.setEnabled(false);
      typeName.setEnabled(false);
   }

   /**{@inheritDoc}*/
   @Override
   public void selectParent(int index)
   {
      if (parents.getItemCount() > index)
      {
         parents.setItemSelected(index, true);
      }
   }
}