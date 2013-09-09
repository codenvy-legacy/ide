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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ImageProducer {

    private String imageURL;

    private String disabledImageURL;

    private ImageResource imageResource;

    private ImageResource disabledImageResource;

    public ImageProducer(ImageResource imageResource) {
        this.imageResource = imageResource;
    }

    public ImageProducer(String imageURL) {
        this.imageURL = imageURL;
    }

    public ImageProducer(ImageResource imageResource, ImageResource disabledImageResource) {
        this.imageResource = imageResource;
        this.disabledImageResource = disabledImageResource;
    }

    public ImageProducer(String imageURL, String disabledImageURL) {
        this.imageURL = imageURL;
        this.disabledImageURL = disabledImageURL;
    }

    public Image getImage() {
        if (imageResource != null) {
            return new Image(imageResource);
        }

        if (imageURL != null) {
            return new Image(imageURL);
        }

        return null;
    }

    public Image getDisabledImage() {
        if (disabledImageResource != null) {
            return new Image(disabledImageResource);
        }

        if (disabledImageURL != null) {
            return new Image(disabledImageURL);
        }

        if (imageResource != null) {
            return new Image(imageResource);
        }

        if (imageURL != null) {
            return new Image(imageURL);
        }

        return null;
    }

}
