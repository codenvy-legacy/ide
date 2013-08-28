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
package org.exoplatform.ide.client.framework.util;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.client.framework.ui.IconImageBundle;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ImageUtil {

    private static HashMap<String, ImageResource> images = new HashMap<String, ImageResource>();

    static {
        // TODO
        images.put(null, IconImageBundle.INSTANCE.defaultFile());

        images.put(Folder.FOLDER_MIME_TYPE, IconImageBundle.INSTANCE.folder());
        images.put(Project.PROJECT_MIME_TYPE, IconImageBundle.INSTANCE.defaultProject());
    }

    public static void putIcon(String mimeType, ImageResource imageResource) {
        images.put(mimeType, imageResource);
    }

    public static ImageResource getIcon(String mimeType) {
        ImageResource icon = images.get(mimeType);
        if (icon == null) {
            icon = IconImageBundle.INSTANCE.defaultFile();
        }
        return icon;
    }

    public static Map<String, ImageResource> getIcons() {
        return images;
    }

    /**
     * @param newFile
     * @return
     */
    public static ImageResource getIcon(FileModel file) {
        return getIcon(file.getMimeType());
    }

}
