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
package org.exoplatform.ide.extension.java.server.parser;

import com.thoughtworks.qdox.model.ClassLibrary;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Nov 28, 2011 3:08:29 PM evgen $
 */
public class VfsClassLibrary extends ClassLibrary {

    private VirtualFileSystem vfs;

    private List<Folder> sourceFolders = new ArrayList<Folder>();

    /** Logger. */
    private static final Log LOG = ExoLogger.getLogger(VfsClassLibrary.class);

    /** @param vfs */
    public VfsClassLibrary(VirtualFileSystem vfs) {
        this.vfs = vfs;
    }

    /**
     *
     */
    private static final long serialVersionUID = -4177400811232878566L;

    public void addSourceFolder(Folder folder) {
        sourceFolders.add(folder);
    }

    public InputStream getSourceFileContent(String className) {
        String mainClassName = className.split("\\$")[0];
        String path = mainClassName.replace('.', '/') + ".java";
        for (Folder f : sourceFolders) {

            try {
                Item i = vfs.getItemByPath(f + "/" + path, null, false, PropertyFilter.NONE_FILTER);
                if (i instanceof File) {
                    return vfs.getContent(i.getId()).getStream();
                }
            } catch (ItemNotFoundException e) {
                if (LOG.isDebugEnabled())
                    LOG.debug(e);
            } catch (PermissionDeniedException e) {
                if (LOG.isWarnEnabled())
                    LOG.warn(e);
            } catch (VirtualFileSystemException e) {
                if (LOG.isWarnEnabled())
                    LOG.warn(e);
            }

        }
        return null;
    }

}
