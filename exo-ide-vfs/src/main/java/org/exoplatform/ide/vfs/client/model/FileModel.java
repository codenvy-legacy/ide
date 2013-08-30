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

import org.exoplatform.ide.vfs.client.JSONDeserializer;
import org.exoplatform.ide.vfs.shared.*;

import java.util.*;

/**
 * @author eXo
 * @version $Id: $
 */
public class FileModel extends FileImpl implements ItemContext {
    private boolean persisted;

    private String content = null;

    private boolean contentChanged = false;

    private HashSet<FileModel> versionHistory = new HashSet<FileModel>();

    private Lock lock = null;

    private ProjectModel project;

    private FolderModel parent;

    @SuppressWarnings("rawtypes")
    public FileModel(String name, String mimeType, String content, FolderModel parent) {
        super(null, null, name, parent.createPath(name), parent.getId(), new Date().getTime(), new Date().getTime(),
              null /* versionId */, mimeType, 0, false, new ArrayList<Property>(), new HashMap<String, Link>());
        this.persisted = false;
        this.content = content;
        this.parent = parent;

        fixMimeType();
    }

    public FileModel() {
        super();
    }

    public FileModel(JSONObject itemObject) {
        super();
        init(itemObject);
    }

    public FileModel(File file) {
        super(file.getVfsId(), file.getId(), file.getName(), file.getPath(), file.getParentId(), file.getCreationDate(),
              file.getLastModificationDate(), file.getVersionId(), file.getMimeType(), file.getLength(), file.isLocked(),
              file.getProperties(), file.getLinks());
        fixMimeType();
        this.persisted = true;
    }

    private void fixMimeType() {
        // Firefox adds ";charset=utf-8" to mime-type. Lets clear it.
        if (mimeType != null) {
            int index = mimeType.indexOf(';');
            if (index > 0)
                mimeType = mimeType.substring(0, index);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void init(JSONObject itemObject) {
        vfsId = itemObject.get("vfsId").isString().stringValue();
        id = itemObject.get("id").isString().stringValue();
        name = itemObject.get("name").isString().stringValue();
        mimeType = itemObject.get("mimeType").isString().stringValue();
        path = itemObject.get("path").isString().stringValue();
        parentId = itemObject.get("parentId").isString().stringValue();
        creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
        properties = (List)JSONDeserializer.STRING_PROPERTY_DESERIALIZER.toList(itemObject.get("properties"));
        links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
        versionId = itemObject.get("versionId").isString().stringValue();
        length = (long)itemObject.get("length").isNumber().doubleValue();
        lastModificationDate = (long)itemObject.get("lastModificationDate").isNumber().doubleValue();
        locked = itemObject.get("locked").isBoolean().booleanValue();
        permissions = JSONDeserializer.STRING_DESERIALIZER.toSet(itemObject.get("permissions"));
        this.persisted = true;
        this.contentChanged = false;
        fixMimeType();
    }

    /** @return the content */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     *         the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    public HashSet<FileModel> getVersionHistory() {
        return versionHistory;
    }

    public void setVersionHistory(HashSet<FileModel> versionHistory) {
        this.versionHistory = versionHistory;
    }

    public void clearVersionHistory() {

        this.versionHistory = new HashSet<FileModel>();
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public boolean isLocked() {
        //IDE-1329
        return locked; //lock != null;
    }

    /** @return the contentChanged */
    public boolean isContentChanged() {
        return contentChanged;
    }

    /**
     * @param contentChanged
     *         the contentChanged to set
     */
    public void setContentChanged(boolean contentChanged) {
        this.contentChanged = contentChanged;
    }

    @Override
    public ProjectModel getProject() {
        return project;
    }

    @Override
    public void setProject(ProjectModel proj) {
        this.project = proj;

    }

    @Override
    public final FolderModel getParent() {
        return parent;
    }

    @Override
    public void setParent(FolderModel parent) {
        this.parent = parent;
    }

    @Override
    public boolean isPersisted() {
        return persisted;
    }

    public boolean isVersion() {
        return versionId != null && !versionId.equals("0");
    }
}
