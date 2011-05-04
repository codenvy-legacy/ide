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
package org.exoplatform.ide.client.operation.ui;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OutputView extends ViewImpl implements org.exoplatform.ide.client.operation.OutputPresenter.Display
{

   private final static String ID = "ideOutputForm";

   private FlowPanel outputLayout;

   private Image clearOutputButton;

   public OutputView()
   {
      super(ID, ViewType.OPERATION, "Output", new Image(IDEImageBundle.INSTANCE.output()));

      outputLayout = new FlowPanel();
      outputLayout.getElement().setId(ID);
      add(outputLayout, true);
      
      clearOutputButton = new Image(Images.OutputPanel.BUTTON_CLEAR);
      clearOutputButton.setWidth(20 + "px");
      clearOutputButton.setHeight(18 + "px");
      clearOutputButton.setTitle("Clear output");
   }

   public void clearOutput()
   {
      outputLayout.clear();
   }

   private boolean odd = true;

   public void outMessage(OutputMessage message)
   {
      OutputRecord record = new OutputRecord(message, odd);
      odd = !odd;
      outputLayout.add(record);
      //getScrollPanel().scrollToBottom();
   }

   public HasClickHandlers getClearOutputButton()
   {
      return clearOutputButton;
   }

}
