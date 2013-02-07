/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.notification;


import com.google.collide.client.util.AnimationController;
import com.google.collide.client.util.AnimationController.AnimationStateListener;
import com.google.collide.client.util.AnimationController.State;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental.html.Element;

import java.util.LinkedList;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NotificationManager
{
   public interface InitialPositionCallback
   {
      public int getBorderX();

      public int getBorderY();
   }

   public interface Css extends CssResource
   {
      String notification();
   }

   public interface Resources extends ClientBundle
   {
      @Source("com/codenvy/ide/notification/Notification.css")
      Css styles();

      @Source("com/codenvy/ide/notification/fancy_closebox.png")
      ImageResource closeImage();
   }


   private class NotificationPanel extends Composite
   {
      private final Image close = new Image(resources.closeImage());

      private final FocusPanel focus = new FocusPanel();

      private final AbsolutePanel panel = new AbsolutePanel();

      public NotificationPanel(final Widget widget, final PopupPanel popup)
      {
         super.initWidget(focus);
         focus.setWidget(panel);
         widget.getElement().getStyle().setPaddingTop(20, Unit.PX);
         panel.add(widget);

         close.addClickHandler(new ClickHandler()
         {
            @Override
            public void onClick(final ClickEvent event)
            {
               animationController.hide((Element)popup.getElement());
               //               popup.hide();
            }
         });
         focus.addMouseOverHandler(new MouseOverHandler()
         {

            @Override
            public void onMouseOver(final MouseOverEvent event)
            {
               panel.add(close, panel.getOffsetWidth() - 20, 0);
            }
         });
         focus.addMouseOutHandler(new MouseOutHandler()
         {
            @Override
            public void onMouseOut(final MouseOutEvent event)
            {
               panel.remove(close);
            }
         });
      }

   }

   private static final int MAX_SHOWING_NOTIFICATIONS = 10;

   private static final int SPACING = 6;

   private static final Resources resources = GWT.create(Resources.class);

   private final InitialPositionCallback position;

   private final LinkedList<Notification> notifications = new LinkedList<Notification>();

   private final LinkedList<PopupPanel> popups = new LinkedList<PopupPanel>();

   private final LinkedList<PopupPanel> pendingPopups = new LinkedList<PopupPanel>();

   private final LinkedList<Notification> pendingNotifications = new LinkedList<Notification>();

   private final AnimationController animationController;

   public NotificationManager(final InitialPositionCallback position)
   {
      this.position = position;
      animationController = AnimationController.FADE_ANIMATION_CONTROLLER;
      resources.styles().ensureInjected();
      Window.addResizeHandler(new ResizeHandler()
      {
         @Override
         public void onResize(final ResizeEvent event)
         {
            reflow();
         }
      });
      Window.addWindowScrollHandler(new Window.ScrollHandler()
      {
         @Override
         public void onWindowScroll(final ScrollEvent event)
         {
            reflow();
         }
      });
   }

   public void addNotification(final Notification notification)
   {
      final PopupPanel pop = new PopupPanel(false, false);
      final NotificationPanel panel = new NotificationPanel(notification, pop);
      pop.setStyleName(resources.styles().notification());
      pop.setWidget(panel);
      pop.setAnimationEnabled(false);
      pop.addCloseHandler(new CloseHandler<PopupPanel>()
      {
         @Override
         public void onClose(final CloseEvent<PopupPanel> event)
         {
            final int idx = popups.indexOf(pop);
            notifications.remove(idx);
            popups.remove(idx);
            reflow();
            checkPending();
         }
      });

      if (popups.size() == MAX_SHOWING_NOTIFICATIONS)
      {
         pendingPopups.add(pop);
         pendingNotifications.add(notification);
      }
      else
      {
         positionAndShow(pop, notification);

         notifications.add(notification);
         popups.add(pop);
      }
   }

   private void checkPending()
   {
      if(!pendingNotifications.isEmpty())
      {
         Notification notification = pendingNotifications.removeFirst();
         PopupPanel pop = pendingPopups.removeFirst();
         positionAndShow(pop, notification);
         notifications.add(notification);
         popups.add(pop);
      }
   }

   public void expireNotification(final Notification notification)
   {
      final int idx = notifications.indexOf(notification);
      if (idx != -1)
      {
         popups.get(idx).hide();
      }
   }

   ;

   private void positionAndShow(final PopupPanel pop, final Notification notification)
   {
      pop.setVisible(false);
      PopupPanel last = null;
      if (!notifications.isEmpty())
      {
         last = popups.getLast();
      }

      final int y = (last == null) ? position.getBorderY() : last.getAbsoluteTop() + last.getOffsetHeight() + SPACING;

      pop.setPopupPositionAndShow(new PopupPanel.PositionCallback()
      {
         @Override
         public void setPosition(final int offsetWidth, final int offsetHeight)
         {
            pop.setPopupPosition(position.getBorderX() - offsetWidth, y);
         }
      });
      pop.setVisible(true);
      animationController.setAnimationStateListener((Element)pop.getElement(), new AnimationStateListener()
      {
         @Override
         public void onAnimationStateChanged(Element element, State state)
         {
            if (State.HIDING == state)
            {
               new Timer()
               {

                  @Override
                  public void run()
                  {
                     pop.hide();
                  }
               }.schedule(200);
            }
         }
      });
      animationController.show((Element)pop.getElement());

      new Timer()
      {
         @Override
         public void run()
         {
            if (pop.isShowing())
            {
               animationController.hide((Element)pop.getElement());
            }
         }
      }.schedule(notification.getDuration());

   }

   private void reflow()
   {
      PopupPanel last = null;
      int y = 0;
      for (int i = 0; i < popups.size(); i++)
      {
         final PopupPanel pop = popups.get(i);
         y = (last == null) ? position.getBorderY() : y + last.getOffsetHeight() + SPACING;
         pop.setPopupPosition(position.getBorderX() - pop.getOffsetWidth(), y);
         last = pop;
      }
   }

}