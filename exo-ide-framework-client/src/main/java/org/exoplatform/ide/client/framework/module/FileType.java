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
package org.exoplatform.ide.client.framework.module;

import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class FileType {

    /** File mime type. */
    String mimeType;

    /** Default file extension. */
    String extension;

    /** File type icon. */
    ImageResource icon;

    public FileType(String mimeType, String extension, ImageResource icon) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.icon = icon;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public ImageResource getIcon() {
        return icon;
    }

    public void setIcon(ImageResource icon) {
        this.icon = icon;
    }

}
