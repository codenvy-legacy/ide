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
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.LaunchDebuggerEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.LaunchDebuggerHandler;
import org.exoplatform.ide.extension.java.jdi.client.ui.DebuggerView;
import org.exoplatform.ide.extension.java.jdi.client.ui.RunDebuggerView;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointEventList;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointList;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Location;
import org.exoplatform.ide.extension.java.jdi.shared.StackFrameDump;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

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
public class DebuggerPresenter implements DebuggerConnectedHandler, LaunchDebuggerHandler, ViewClosedHandler
{
   private Display display;

   private String id;

   public interface Display extends IsView
   {

      HasClickHandlers getResumeButton();

      HasClickHandlers getRemoveAllBreakpointsButton();

      HasClickHandlers getDisconnectButton();

      HasClickHandlers getAddBreakPointButton();

      HasClickHandlers getBreakPointsButton();

      HasClickHandlers getCheckEventsButton();

      void addBreakPoint(BreakPoint breakPoint);

      void setBreakPoints(BreakPointList breakPoints);

      void cleare();

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
               DebuggerClientService.getInstance().resume(id, new AsyncRequestCallback()
               {

                  @Override
                  protected void onSuccess(Object result)
                  {

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

      display.getBreakPointsButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            AutoBean<BreakPointList> autoBean = DebuggerExtension.AUTO_BEAN_FACTORY.create(BreakPointList.class);
            AutoBeanUnmarshaller<BreakPointList> unmarshaller = new AutoBeanUnmarshaller<BreakPointList>(autoBean);
            try
            {
               DebuggerClientService.getInstance().getBreakPoints(id,
                  new AsyncRequestCallback<BreakPointList>(unmarshaller)
                  {

                     @Override
                     protected void onSuccess(BreakPointList result)
                     {
                        display.setBreakPoints(result);
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
               e.printStackTrace();
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
               DebuggerClientService.getInstance().disconnect(id, new AsyncRequestCallback()
               {

                  @Override
                  protected void onSuccess(Object result)
                  {
                     IDE.eventBus().fireEvent(new DebuggerDisconnectedEvent());
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

      display.getAddBreakPointButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            AutoBean<BreakPoint> autoBean = DebuggerExtension.AUTO_BEAN_FACTORY.create(BreakPoint.class);
            final BreakPoint breakPoint = autoBean.as();

            AutoBean<Location> autoBeanlocation = DebuggerExtension.AUTO_BEAN_FACTORY.create(Location.class);
            final Location location = autoBeanlocation.as();

            location.setClassName("org.exoplatform.services.jcr.webdav.WebDavServiceImpl");
            location.setLineNumber(645);

            breakPoint.setLocation(location);
            breakPoint.setEnabled(true);

            try
            {
               DebuggerClientService.getInstance().addBreakPoint(id, breakPoint, new AsyncRequestCallback<BreakPoint>()
               {

                  @Override
                  protected void onSuccess(BreakPoint result)
                  {
                     display.addBreakPoint(breakPoint);
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
            display.cleare();
         }
      });

      display.getCheckEventsButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            AutoBean<BreakPointEventList> breakPointEvents =
               DebuggerExtension.AUTO_BEAN_FACTORY.create(BreakPointEventList.class);
            AutoBeanUnmarshaller<BreakPointEventList> unmarshaller =
               new AutoBeanUnmarshaller<BreakPointEventList>(breakPointEvents);
            try
            {
               DebuggerClientService.getInstance().checkEvents(id,
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
                        exception.printStackTrace();
                        Window.alert(exception.getMessage());
                     }
                  });
            }
            catch (RequestException e)
            {
               e.printStackTrace();
            }
         }
      });
   }

   private void doGetDump()
   {
      AutoBean<StackFrameDump> autoBean = DebuggerExtension.AUTO_BEAN_FACTORY.create(StackFrameDump.class);
      AutoBeanUnmarshaller<StackFrameDump> unmarshaller = new AutoBeanUnmarshaller<StackFrameDump>(autoBean);
      try
      {
         DebuggerClientService.getInstance().dump(id, new AsyncRequestCallback<StackFrameDump>(unmarshaller)
         {

            @Override
            protected void onSuccess(StackFrameDump result)
            {
               display.getDataProvider().getList().addAll(result.getFields());
               display.getDataProvider().getList().addAll(result.getLocalVariables());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
            }
         });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public void onDebuggerConnected(DebuggerConnectedEvent event)
   {
      if (display == null)
      {
         display = new DebuggerView();
         bindDisplay(display);
         IDE.getInstance().openView(display.asView());

         DebuggerInfo debuggerInfo = event.getDebuggerInfo();
         DebuggerExtension.DEBUG_ID = event.getDebuggerInfo().getId();
         id = event.getDebuggerInfo().getId();
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
            DebuggerClientService.getInstance().checkEvents(DebuggerExtension.DEBUG_ID,
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
}
