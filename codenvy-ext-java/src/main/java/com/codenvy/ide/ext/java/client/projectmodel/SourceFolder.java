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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.ide.resources.model.Folder;
import com.google.gwt.json.client.JSONObject;

/** @author Nikolay Zamosenchuk */
public class SourceFolder extends Folder {
    public static final String TYPE = "java.sourcefolder";
    private String sourceFolderName;

    protected SourceFolder() {
        super(TYPE, FOLDER_MIME_TYPE);
    }

    /**
     * Init Java Source Folder from JSon Object
     *
     * @param itemObject
     */
    protected SourceFolder(JSONObject itemObject, String name) {
        this();
        init(itemObject);
        sourceFolderName = name;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return sourceFolderName;
    }

    /** {@inheritDoc} */
    @Override
    public String getPath() {
        return parent.getPath() + "/" + getName();
    }

    public void init(JSONObject object, String projectPath) {
        init(object);
        sourceFolderName = object.get("name").isString().stringValue();
    }
}
