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
package com.codenvy.ide.ext.java.server.internal.core.search.indexing;

import com.codenvy.ide.ext.java.server.internal.core.JavaProject;
import com.codenvy.ide.ext.java.server.internal.core.search.processing.JobManager;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.compiler.SourceElementParser;
import org.eclipse.jdt.internal.core.index.Index;
import org.eclipse.jdt.internal.core.search.indexing.ReadWriteMonitor;
import org.eclipse.jdt.internal.core.util.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

class AddFolderToIndex extends IndexRequest {
	IPath folderPath;
    JavaProject project;
	char[][] inclusionPatterns;
	char[][] exclusionPatterns;

	public AddFolderToIndex(IPath folderPath, JavaProject project, char[][] inclusionPatterns, char[][] exclusionPatterns, IndexManager manager) {
		super(project.getFullPath(), manager);
		this.folderPath = folderPath;
		this.project = project;
		this.inclusionPatterns = inclusionPatterns;
		this.exclusionPatterns = exclusionPatterns;
	}
	public boolean execute(IProgressMonitor progressMonitor) {

		if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) return true;
//		if (!this.project.isAccessible()) return true; // nothing to do
//		IResource folder = this.project.getParent().findMember(this.folderPath);

        File folder = new File(folderPath.toOSString());
        if(! folder.exists()) return true; // nothing to do, source folder was removed

		/* ensure no concurrent write access to index */
		Index index = this.manager.getIndex(this.containerPath, true, /*reuse index file*/ true /*create if none*/);
		if (index == null) return true;
		ReadWriteMonitor monitor = index.monitor;
		if (monitor == null) return true; // index got deleted since acquired

		try {
			monitor.enterRead(); // ask permission to read

			final IPath container = this.containerPath;
			final IndexManager indexManager = this.manager;
			final SourceElementParser
                    parser = indexManager.getSourceElementParser(this.project, null/*requestor will be set by indexer*/);
            Path path = FileSystems.getDefault().getPath(folderPath.toOSString());
			if (this.exclusionPatterns == null && this.inclusionPatterns == null) {

                    Files.walkFileTree(path, new FileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            return null;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					    if (Util.isJavaLikeFileName(file.toFile().getName()))
                            indexManager.addSource(file, container, parser);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                            return null;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            return null;
                        }
                    });

//                folder.accept(
//					new IResourceProxyVisitor() {
//						public boolean visit(IResourceProxy proxy) /* throws CoreException */{
//							if (proxy.getType() == IResource.FILE) {
//
//								return false;
//							}
//							return true;
//						}
//					},
//					IResource.NONE
//				);
			} else {
                    Files.walkFileTree(path, new FileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            if (AddFolderToIndex.this.exclusionPatterns != null &&
                                AddFolderToIndex.this.inclusionPatterns == null) {
                                // if there are inclusion patterns then we must walk the children
                                if (Util.isExcluded(new org.eclipse.core.runtime.Path(dir.toFile().getPath()),
                                                    AddFolderToIndex.this
                                                            .inclusionPatterns,
                                                    AddFolderToIndex.this.exclusionPatterns,
                                                    true))
                                    return FileVisitResult.SKIP_SUBTREE;
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (Util.isJavaLikeFileName(file.getFileName().toString())) {
//                                IResource resource = proxy.requestResource();
                                if (!Util.isExcluded(new org.eclipse.core.runtime.Path(file.toFile().getPath()),
                                                     AddFolderToIndex.this
                                                             .inclusionPatterns,
                                                     AddFolderToIndex.this.exclusionPatterns, false))
                                    indexManager.addSource(file, container, parser);
                            }
                            return null;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                            return null;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            return null;
                        }
                    });
//                folder.accept(
//					new IResourceProxyVisitor() {
//						public boolean visit(IResourceProxy proxy) /* throws CoreException */{
//							switch(proxy.getType()) {
//								case IResource.FILE :
//
//									return false;
//								case IResource.FOLDER :
//
//							}
//							return true;
//						}
//					},
//					IResource.NONE
//				);
			}
		} catch (IOException e) {
			if (JobManager.VERBOSE) {
				Util.verbose("-> failed to add " + this.folderPath + " to index because of the following exception:", System.err); //$NON-NLS-1$ //$NON-NLS-2$
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
