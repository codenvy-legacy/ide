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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.server.VirtualFileFilter;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import com.codenvy.api.vfs.server.search.LuceneSearcher;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SingleInstanceLockFactory;

import java.io.IOException;

/**
 * Implementation of LuceneSearcher which stores index in the filesystem.
 *
 * @author andrew00x
 */
public class FSIndexSearcher extends LuceneSearcher {
    protected final java.io.File indexDir;

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
