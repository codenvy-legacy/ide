/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.LaunchDebuggerEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.LaunchDebuggerHandler;
import org.exoplatform.ide.extension.java.jdi.client.ui.DebuggerView;
import org.exoplatform.ide.extension.java.jdi.client.ui.RunDebuggerView;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEventList;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.StackFrameDump;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class DebuggerPresenter implements DebuggerConnectedHandler, DebuggerDisconnectedHandler, LaunchDebuggerHandler,
   ViewClosedHandler, BreakPointsUpdatedHandler
{
   private Display display;

   private DebuggerInfo debuggerInfo;

   public interface Display extends IsView
   {

      HasClickHandlers getResumeButton();

      HasClickHandlers getRemoveAllBreakpointsButton();

      HasClickHandlers getDisconnectButton();

      void setBreakPoints(List<BreakPoint> breakPoints);

      void cleareVariabels();

      ListDataProvider<Variable> getDataProvider();

   }

   void bindDisplay(Display d)
   {
      this.display = d;

      display.getResumeButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            try
            {
               DebuggerClientService.getInstance().resume(debuggerInfo.getId(), new AsyncRequestCallback<String>()
               {

                  @Override
                  protected void onSuccess(String result)
                  {
                     display.getDataProvider().getList().clear();
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(exception));
                  }

               });
            }
            catch (RequestException e)
            {
               IDE.fireEvent(new ExceptionThrownEvent(e));
            }
         }

      });

      display.getDisconnectButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            try
            {
               DebuggerClientService.getInstance().disconnect(debuggerInfo.getId(), new AsyncRequestCallback<String>()
               {

                  @Override
                  protected void onSuccess(String result)
                  {
                     IDE.eventBus().fireEvent(new DebuggerDisconnectedEvent());
                     debuggerInfo = null;
                     checkDebugEventsTimer.cancel();
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(exception));
                  }
               });

            }
            catch (RequestException e)
            {
               IDE.fireEvent(new ExceptionThrownEvent(e));
            }

         }
      });

      display.getRemoveAllBreakpointsButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            Window.alert("Not implement yet");
         }
      });
   }

   private void doGetDump()
   {
      AutoBean<StackFrameDump> autoBean = DebuggerExtension.AUTO_BEAN_FACTORY.create(StackFrameDump.class);
      AutoBeanUnmarshaller<StackFrameDump> unmarshaller = new AutoBeanUnmarshaller<StackFrameDump>(autoBean);
      try
      {
         DebuggerClientService.getInstance().dump(debuggerInfo.getId(), new AsyncRequestCallback<StackFrameDump>(unmarshaller)
         {

            @Override
            protected void onSuccess(StackFrameDump result)
            {
//               display.cleareVariabels();
               display.getDataProvider().getList().clear();
               //
               display.getDataProvider().getList().addAll(result.getFields());
               display.getDataProvider().getList().addAll(result.getLocalVariables());
               display.getDataProvider().flush();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
            }
         });
      }
      catch (RequestException e)
      {
         IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
      }
   }

   @Override
   public void onDebuggerConnected(DebuggerConnectedEvent event)
   {
      if (display == null)
      {
         debuggerInfo = event.getDebuggerInfo();
         display = new DebuggerView(debuggerInfo);
         bindDisplay(display);
         IDE.getInstance().openView(display.asView());
         checkDebugEventsTimer.scheduleRepeating(3000);
      }
   }

   /**
    * A timer for checking events
    */
   private Timer checkDebugEventsTimer = new Timer()
   {
      @Override
      public void run()
      {
         AutoBean<BreakPointEventList> breakPointEvents =
            DebuggerExtension.AUTO_BEAN_FACTORY.create(BreakPointEventList.class);
         AutoBeanUnmarshaller<BreakPointEventList> unmarshaller =
            new AutoBeanUnmarshaller<BreakPointEventList>(breakPointEvents);
         try
         {
            DebuggerClientService.getInstance().checkEvents(debuggerInfo.getId(),
               new AsyncRequestCallback<BreakPointEventList>(unmarshaller)
               {
                  @Override
                  protected void onSuccess(BreakPointEventList result)
                  {
                     if (result != null && result.getEvents().size() > 0)
                     {
                        doGetDump();
                     }
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     cancel();
                     IDE.fireEvent(new ExceptionThrownEvent(exception));
                  }
               });
         }
         catch (RequestException e)
         {
            IDE.fireEvent(new ExceptionThrownEvent(e));
         }
      }
   };

   @Override
   public void onLaunchDebugger(LaunchDebuggerEvent event)
   {
      RunDebuggerPresenter runDebuggerPresenter = new RunDebuggerPresenter();
      RunDebuggerView view = new RunDebuggerView();
      runDebuggerPresenter.bindDisplay(view);
      IDE.getInstance().openView(view.asView());
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onDebuggerDisconnected(DebuggerDisconnectedEvent event)
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   @Override
   public void onBreakPointsUpdated(BreakPointsUpdatedEvent event)
   {
      if (event.getBreakPoints() != null)
      {
         List<BreakPoint> breakPoints = new ArrayList<BreakPoint>();
         Collection<Set<EditorBreakPoint>> values = event.getBreakPoints().values();
         for (Set<EditorBreakPoint> ebps : values)
         {
            for (EditorBreakPoint editorBreakPoint : ebps)
            {
               breakPoints.add(editorBreakPoint.getBreakPoint());
            }
         }
         display.setBreakPoints(breakPoints);
      }
   }
}
