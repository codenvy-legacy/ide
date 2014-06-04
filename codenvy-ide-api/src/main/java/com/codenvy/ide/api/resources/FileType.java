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
