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
package org.exoplatform.ide.client.operation.output;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;

import org.exoplatform.gwtframework.ui.client.component.ImgButton;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.LockableView;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OutputForm extends LockableView implements OutputPresenter.Display
{

   /**
    * 
    */
   private static final String OUTPUT_VIEW_ID = "ideOutputView";

   private final static String ID = "ideOutputForm"; 
   
   private OutputPresenter presenter;

   private VLayout outputLayout;

   private ImgButton clearOutputButton;

   public OutputForm(HandlerManager eventBus)
   {
      super(OUTPUT_VIEW_ID,eventBus, true);
      
//      setVertical(Boolean.TRUE);
      
      
      setOverflow(Overflow.HIDDEN);

      outputLayout = new VLayout();
      outputLayout.setWidth100();
      outputLayout.setHeight100();
      outputLayout.setOverflow(Overflow.SCROLL);
      outputLayout.setCanFocus(Boolean.TRUE);
      outputLayout.setID(ID);
      addMember(outputLayout);

      clearOutputButton = new ImgButton();
      clearOutputButton.setSrc(Images.OutputPanel.BUTTON_CLEAR);
      clearOutputButton.setWidth(20);
      clearOutputButton.setHeight(18);
      clearOutputButton.setCanFocus(false);
      clearOutputButton.setTooltip("Clear output");
      //clearOutputButton1.disable();
      addTabButton(clearOutputButton);
      
      image = new Image(IDEImageBundle.INSTANCE.output());
      
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

   private Image image;

   @Override
   public String getTitle()
   {
      return "Output";
   }

   /**
    * @return the image
    */
   public Image getImage()
   {
      return image;
   }

   public HasClickHandlers getClearOutputButton()
   {
      return clearOutputButton;
   }

   public String getId()
   {
      return "Output";
   }

}
