/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.editor.codemirror;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class NotificationWidget {

    private Element targetElement;

    private FlowPanel notificationPanel;

    private String title;

    public NotificationWidget(Element targetElement, int offsetLeft, int offsetTop) {
        this.targetElement = targetElement;

        title = targetElement.getAttribute("title");
        targetElement.removeAttribute("title");

        notificationPanel = new FlowPanel();
        notificationPanel.setStyleName("editor-notification");
        notificationPanel.getElement().getStyle().setVisibility(Visibility.HIDDEN);

        RootPanel.get().add(notificationPanel);
        int left = targetElement.getAbsoluteLeft() + targetElement.getOffsetWidth() + 5;
        int top = targetElement.getAbsoluteTop();

        notificationPanel.getElement().getStyle().setLeft(left + offsetLeft, Unit.PX);
        notificationPanel.getElement().getStyle().setTop(top + offsetTop, Unit.PX);

        notificationPanel.getElement().setInnerHTML(title);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                correctPosition();
            }
        });
    }

    public void destroy() {
        if (!targetElement.hasAttribute("title") || targetElement.getAttribute("title").isEmpty())
            targetElement.setAttribute("title", title);
        notificationPanel.removeFromParent();
    }

    private void correctPosition() {
        if (notificationPanel.getAbsoluteTop() + notificationPanel.getOffsetHeight() > Window.getClientHeight()) {
            int top = Window.getClientHeight() - notificationPanel.getOffsetHeight();
            // int top = targetElement.getAbsoluteTop() - notificationPanel.getOffsetHeight();
            notificationPanel.getElement().getStyle().setTop(top, Unit.PX);
        }
        notificationPanel.getElement().getStyle().setVisibility(Visibility.VISIBLE);

        if (notificationPanel.getAbsoluteLeft() + notificationPanel.getOffsetWidth() > Window.getClientWidth()) {
            notificationPanel.getElement().getStyle().clearLeft();
            notificationPanel.getElement().getStyle()
                             .setRight(Window.getClientWidth() - targetElement.getAbsoluteLeft() + 10, Unit.PX);
        }
    }

    /**
     *
     */
    public void update() {
        title = targetElement.getAttribute("title");
        targetElement.setAttribute("title", null);
        notificationPanel.getElement().setInnerHTML(title);
    }

}
