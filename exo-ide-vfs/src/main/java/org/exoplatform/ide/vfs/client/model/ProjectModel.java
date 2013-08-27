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
package org.exoplatform.ide.vfs.client.model;

import com.google.gwt.json.client.JSONObject;

import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** @version $Id:$ */
public class ProjectModel extends FolderModel implements Project {

    protected String projectType;

    public ProjectModel() {
        super();
    }

    public ProjectModel(String name, FolderModel parent, String projectType, List<Property> properties) {
        this(null, null, name, PROJECT_MIME_TYPE, parent.createPath(name), parent.getId(), new Date().getTime(),
             properties, new HashMap<String, Link>(), projectType);
        this.parent = parent;
    }

    public ProjectModel(ProjectModel project) {
        this(project.getVfsId(), project.getId(), project.getName(), PROJECT_MIME_TYPE, project.getPath(), project.getParentId(),
             project.getCreationDate(), project.getProperties(), project.getLinks(), project.getProjectType());
    }

    public ProjectModel(String vfsId, String id, String name, String mimeType, String path, String parentId, long creationDate,
                        List<Property> properties, Map<String, Link> links, String projectType) {
        super(vfsId, id, name, ItemType.PROJECT, mimeType, path, parentId, creationDate, properties, links);
        this.projectType = projectType;
    }

    public ProjectModel(JSONObject itemObject) {
        super();
        init(itemObject);
    }

    public void init(JSONObject itemObject) {
        super.init(itemObject);
        projectType = (itemObject.get("projectType") != null) ? itemObject.get("projectType").isString().stringValue() : null;
        this.persisted = true;
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
