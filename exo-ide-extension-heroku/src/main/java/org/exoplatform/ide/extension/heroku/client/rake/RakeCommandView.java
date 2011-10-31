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
package org.exoplatform.ide.extension.heroku.client.rake;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;

/**
 * View for executing rake command.
 * Must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 17, 2011 10:55:51 AM anya $
 *
 */
public class RakeCommandView extends ViewImpl implements RakeCommandPresenter.Display
{
   private static final String ID = "ideRakeCommandView";

   private static final int WIDTH = 480;

   private static final int HEIGHT = 120;

   private static final String RUN_BUTTON_ID = "ideRakeCommandViewRunButton";

   private static final String HELP_BUTTON_ID = "ideRakeCommandViewHelpButton";

   private static final String CLOSE_BUTTON_ID = "ideRakeCommandViewCloseButton";

   private static final String COMMAND_FIELD_ID = "ideRakeCommandViewCommandField";

   private static RakeCommandViewUiBinder uiBinder = GWT.create(RakeCommandViewUiBinder.class);

   interface RakeCommandViewUiBinder extends UiBinder<Widget, RakeCommandView>
   {
   }

   /**
    *Rake command field.
    */
   @UiField
   TextInput commandField;

   /**
    *Run rake command button.
    */
   @UiField
   ImageButton runButton;

   /**
    *Get rake help button.
    */
   @UiField
   ImageButton helpButton;

   /**
    * Close view button.
    */
   @UiField
   ImageButton closeButton;

   public RakeCommandView()
   {
      super(ID, ViewType.POPUP, HerokuExtension.LOCALIZATION_CONSTANT.rakeViewTitle(), null, WIDTH, HEIGHT,false);
      add(uiBinder.createAndBindUi(this));

      commandField.setName(COMMAND_FIELD_ID);
      runButton.setButtonId(RUN_BUTTON_ID);
      helpButton.setButtonId(HELP_BUTTON_ID);
      closeButton.setButtonId(CLOSE_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#getCommandField()
    */
   @Override
   public TextFieldItem getCommandField()
   {
      return commandField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#getRunButton()
    */
   @Override
   public HasClickHandlers getRunButton()
   {
      return runButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#getHelpButton()
    */
   @Override
   public HasClickHandlers getHelpButton()
   {
      return helpButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#enableRunButton(boolean)
    */
   @Override
   public void enableRunButton(boolean isEnabled)
   {
      runButton.setEnabled(isEnabled);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#focusInCommandField()
    */
   @Override
   public void focusInCommandField()
   {
      commandField.setFocus(true);
   }
}
