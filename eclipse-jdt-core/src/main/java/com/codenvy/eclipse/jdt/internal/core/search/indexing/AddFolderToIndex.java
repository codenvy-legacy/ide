/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.core.search.indexing;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IResourceProxy;
import com.codenvy.eclipse.core.resources.IResourceProxyVisitor;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.jdt.core.JavaCore;
import com.codenvy.eclipse.jdt.internal.compiler.SourceElementParser;
import com.codenvy.eclipse.jdt.internal.core.index.Index;
import com.codenvy.eclipse.jdt.internal.core.search.processing.JobManager;
import com.codenvy.eclipse.jdt.internal.core.util.Util;

import org.exoplatform.services.security.ConversationState;

class AddFolderToIndex extends IndexRequest {
    IPath    folderPath;
    IProject project;
    char[][] inclusionPatterns;
    char[][] exclusionPatterns;

    public AddFolderToIndex(IPath folderPath, IProject project, char[][] inclusionPatterns, char[][] exclusionPatterns,
                            IndexManager manager) {
        super(project.getFullPath(), manager);
        this.folderPath = folderPath;
        this.project = project;
        this.inclusionPatterns = inclusionPatterns;
        this.exclusionPatterns = exclusionPatterns;
    }

    public boolean execute(IProgressMonitor progressMonitor) {
        EnvironmentContext.setCurrent(context);
        if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) return true;
        if (!this.project.isAccessible()) return true; // nothing to do
        IResource folder = this.project.getParent().findMember(this.folderPath);
        if (folder == null || folder.getType() == IResource.FILE) return true; // nothing to do, source folder was removed

		/* ensure no concurrent write access to index */
        Index index = this.manager.getIndex(this.containerPath, true, /*reuse index file*/ true /*create if none*/);
        if (index == null) return true;
        ReadWriteMonitor monitor = index.monitor;
        if (monitor == null) return true; // index got deleted since acquired

        try {
            monitor.enterRead(); // ask permission to read

            final IPath container = this.containerPath;
            final IndexManager indexManager = this.manager;
            final SourceElementParser parser =
                    indexManager.getSourceElementParser(JavaCore.create(this.project), null/*requestor will be set by indexer*/);
            if (this.exclusionPatterns == null && this.inclusionPatterns == null) {
                folder.accept(
                        new IResourceProxyVisitor() {
                            public boolean visit(IResourceProxy proxy) /* throws CoreException */ {
                                if (proxy.getType() == IResource.FILE) {
                                    if (com.codenvy.eclipse.jdt.internal.core.util.Util.isJavaLikeFileName(proxy.getName()))
                                        indexManager.addSource((IFile)proxy.requestResource(), container, parser);
                                    return false;
                                }
                                return true;
                            }
                        },
                        IResource.NONE
                             );
            } else {
                folder.accept(
                        new IResourceProxyVisitor() {
                            public boolean visit(IResourceProxy proxy) /* throws CoreException */ {
                                switch (proxy.getType()) {
                                    case IResource.FILE:
                                        if (com.codenvy.eclipse.jdt.internal.core.util.Util.isJavaLikeFileName(proxy.getName())) {
                                            IResource resource = proxy.requestResource();
                                            if (!Util.isExcluded(resource, AddFolderToIndex.this.inclusionPatterns,
                                                                 AddFolderToIndex.this.exclusionPatterns))
                                                indexManager.addSource((IFile)resource, container, parser);
                                        }
                                        return false;
                                    case IResource.FOLDER:
                                        if (AddFolderToIndex.this.exclusionPatterns != null &&
                                            AddFolderToIndex.this.inclusionPatterns == null) {
                                            // if there are inclusion patterns then we must walk the children
                                            if (Util.isExcluded(proxy.requestFullPath(), AddFolderToIndex.this.inclusionPatterns,
                                                                AddFolderToIndex.this.exclusionPatterns, true))
                                                return false;
                                        }
                                }
                                return true;
                            }
                        },
                        IResource.NONE
                             );
            }
        } catch (CoreException e) {
            if (JobManager.VERBOSE) {
                Util.verbose("-> failed to add " + this.folderPath + " to index because of the following exception:",
                             System.err); //$NON-NLS-1$ //$NON-NLS-2$
                e.printStackTrace();
            }
            return false;
        } finally {
            monitor.exitRead(); // free read lock
        }
        return true;
    }

    public String toString() {
        return "adding " + this.folderPath + " to index " + this.containerPath; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
