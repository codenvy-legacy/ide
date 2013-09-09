/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
