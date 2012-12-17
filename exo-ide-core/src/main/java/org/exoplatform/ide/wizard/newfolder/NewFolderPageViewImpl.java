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
package org.exoplatform.ide.wizard.newfolder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import elemental.html.Element;


/**
 * NewFolderPageViewImpl is the view of NewFolder wizard.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewFolderPageViewImpl implements NewFolderPageView
{
   private static NewFolderViewUiBinder uiBinder = GWT.create(NewFolderViewUiBinder.class);

   private final Widget widget;

   @UiField
   TextBox folderName;

   interface NewFolderViewUiBinder extends UiBinder<Widget, NewFolderPageViewImpl>
   {
   }

   private ActionDelegate delegate;

   /**
    * Create view.
    */
   public NewFolderPageViewImpl()
   {
      widget = uiBinder.createAndBindUi(this);
   }

   /**
    * {@inheritDoc}
    */
   public String getFolderName()
   {
      return folderName.getText();
   }

   /**
    * {@inheritDoc}
    */
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }

   @UiHandler("folderName")
   void onFolderNameKeyUp(KeyUpEvent event)
   {
      delegate.checkEnteredInformation();
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
   public Widget asWidget()
   {
      return widget;
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