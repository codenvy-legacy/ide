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
package org.exoplatform.ide.client.outline;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;

/**
 * Is used to set empty tree message.
 * CellTree has hard coded empty message, which cannot be changed.
 * So this node is added instead of the empty message.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 24, 2012 9:16:16 AM anya $
 */
public class EmptyTreeMessage {
    /** Image to display (loader for example). */
    private Image image;

    /** Message to display. */
    private String message;

    /**
     * @param image
     * @param message
     */
    public EmptyTreeMessage(Image image, String message) {
        this.message = message;
        this.image = image;
        if (image != null) {
            DOM.setStyleAttribute(image.getElement(), "cssFloat", "left");
            DOM.setStyleAttribute(image.getElement(), "marginRight", "5px");
        }
    }

    /** @return the image */
    public Image getImage() {
        return image;
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }
}
