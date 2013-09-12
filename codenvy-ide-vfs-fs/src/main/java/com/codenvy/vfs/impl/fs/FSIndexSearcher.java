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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.server.VirtualFileFilter;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import com.codenvy.api.vfs.server.search.LuceneSearcher;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SingleInstanceLockFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Implementation of LuceneSearcher which stores index in the filesystem.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class FSIndexSearcher extends LuceneSearcher {
    protected final java.io.File indexDir;

    public FSIndexSearcher(java.io.File indexDir, Set<String> indexedMediaTypes) {
        super(indexedMediaTypes);
        this.indexDir = indexDir;
    }

    public FSIndexSearcher(java.io.File indexDir, VirtualFileFilter filter) {
        super(filter);
        this.indexDir = indexDir;
    }

    @Override
    protected Directory makeDirectory() {
        try {
            return FSDirectory.open(indexDir, new SingleInstanceLockFactory());
        } catch (IOException e) {
            throw new VirtualFileSystemRuntimeException(e);
        }
    }

    public java.io.File getIndexDir() {
        return indexDir;
    }
}
