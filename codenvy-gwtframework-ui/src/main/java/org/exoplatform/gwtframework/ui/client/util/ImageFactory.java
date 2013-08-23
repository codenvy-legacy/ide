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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ImageFactory {

    private static Map<String, ImageProducer> images = new HashMap<String, ImageProducer>();

    private static ImageProducer defaultImageProducer = new ImageProducer("gwtframework-images/default-image.png");

    //   private static ImageProducer defaultImageProducer = new ImageProducer(ShowCaseImageBundle.INSTANCE.add(),
    //      ShowCaseImageBundle.INSTANCE.addDisabled());

    public static List<String> getImageNames() {
        return new ArrayList<String>(images.keySet());
    }

    public static Image getImage(String imageName) {
        ImageProducer producer = images.get(imageName);
        if (producer != null) {
            return producer.getImage();
        }

        return defaultImageProducer.getImage();
    }

    public static Image getDisabledImage(String imageName) {
        ImageProducer producer = images.get(imageName);
        if (producer != null) {
            return producer.getDisabledImage();
        }

        return defaultImageProducer.getImage();
    }

    public static void addImage(String imageName, ImageResource imageResource) {
        images.put(imageName, new ImageProducer(imageResource));
    }

    public static void addImage(String imageName, ImageResource imageResource, ImageResource disabledImageResource) {
        images.put(imageName, new ImageProducer(imageResource, disabledImageResource));
    }

}
