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
package org.exoplatform.ide.vfs.server.observation;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ProjectUpdateListener implements EventListener {
    private final String projectId;

    public ProjectUpdateListener(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public void handleEvent(ChangeEvent event) throws VirtualFileSystemException {
        List<Property> properties = new ArrayList<Property>(1);
        properties.add(new PropertyImpl("vfs:lastUpdateTime", Long.toString(System.currentTimeMillis())));
        event.getVirtualFileSystem().updateItem(projectId, properties, null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectUpdateListener)) {
            return false;
        }
        ProjectUpdateListener other = (ProjectUpdateListener)o;
        return projectId.equals(other.projectId);
    }

    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 31 * hash + projectId.hashCode();
        return hash;
    }
}
