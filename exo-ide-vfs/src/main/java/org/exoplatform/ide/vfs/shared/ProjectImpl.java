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
package org.exoplatform.ide.vfs.shared;

import java.util.List;
import java.util.Map;

/** The Project - folder w/ special meaning. */
public class ProjectImpl extends FolderImpl implements Project {

    protected String projectType;

    @SuppressWarnings("rawtypes")
    public ProjectImpl(String vfsId, String id, String name, String mimeType, String path, String parentId, long creationDate,
                       List<Property> properties, Map<String, Link> links, String projectType) {
        super(vfsId, id, name, ItemType.PROJECT, mimeType, path, parentId, creationDate, properties, links);
        this.projectType = projectType;
    }

    public ProjectImpl() {
        super(ItemType.PROJECT);
        mimeType = PROJECT_MIME_TYPE;
    }

    @Override
    public String getProjectType() {
        return projectType;
    }

    @Override
    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

}
