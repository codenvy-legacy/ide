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
package org.exoplatform.ide.client.operation;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputHandler;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewDisplay;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.operation.ui.OutputForm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OutputPresenter implements OutputHandler, ViewClosedHandler
{

   public interface Display extends ViewDisplay
   {

      void clearOutput();

      void outMessage(OutputMessage message);

      HasClickHandlers getClearOutputButton();

   }

   private HandlerManager eventBus;

   private Display display;

   private List<OutputMessage> messages = new ArrayList<OutputMessage>();

   public OutputPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(OutputEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {

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
      try
      {
         eventBus.fireEvent(new RestorePerspectiveEvent());
         if (display == null)
         {
            display = GWT.create(OutputForm.class);
            bindDisplay();
            IDE.getInstance().openView((ViewEx)display);
         }
         else
         {
            ((ViewEx)display).setViewVisible();
         }
         OutputMessage message = new OutputMessage(event.getMessage(), event.getOutputType());
         if (message.getType() == OutputMessage.Type.LOG)
         {
            return;
         }
         messages.add(message);
         display.outMessage(message);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

}
