/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.resources;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.resources.client.ImageResource;


/**
 * FileType is meta information about file.
 * It's contains
 * <ul>
 * <li> <code>contentDescription</code> - string description associated with file content
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

    private String contentDescription;

    public FileType(ImageResource image, String mimeType, String extension) {
        this(image, Collections.createArray(mimeType), extension);
    }

    public FileType(String contentDescription, ImageResource image, String mimeType, String extension) {
        this(image, Collections.createArray(mimeType), extension);
        this.contentDescription = contentDescription;
    }

    public FileType(ImageResource image, Array<String> mimeTypes, String extension) {
        this(image, mimeTypes, extension, null);
    }

    public FileType(String contentDescription, ImageResource image, Array<String> mimeTypes, String extension) {
        this(image, mimeTypes, extension, null);
        this.contentDescription = contentDescription;
    }

    public FileType(ImageResource image, String namePattern) {
        this(image, null, null, namePattern);
    }

    public FileType(String contentDescription, ImageResource image, String namePattern) {
        this(image, null, null, namePattern);
        this.contentDescription = contentDescription;
    }

    private FileType(ImageResource image, Array<String> mimeTypes, String extension, String namePattern) {
        this(null, image, mimeTypes, extension, namePattern);
    }

    private FileType(String contentDescription, ImageResource image, Array<String> mimeTypes, String extension, String namePattern) {
        super();
        this.contentDescription = contentDescription;
        this.image = image;
        this.mimeTypes = mimeTypes;
        this.extension = extension;
        this.namePattern = namePattern;
        id = ++ID;
    }

    /** @return the contentDescription */
    public String getContentDescription() {
        return contentDescription;
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
