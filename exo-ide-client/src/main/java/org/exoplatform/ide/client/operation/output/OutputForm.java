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

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ImgButton;
import org.exoplatform.ideall.client.IDEImageBundle;
import org.exoplatform.ideall.client.ImageUtil;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.framework.output.event.OutputMessage;
import org.exoplatform.ideall.client.framework.ui.TabPanel;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OutputForm extends TabPanel implements OutputPresenter.Display
{

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
      clearOutputButton.setSrc(Images.OutputPanel.BUTTON_CLEAR);
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
      OutputRecord record = new OutputRecord(message, odd);
      odd = !odd;
      outputLayout.addMember(record);
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
      Image image = new Image(IDEImageBundle.INSTANCE.output());
      String html = ImageUtil.getHTML(image);
      return "<span>" + html + "&nbsp;Output</span>";
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
