package org.exoplatform.ide.java.client.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


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
public class JavaProjectPageViewImpl implements JavaProjectPageView
{

   interface JavaProjectPageViewImplUiBinder
      extends UiBinder<Widget, JavaProjectPageViewImpl>
   {
   }

   private static JavaProjectPageViewImplUiBinder ourUiBinder = GWT.create(JavaProjectPageViewImplUiBinder.class);

   private final Widget widget;

   @UiField
   TextBox projectName;

   @UiField
   TextBox sourceFolder;

   private ActionDelegate delegate;

   public JavaProjectPageViewImpl()
   {
      widget = ourUiBinder.createAndBindUi(this);
   }

   @Override
   public Widget asWidget()
   {
      return widget;
   }

   @Override
   public String getProjectName()
   {
      return projectName.getValue();
   }

   @Override
   public String getResourceFolder()
   {
      return sourceFolder.getValue();
   }

   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   @UiHandler(value = {"projectName", "sourceFolder"})
   void handleKeyUpEvent(KeyUpEvent event)
   {
      delegate.checkProjectInput();
   }
}