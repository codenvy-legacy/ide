/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.eclipse.jdt.client.refactoring;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.refactoring.RefactoringRenamePresenter.Display;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * View for rename a Java element using refactoring.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RefactoringRenameView.java Jan 17, 2013 4:07:34 PM azatsarynnyy $
 *
 */
public class RefactoringRenameView extends ViewImpl implements Display
{

   private static final String ID = "ideRefactoringRenameView";

   private static final String TITLE = "Rename";

   private static final int HEIGHT = 140;

   private static final int WIDTH = 450;

   private static final String NEW_NAME_FIELD_ID = "ideRefactoringRenameViewNewNameField";

   private static final String RENAME_BUTTON_ID = "ideRefactoringRenameViewRenameButton";

   private static final String CANCEL_BUTTON_ID = "ideRefactoringRenameViewCancelButton";

   private static RefactoringRenameViewUiBinder uiBinder = GWT.create(RefactoringRenameViewUiBinder.class);

   interface RefactoringRenameViewUiBinder extends UiBinder<Widget, RefactoringRenameView>
   {
   }

   @UiField
   TextInput newNameField;

   @UiField
   ImageButton cancelButton;

   @UiField
   ImageButton renameButton;

   public RefactoringRenameView()
   {
      super(ID, ViewType.MODAL, TITLE, null, WIDTH, HEIGHT, false);
      setCloseOnEscape(false);
      add(uiBinder.createAndBindUi(this));

      newNameField.setName(NEW_NAME_FIELD_ID);
      renameButton.setId(RENAME_BUTTON_ID);
      cancelButton.setId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.eclipse.jdt.client.refactoring.RefactoringRenamePresenter.Display#getNewNameField()
    */
   @Override
   public TextFieldItem getNewNameField()
   {
      return newNameField;
   }

   /**
    * @see org.eclipse.jdt.client.refactoring.RefactoringRenamePresenter.Display#getRenameButton()
    */
   @Override
   public HasClickHandlers getRenameButton()
   {
      return renameButton;
   }

   /**
    * @see org.eclipse.jdt.client.refactoring.RefactoringRenamePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.eclipse.jdt.client.refactoring.RefactoringRenamePresenter.Display#setFocusOnNewNameField()
    */
   @Override
   public void setFocusOnNewNameField()
   {
      newNameField.focus();
   }

   /**
    * @see org.eclipse.jdt.client.refactoring.RefactoringRenamePresenter.Display#selectAllTextInNewNameField()
    */
   @Override
   public void selectAllTextInNewNameField()
   {
      newNameField.selectAll();
   }

   /**
    * @see org.eclipse.jdt.client.refactoring.RefactoringRenamePresenter.Display#setNewNameFieldValue(java.lang.String)
    */
   @Override
   public void setNewNameFieldValue(String value)
   {
      newNameField.setText(value);
   }

}
