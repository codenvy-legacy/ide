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
package com.codenvy.ide.api.resources;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.resources.client.ImageResource;


/**
 * FileType is meta information about file.
 * It's contains
 * <ul>
 * <li> <code>image</code> - image resource associated with file
 * <li> <code>mimeTypes</code> - array of mime types associated with file
 * <li> <code>extension</code> - extension associated with file
 * <li> <code>namePattern</code> - name pattern
 * </ul>
 * <p>Mime types is array in case when one file type can describe several mime types.(For example JavaScript file can have:
 * "application/javascript", "application/x-javascript", "text/javascript" mime types)
 * <p/>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public class FileType {

    private static int ID = 0;

    private int id;

    private ImageResource image;

    private Array<String> mimeTypes;

    private String extension;

    private String namePattern;


    public FileType(ImageResource image, String mimeType, String extension) {
        this(image, Collections.createArray(mimeType), extension);
    }

    public FileType(ImageResource image, Array<String> mimeTypes, String extension) {
        this(image, mimeTypes, extension, null);
    }

    public FileType(ImageResource image, String namePattern) {
        this(image, null, null, namePattern);
    }

    private FileType(ImageResource image, Array<String> mimeTypes, String extension, String namePattern) {
        super();
        this.image = image;
        this.mimeTypes = mimeTypes;
        this.extension = extension;
        this.namePattern = namePattern;
        id = ++ID;
    }

    /** @return the mimeTypes */
    public Array<String> getMimeTypes() {
        return mimeTypes;
    }

    /** @return the extension */
    public String getExtension() {
        return extension;
    }

    /** @return the namePatterns */
    public String getNamePattern() {
        return namePattern;
    }

    /** @return the image */
    public ImageResource getImage() {
        return image;
    }

    public int getId() {
        return id;
    }
}
