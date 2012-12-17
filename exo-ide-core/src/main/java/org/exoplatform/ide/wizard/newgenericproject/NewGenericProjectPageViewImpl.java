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
package org.exoplatform.ide.wizard.newgenericproject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import elemental.html.Element;


/**
 * GenericProjectPageViewImpl is the view of generic project page wizard.
 * Provides entering project's name for new generic project.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewGenericProjectPageViewImpl implements NewGenericProjectPageView
{
   private final Widget widget;

   private ActionDelegate delegate;

   private static NewGenericPrPageViewImplUiBinder uiBinder = GWT.create(NewGenericPrPageViewImplUiBinder.class);

   @UiField
   FlowPanel mainPanel;

   @UiField
   TextBox projectName;

   interface NewGenericPrPageViewImplUiBinder extends UiBinder<Widget, NewGenericProjectPageViewImpl>
   {
   }

   public NewGenericProjectPageViewImpl()
   {
      widget = uiBinder.createAndBindUi(this);
   }

   @UiHandler("projectName")
   void onProjectNameKeyUp(KeyUpEvent event)
   {
      delegate.checkProjectName();
   }

   /**
    * {@inheritDoc}
    */
   public Widget asWidget()
   {
      return widget;
   }

   /**
    * {@inheritDoc}
    */
   public String getProjectName()
   {
      return projectName.getText();
   }

   /**
    * {@inheritDoc}
    */
   public ActionDelegate getDelegate()
   {
      return delegate;
   }

   /**
    * {@inheritDoc}
    */
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   /**
    * {@inheritDoc}
    */
   public Element getElement()
   {
      // TODO Auto-generated method stub
      return null;
   }
}