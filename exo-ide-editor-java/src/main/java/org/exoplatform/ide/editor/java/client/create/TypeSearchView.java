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

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.editor.java.client.JavaConstants;
import org.exoplatform.ide.editor.java.client.JavaEditorExtension;
import org.exoplatform.ide.editor.java.client.create.TypeSearchPresenter.Display;
import org.exoplatform.ide.editor.java.client.ui.TypeList;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Dec 2, 2011 4:44:25 PM evgen $
 * 
 */
public class TypeSearchView extends ViewImpl implements Display
{

   private static TypeSearchViewUiBinder uiBinder = GWT.create(TypeSearchViewUiBinder.class);

   interface TypeSearchViewUiBinder extends UiBinder<Widget, TypeSearchView>
   {
   }

   @UiField
   JavaConstants tex = JavaEditorExtension.MESSAGES;

   @UiField
   TextInput typeNameText;

   @UiField
   ImageButton searchButton;

   @UiField
   ListGridItem<ShortTypeInfo> typeList;

   @UiField
   ImageButton cancelButton;

   @UiField
   ImageButton okButton;

   public TypeSearchView()
   {
      super(ID, ViewType.MODAL, "", null, 400, 300, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.TypeSearchPresenter.Display#getSearchButton()
    */
   @Override
   public HasClickHandlers getSearchButton()
   {
      return searchButton;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.TypeSearchPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.TypeSearchPresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.TypeSearchPresenter.Display#getTypesList()
    */
   @Override
   public ListGridItem<ShortTypeInfo> getTypesList()
   {
      return typeList;
   }

   /**
    * @see org.exoplatform.ide.editor.java.client.create.TypeSearchPresenter.Display#getSearchImput()
    */
   @Override
   public HasValue<String> getSearchInput()
   {
      return typeNameText;
   }
}
