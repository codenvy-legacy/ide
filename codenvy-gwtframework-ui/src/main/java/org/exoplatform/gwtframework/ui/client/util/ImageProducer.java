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
