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
package org.eclipse.jdt.client.create;

import com.google.gwt.user.client.ui.HasText;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.create.CreatePackagePresenter.Display;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.editor.java.client.JavaEditorExtension;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CreatePackageView extends ViewImpl implements Display
{

   private static CreatePackageViewUiBinder uiBinder = GWT.create(CreatePackageViewUiBinder.class);

   @UiField
   TextInput packageField;

   @UiField
   ImageButton createButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   Label errorLabel;

   @UiField
   Label warningLabel;

   interface CreatePackageViewUiBinder extends UiBinder<Widget, CreatePackageView>
   {
   }

   public CreatePackageView()
   {
      super(ID, ViewType.MODAL, JavaEditorExtension.MESSAGES.createPackageTitle(), null, 340, 130, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#getPackageNameField()
    */
   @Override
   public HasValue<String> getPackageNameField()
   {
      return packageField;
   }

   /**
    * @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return createButton;
   }

   /**
    * @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#getErrorLabel()
    */
   @Override
   public HasText getErrorLabel()
   {
      return errorLabel;
   }

   /**
    * @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#setOkButtonEnabled(boolean)
    */
   @Override
   public void setOkButtonEnabled(boolean enabled)
   {
      createButton.setEnabled(enabled);
   }

   /**
    * @see org.eclipse.jdt.client.create.CreatePackagePresenter.Display#getWarningLabel()
    */
   @Override
   public HasText getWarningLabel()
   {
      return warningLabel;
   }

}
