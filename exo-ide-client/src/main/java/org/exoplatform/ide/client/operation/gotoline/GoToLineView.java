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
package org.exoplatform.ide.client.operation.gotoline;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GoToLineView extends ViewImpl implements org.exoplatform.ide.client.operation.gotoline.GoToLinePresenter.Display
{

   private static final String ID = "ideGoToLineForm";

   private static final int WIDTH = 400;

   private static final int HEIGHT = 160;

   private static final String GO_BUTTON_ID = "ideGoToLineFormGoButton";

   private static final String CANCEL_BUTTON_ID = "ideGoToLineFormCancelButton";

   private static final String LINE_NUMBER_FIELD = "ideGoToLineFormLineNumberField";

   private static final String LINE_NUMBER_RANGE_LABEL = "ideGoToLineFormLineRangeLabel";

   private static GoTolineViewUiBinder uiBinder = GWT.create(GoTolineViewUiBinder.class);

   interface GoTolineViewUiBinder extends UiBinder<Widget, GoToLineView>
   {
   }

   @UiField
   TextInput lineNumberField;

   @UiField
   Label rangeLabel;

   @UiField
   ImageButton goButton;

   @UiField
   ImageButton cancelButton;

   private static final String TITLE = IDE.EDITOR_CONSTANT.goToLineTitle();

   public GoToLineView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.goToLine()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      lineNumberField.setName(LINE_NUMBER_FIELD);
      rangeLabel.getElement().setId(LINE_NUMBER_RANGE_LABEL);
      goButton.setButtonId(GO_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.client.EditorPresenter.GoToLinePresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.EditorPresenter.GoToLinePresenter.Display#getGoButton()
    */
   public HasClickHandlers getGoButton()
   {
      return goButton;
   }

   /**
    * @see org.exoplatform.ide.client.EditorPresenter.GoToLinePresenter.Display#removeFocusFromLineNumber()
    */
   public void removeFocusFromLineNumber()
   {
   }

   /**
    * @see org.exoplatform.ide.client.EditorPresenter.edit.action.GoToLinePresenter.Display#getLineNumber()
    */
   @Override
   public TextFieldItem getLineNumber()
   {
      return lineNumberField;
   }

   /**
    * @see org.exoplatform.ide.client.edit.GoToLinePresenter.Display#setFocusInLineNumberField()
    */
   @Override
   public void setFocusInLineNumberField()
   {
      lineNumberField.setFocus(true);
   }

   /**
    * @see org.exoplatform.ide.client.edit.GoToLinePresenter.Display#setCaptionLabel(java.lang.String)
    */
   @Override
   public void setCaptionLabel(String caption)
   {
      rangeLabel.setText(caption);
   }

}
