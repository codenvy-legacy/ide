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
package org.exoplatform.ide.client.output;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputHandler;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OutputPresenter implements OutputHandler, ViewClosedHandler, ShowOutputHandler
{

   public interface Display extends IsView
   {

      void clearOutput();

      void outMessage(OutputMessage message);

      HasClickHandlers getClearOutputButton();

   }

   private Display display;

   private List<OutputMessage> messages = new ArrayList<OutputMessage>();

   public OutputPresenter()
   {
      IDE.addHandler(OutputEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ShowOutputEvent.TYPE, this);
      IDE.getInstance().addControl(new ShowOutputCommand(), Docking.NONE);
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

      for (OutputMessage message : messages)
      {
         display.outMessage(message);
      }
   }

   public void onOutput(OutputEvent event)
   {
      try
      {
         openView();

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
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.client.output.ShowOutputHandler#onShowOutput(org.exoplatform.ide.client.output.ShowOutputEvent)
    */
   @Override
   public void onShowOutput(ShowOutputEvent event)
   {
      openView();
   }

   /**
    * 
    */
   private void openView()
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView((View)display);
         bindDisplay();
      }
      else
      {
         ((View)display).setViewVisible();
      }

   }

}
