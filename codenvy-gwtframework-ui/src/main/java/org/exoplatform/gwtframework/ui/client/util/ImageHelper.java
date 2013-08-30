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

package org.exoplatform.gwtframework.ui.client.util;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ImageHelper {

    /**
     *
     */
    private static AbsolutePanel imagePanel;

    /**
     * @param image
     * @return
     */
    public static final String getImageHTML(Image image) {
        if (imagePanel == null) {
            imagePanel = new AbsolutePanel();
            imagePanel.getElement().getStyle().setWidth(16, Unit.PX);
            imagePanel.getElement().getStyle().setHeight(16, Unit.PX);
            imagePanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
            RootPanel.get().add(imagePanel, -10000, -10000);
        }

        imagePanel.add(image);
        String imageHTML = DOM.getInnerHTML(imagePanel.getElement());
        imagePanel.clear();
        return imageHTML;
    }

    /**
     * @param imageResource
     * @return
     */
    public static final String getImageHTML(ImageResource imageResource) {
        Image image = new Image(imageResource);
        return getImageHTML(image);
    }

    /**
     * @param imageURL
     * @return
     */
    public static String getImageHTML(String imageURL) {
        if (imageURL == null) {
            imageURL = "";
        }
        return "<img src=\"" + imageURL + "\" />";
    }

}
