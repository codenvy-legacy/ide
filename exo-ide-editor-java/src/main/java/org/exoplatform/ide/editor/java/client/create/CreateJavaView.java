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
package org.exoplatform.ide.editor.java.client.create;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.editor.java.client.JavaEditorExtension;
import org.exoplatform.ide.editor.java.client.create.CreateJavaPresenter.Display;

import java.util.Collection;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CreateJavaView extends ViewImpl implements Display
{

   private static CreateJavaViewUiBinder uiBinder = GWT.create(CreateJavaViewUiBinder.class);

   @UiField
   ImageButton createButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   TextInput nameInput;

   @UiField
   SelectItem typeSelect;

   interface CreateJavaViewUiBinder extends UiBinder<Widget, CreateJavaView>
   {
   }

   public CreateJavaView()
   {
      super(ID, ViewType.MODAL, JavaEditorExtension.MESSAGES.createJavaClassTitle(), null, 400, 140, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaPresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaPresenter.Display#getNameField()
    */
   @Override
   public HasValue<String> getNameField()
   {
      return nameInput;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaPresenter.Display#getTypeSelect()
    */
   @Override
   public HasValue<String> getTypeSelect()
   {
      return typeSelect;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaPresenter.Display#setTypes(java.util.Collection)
    */
   @Override
   public void setTypes(Collection<String> types)
   {
      typeSelect.setValueMap(types.toArray(new String[types.size()]), types.iterator().next());
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaPresenter.Display#setCreateButtonEnabled(boolean)
    */
   @Override
   public void setCreateButtonEnabled(boolean enabled)
   {
      createButton.setEnabled(enabled);
   }
}
