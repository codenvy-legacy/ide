/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.editor.java.client.JavaConstants;
import org.exoplatform.ide.editor.java.client.JavaEditorExtension;
import org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display;
import org.exoplatform.ide.editor.java.client.model.ShortTypeInfo;
import org.exoplatform.ide.editor.java.client.ui.TypeList;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 1, 2011 4:47:35 PM evgen $
 *
 */
public class CreateJavaClassView extends ViewImpl implements Display
{

   @UiField
   JavaConstants tex = JavaEditorExtension.MESSAGES;

   @UiField
   TextInput sourceFolderText;

   @UiField
   TextInput packageText;

   @UiField
   TextInput classNameText;

   @UiField
   RadioButton publicRadio;

   @UiField
   RadioButton defaultRadio;

   @UiField
   CheckBox abstaractCheck;

   @UiField
   CheckBox finalCheck;

   @UiField
   TextInput superclassText;

   @UiField
   ImageButton browseClasses;

   @UiField
   TypeList interfaceList;

   @UiField
   ImageButton addInterfaces;

   @UiField
   ImageButton removeInterfaces;

   @UiField
   CheckBox constructorsCheck;

   @UiField
   CheckBox methodsCheck;

   @UiField
   ImageButton cancelButton;

   @UiField
   ImageButton createButton;

   @UiField
   Label descriptionLabel;

   private static CreateJavaClassViewUiBinder uiBinder = GWT.create(CreateJavaClassViewUiBinder.class);

   interface CreateJavaClassViewUiBinder extends UiBinder<Widget, CreateJavaClassView>
   {
   }

   public CreateJavaClassView()
   {
      super(ID, ViewType.MODAL, JavaEditorExtension.MESSAGES.createJavaClassTitle(), null, 500, 500, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getSourceFolderText()
    */
   @Override
   public HasValue<String> getSourceFolderText()
   {
      return sourceFolderText;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getPackageText()
    */
   @Override
   public HasValue<String> getPackageText()
   {
      return packageText;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getNameText()
    */
   @Override
   public HasValue<String> getNameText()
   {
      return classNameText;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getPublicRadio()
    */
   @Override
   public HasValue<Boolean> getPublicRadio()
   {
      return publicRadio;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getDefaultRadio()
    */
   @Override
   public HasValue<Boolean> getDefaultRadio()
   {
      return defaultRadio;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getAbstract()
    */
   @Override
   public HasValue<Boolean> getAbstract()
   {
      return abstaractCheck;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getFinal()
    */
   @Override
   public HasValue<Boolean> getFinal()
   {
      return finalCheck;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getSuperClassText()
    */
   @Override
   public HasValue<String> getSuperClassText()
   {
      return superclassText;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getBrowseClassesButton()
    */
   @Override
   public HasClickHandlers getBrowseClassesButton()
   {
      return browseClasses;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getInterfaceList()
    */
   @Override
   public ListGrid<ShortTypeInfo> getInterfaceList()
   {
      return interfaceList;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getAddInterfaceButton()
    */
   @Override
   public HasClickHandlers getAddInterfaceButton()
   {
      return addInterfaces;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getRemoveInterfaceButton()
    */
   @Override
   public HasClickHandlers getRemoveInterfaceButton()
   {
      return removeInterfaces;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getConstructors()
    */
   @Override
   public HasValue<Boolean> getConstructors()
   {
      return constructorsCheck;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#getMethods()
    */
   @Override
   public HasValue<Boolean> getMethods()
   {
      return methodsCheck;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#setCreateButtonEnabled(boolean)
    */
   @Override
   public void setCreateButtonEnabled(boolean enabled)
   {
      createButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#setDescriptionLabelText(java.lang.String)
    */
   @Override
   public void setDescriptionLabelText(String text)
   {
      descriptionLabel.setText(text);
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.CreateJavaClassPresenter.Display#setRemoveInterfaceButtonEnabled(boolean)
    */
   @Override
   public void setRemoveInterfaceButtonEnabled(boolean enabled)
   {
      removeInterfaces.setEnabled(enabled);
   }

}
