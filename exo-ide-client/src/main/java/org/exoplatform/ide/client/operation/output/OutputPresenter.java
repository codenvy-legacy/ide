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
package org.exoplatform.ide.client.operation.output;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputHandler;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OutputPresenter implements OutputHandler
{

   public interface Display
   {

      void clearOutput();

      void outMessage(OutputMessage message);

      HasClickHandlers getClearOutputButton();

   }

   private HandlerManager eventBus;

   private Display display;

   private HandlerRegistration outputHandler;

   private List<OutputMessage> messages = new ArrayList<OutputMessage>();

   public OutputPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   public void destroy()
   {
      outputHandler.removeHandler();
   }

   public void bindDisplay(Display d)
   {
      display = d;

      outputHandler = eventBus.addHandler(OutputEvent.TYPE, this);

      display.getClearOutputButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            messages.clear();
            display.clearOutput();
         }
      });
   }

   public void onOutput(OutputEvent event)
   {
      OutputMessage message = new OutputMessage(event.getMessage(), event.getOutputType());
      if (message.getType() == OutputMessage.Type.LOG)
      {
         return;
      }
      messages.add(message);
      display.outMessage(message);
   }

}
