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
package com.codenvy.ide.ext.java.server.parser;

import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.dto.Folder;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.ide.ext.java.server.parser.scanner.FileSuffixFilter;
import com.codenvy.ide.ext.java.server.parser.scanner.FolderScanner;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Nov 28, 2011 3:07:14 PM evgen $
 */
public class JavaDocBuilderVfs extends JavaDocBuilder {
    /**
     *
     */
    private static final long serialVersionUID = 2801488236934185900L;

    private VirtualFileSystem vfs;

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(JavaDocBuilderVfs.class);

    /** @param vfs */
    public JavaDocBuilderVfs(VirtualFileSystem vfs, VfsClassLibrary library) {
        super(library);
        this.vfs = vfs;
    }

    @Override
    protected JavaClass createSourceClass(String name) {
        InputStream sourceFile = ((VfsClassLibrary)getClassLibrary()).getSourceFileContent(name);
        if (sourceFile != null) {
            try (Reader reader = new BufferedReader(new InputStreamReader(sourceFile))) {
                JavaSource source = addSource(reader, name);
                for (int index = 0; index < source.getClasses().length; index++) {
                    JavaClass clazz = source.getClasses()[index];
                    if (name.equals(clazz.getFullyQualifiedName())) {
                        return clazz;
                    }
                }
                return source.getNestedClassByName(name);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return null;
    }

    public void addSourceTree(Folder folder) throws VirtualFileSystemException {
        FolderScanner scanner = new FolderScanner(folder, vfs);
        scanner.addFilter(new FileSuffixFilter(".java"));
        List<Item> list = scanner.scan();
        for (Item i : list) {
            try (Reader reader = new BufferedReader(new InputStreamReader(vfs.getContent(i.getId()).getStream()))) {
                addSource(reader, i.getId());
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
    }

    /** @see com.thoughtworks.qdox.JavaDocBuilder#getClassByName(java.lang.String) */
    @Override
    public JavaClass getClassByName(String name) {
        for (JavaClass clazz : getClasses()) {
            if (clazz.getFullyQualifiedName().equals(name)) {
                return clazz;
            }
        }
        return null;
    }
}
