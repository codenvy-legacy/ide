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
package org.exoplatform.ide.client.dialogs;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AskForValueView extends ViewImpl implements org.exoplatform.ide.client.dialogs.AskForValueDialog.Display
{

   private static final String ID = "ideAskForValueView";

   private static AskForValueViewUiBinder uiBinder = GWT.create(AskForValueViewUiBinder.class);

   interface AskForValueViewUiBinder extends UiBinder<Widget, AskForValueView>
   {
   }

   @UiField
   Label promptLabel;

   @UiField
   TextField textField;

   @UiField
   ImageButton yesButton;

   @UiField
   ImageButton noButton;

   @UiField
   ImageButton cancelButton;

   public AskForValueView()
   {
      super(ID, "modal", "view title", new Image(IDEImageBundle.INSTANCE.about()), 450, 170);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public HasText getPromptLabel()
   {
      return promptLabel;
   }

   @Override
   public TextFieldItem getTextField()
   {
      return textField;
   }

   @Override
   public HasClickHandlers getYesButton()
   {
      return yesButton;
   }

   @Override
   public HasClickHandlers getNoButton()
   {
      return noButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public void setNoButtonEnabled(boolean enabled)
   {
      noButton.setEnabled(enabled);
   }

   @Override
   public void setYesButtonEnabled(boolean enabled)
   {
      yesButton.setEnabled(enabled);
   }
   
   @Override
   protected void onAttach()
   {
      super.onAttach();
      
      ScheduledCommand command = new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            textField.focusInItem();
            textField.selectValue();
         }
      };
      
      Scheduler.get().scheduleDeferred(command);
   }

}
