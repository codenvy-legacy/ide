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
package org.eclipse.jdt.client;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemDeletedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:19:16 PM 34360 evgen $
 */
public class TypeInfoUpdater implements FileSavedHandler, ItemDeletedHandler {

    /** Default Maven 'sourceDirectory' value */
    private static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

    /**
     *
     */
    public TypeInfoUpdater() {
        IDE.addHandler(FileSavedEvent.TYPE, this);
        IDE.addHandler(ItemDeletedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event
     * .FileSavedEvent) */
    @Override
    public void onFileSaved(FileSavedEvent event) {
        if (event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA)) {
            deleteTypeFormStorage(event.getFile(), true);
        }
    }

    /**
     * Delete type info from storage and receive new if needed.
     *
     * @param file
     *         The Java file that edited or deleted.
     * @param isNeedUpdate
     *         is need to receive new type info for file
     */
    private void deleteTypeFormStorage(FileModel file, boolean isNeedUpdate) {
        ProjectModel project = file.getProject();
        if (project == null)
            return;
        String srcPath;
        if (project.hasProperty("sourceFolder")) {
            srcPath = (String)project.getPropertyValue("sourceFolder");
        } else {
            srcPath = DEFAULT_SOURCE_FOLDER;
        }
        String fqn = file.getPath().substring((project.getPath() + "/" + srcPath).length() + 1);
        fqn = fqn.substring(0, fqn.lastIndexOf('.'));
        fqn = fqn.replaceAll("/", ".");
        TypeInfoStorage.get().removeTypeInfo(fqn);
        if (isNeedUpdate)
            NameEnvironment.loadTypeInfo(fqn, project.getId());
    }

    /** @see org.exoplatform.ide.vfs.client.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.vfs.client.event.ItemDeletedEvent) */
    @Override
    public void onItemDeleted(ItemDeletedEvent event) {
        if (event.getItem().getMimeType().equals(MimeType.APPLICATION_JAVA)) {
            deleteTypeFormStorage((FileModel)event.getItem(), false);
        }
    }

}
