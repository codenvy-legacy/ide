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
package com.codenvy.ide.notification;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.AnchorElement;
import elemental.html.DivElement;
import elemental.html.Element;
import elemental.html.ParagraphElement;

import com.codenvy.ide.client.util.Elements;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Notification {

    public interface ClickCallback {
        void onClick(Notification notification);
    }

    private ClickCallback callback;

    private final int duration;

    private final Element element;


    public Notification(final String message, final int duration) {
        this(message, null, duration);
    }

    public Notification(String message, ClickCallback callback, final int duration) {
        this.callback = callback;
        this.duration = duration;
        element = createAnchorElement(message);
    }

    /** @return the duration */
    public int getDuration() {
        return duration;
    }

    public Element getElement() {
        return element;
    }

    private AnchorElement createAnchorElement(final String message) {
        AnchorElement anchorElement = Elements.createAnchorElement(/*css.anchor()*/);
        anchorElement.setHref("javascript:;");
        DivElement messageDiv = Elements.createDivElement(NotificationManager.resources.styles().message());
        ParagraphElement paragraphElement = Elements.createParagraphElement();
        paragraphElement.appendChild(Elements.createTextNode(message));
        messageDiv.appendChild(paragraphElement);
        anchorElement.appendChild(messageDiv);
        if (callback != null) {
            anchorElement.addEventListener(Event.CLICK, new EventListener() {
                @Override
                public void handleEvent(Event event) {
                    callback.onClick(Notification.this);
                }
            }, false);
        }
        return anchorElement;
    }
}
