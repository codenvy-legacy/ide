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
package com.codenvy.ide.ext.java.server.internal.core.search;

import com.codenvy.ide.ext.java.server.core.search.IJavaSearchScope;
import com.codenvy.ide.ext.java.server.core.search.SearchDocument;
import com.codenvy.ide.ext.java.server.core.search.SearchParticipant;
import com.codenvy.ide.ext.java.server.internal.core.search.processing.JobManager;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.io.File;

public class JavaSearchDocument extends SearchDocument {

    private   File  file;
    protected byte[] byteContents;
    protected char[] charContents;

    public JavaSearchDocument(String documentPath, SearchParticipant participant) {
        super(documentPath, participant);
    }

    public JavaSearchDocument(java.util.zip.ZipEntry zipEntry, IPath zipFilePath, byte[] contents, SearchParticipant participant) {
        super(zipFilePath + IJavaSearchScope.JAR_FILE_ENTRY_SEPARATOR + zipEntry.getName(), participant);
        this.byteContents = contents;
    }

    public byte[] getByteContents() {
        if (this.byteContents != null) return this.byteContents;
        try {
            return Util.getResourceContentsAsByteArray(getFile());
        } catch (Exception e) {
            if (org.eclipse.jdt.internal.core.search.BasicSearchEngine.VERBOSE ||
                JobManager.VERBOSE) { // used during search and during indexing
                e.printStackTrace();
            }
            return null;
        }
    }

    public char[] getCharContents() {
        if (this.charContents != null) return this.charContents;
        try {
            return Util.getResourceContentsAsCharArray(getFile());
        } catch (Exception e) {
            if (BasicSearchEngine.VERBOSE || JobManager.VERBOSE) { // used during search and during indexing
                e.printStackTrace();
            }
            return null;
        }
    }

    public String getEncoding() {
        // Return the encoding of the associated file
        File resource = getFile();
        if (resource != null) {
            return "UTF-8"; //resource.getCharset();
        }
		return null;
	}
	private File getFile() {
		if (this.file == null)
			this.file = new Path(getPath()).toFile();
		return this.file;
	}
	public String toString() {
		return "SearchDocument for " + getPath(); //$NON-NLS-1$
	}
}
