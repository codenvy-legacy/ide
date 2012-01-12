/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation.findtext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FindTextView extends ViewImpl implements
   org.exoplatform.ide.client.operation.findtext.FindTextPresenter.Display
{

   private static final String ID = "ideFindReplaceTextView";

   private static final int DEFAULT_WIDTH = 470;

   private static final int DEFAULT_HEIGHT = 230;

   private static final String TITLE = IDE.EDITOR_CONSTANT.findTextTitle();

   @UiField
   ImageButton findTextButton;

   @UiField
   ImageButton replaceButton;

   @UiField
   ImageButton replaceFindButton;

   @UiField
   ImageButton replaceAllButton;

   @UiField
   TextInput findTextField;

   @UiField
   TextInput replaceTextField;

   @UiField
   CheckBox caseSensitiveField;

   @UiField
   Label findResultLabel;

   interface FindTextViewUiBinder extends UiBinder<Widget, FindTextView>
   {
   }

   /**
    * UIBinder instance
    */
   private static FindTextViewUiBinder uiBinder = GWT.create(FindTextViewUiBinder.class);

   public FindTextView()
   {
      super(ID, ViewType.OPERATION, TITLE, new Image(IDEImageBundle.INSTANCE.findText()), DEFAULT_WIDTH, DEFAULT_HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getCaseSensitiveField()
    */
   public HasValue<Boolean> getCaseSensitiveField()
   {
      return caseSensitiveField;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getFindButton()
    */
   public HasClickHandlers getFindButton()
   {
      return findTextButton;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getFindField()
    */
   public TextFieldItem getFindField()
   {
      return findTextField;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceAllButton()
    */
   public HasClickHandlers getReplaceAllButton()
   {
      return replaceAllButton;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceButton()
    */
   public HasClickHandlers getReplaceButton()
   {
      return replaceButton;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceField()
    */
   public TextFieldItem getReplaceField()
   {
      return replaceTextField;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableFindButton(boolean)
    */
   public void enableFindButton(boolean isEnable)
   {
      findTextButton.setEnabled(isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceFindButton()
    */
   public HasClickHandlers getReplaceFindButton()
   {
      return replaceFindButton;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceFindButton(boolean)
    */
   public void enableReplaceFindButton(boolean isEnable)
   {
      replaceFindButton.setEnabled(isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceAllButton(boolean)
    */
   public void enableReplaceAllButton(boolean isEnable)
   {
      replaceAllButton.setEnabled(isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceButton(boolean)
    */
   public void enableReplaceButton(boolean isEnable)
   {
      replaceButton.setEnabled(isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getResultLabel()
    */
   public HasValue<String> getResultLabel()
   {
      return findResultLabel;
   }

   /**
    * @see org.exoplatform.ide.client.edit.FindTextPresenter.Display#focusInFindField()
    */
   @Override
   public void focusInFindField()
   {
      findTextField.focus();
   }

}
