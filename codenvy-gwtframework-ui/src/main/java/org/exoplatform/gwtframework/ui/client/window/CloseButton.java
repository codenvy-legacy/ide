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
package org.exoplatform.gwtframework.ui.client.window;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import org.exoplatform.gwtframework.ui.client.WindowResource;

/**
 * Button for closing window (displayed at the right upper corner).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 3, 2011 11:09:49 AM anya $
 */
public class CloseButton extends WindowButton {
    /** Handler for close click action. */
    private CloseClickHandler closeClickHandler;

    /**
     * @param closeHandler
     *         close click window handler
     */
    public CloseButton(CloseClickHandler closeClickHandler) {
        super(WindowResource.INSTANCE.closeWindowButton(), WindowResource.INSTANCE.closeWindowButtonOver(),
              WindowResource.INSTANCE.closeWindowButtonDisabled());
        this.closeClickHandler = closeClickHandler;
        setPrompt("Close");

        addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doClick();
            }
        });
    }

    /** Perform actions on close button's click. */
    public void doClick() {
        if (closeClickHandler != null) {
            closeClickHandler.onCloseClick();
        }
    }

}
