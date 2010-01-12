/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.operation.output;

import org.exoplatform.gwt.commons.smartgwt.component.ImgButton;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.operation.TabPanel;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OutputForm extends TabPanel implements OutputPresenter.Display
{

   private static final String LOG_COLOR = "#000077";

   private static final String INFO_COLOR = "#007700";

   private static final String WARNING_COLOR = "#AA0077";

   private static final String ERROR_COLOR = "#880000";

   private static final String OUTPUT_COLOR = "#000088";

   private OutputPresenter presenter;

   private VLayout outputLayout;

   private ImgButton clearOutputButton;

   public OutputForm(HandlerManager eventBus)
   {
      super(eventBus, false);
      setCanFocus(false);

      setOverflow(Overflow.HIDDEN);

      outputLayout = new VLayout();
      outputLayout.setWidth100();
      outputLayout.setHeight100();
      outputLayout.setOverflow(Overflow.SCROLL);
      outputLayout.setCanFocus(false);
      addMember(outputLayout);

      clearOutputButton = new ImgButton();
      clearOutputButton.setSrc(Images.ViewPanel.BUTTON_CLEAR);
      clearOutputButton.setWidth(20);
      clearOutputButton.setHeight(18);
      clearOutputButton.setCanFocus(false);
      clearOutputButton.setTooltip("Clear output");
      //clearOutputButton1.disable();
      addTabButton(clearOutputButton);

      presenter = new OutputPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   public void clearOutput()
   {
      for (Canvas canvas : outputLayout.getChildren())
      {
         outputLayout.removeChild(canvas);
      }
   }

   private boolean odd = true;

   public void outMessage(OutputMessage message)
   {
      Label label = new Label();

      if (message.getType() == OutputMessage.Type.LOG)
      {
         label.setContents("<font color=\"" + LOG_COLOR + "\">[" + OutputMessage.Type.LOG.name() + "] "
            + message.getMessage() + "</font>");

      }
      else if (message.getType() == OutputMessage.Type.INFO)
      {
         label.setContents("<font color=\"" + INFO_COLOR + "\">[" + OutputMessage.Type.INFO.name() + "] "
            + message.getMessage() + "</font>");

      }
      else if (message.getType() == OutputMessage.Type.WARNING)
      {
         label.setContents("<font color=\"" + WARNING_COLOR + "\">[" + OutputMessage.Type.WARNING.name() + "] "
            + message.getMessage() + "</font>");

      }
      else if (message.getType() == OutputMessage.Type.ERROR)
      {
         label.setContents("<font color=\"" + ERROR_COLOR + "\">[" + OutputMessage.Type.ERROR.name() + "] "
            + message.getMessage() + "</font>");
      }
      else if (message.getType() == OutputMessage.Type.OUTPUT)
      {
         label.setContents("<font color=\"" + OUTPUT_COLOR + "\">[" + OutputMessage.Type.OUTPUT.name() + "] "
            + message.getMessage() + "</font>");
      }

      if (odd)
      {
         label.setBackgroundColor("#f8f8f8");
      }
      odd = !odd;

      label.setWidth100();
      label.setAutoHeight();
      label.setPadding(4);
      label.setCanSelectText(true);
      label.setCursor(Cursor.TEXT);
      outputLayout.addMember(label);
      scrollToBottomTimer.schedule(100);
   }

   private Timer scrollToBottomTimer = new Timer()
   {

      @Override
      public void run()
      {
         outputLayout.scrollToBottom();
      }

   };

   @Override
   public String getTitle()
   {
      return "<span>" + Canvas.imgHTML(Images.OutputPanel.ICON) + "&nbsp;Output</span>";
   }

   public HasClickHandlers getClearOutputButton()
   {
      return clearOutputButton;
   }

   @Override
   public String getId()
   {
      return "Output";
   }

}
