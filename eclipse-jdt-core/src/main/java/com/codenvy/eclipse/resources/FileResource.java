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
package com.codenvy.eclipse.resources;

import com.codenvy.commons.lang.IoUtil;
import com.codenvy.eclipse.core.internal.utils.Policy;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IFileState;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.IStatus;
import com.codenvy.eclipse.core.runtime.Status;
import com.codenvy.eclipse.core.runtime.content.IContentDescription;
import com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

/**
 * Implementation of {@link IFile}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: FileResource.java Dec 26, 2012 12:27:39 PM azatsarynnyy $
 */
public class FileResource extends ItemResource implements IFile {

    /**
     * Creates new {@link FileResource} with the specified <code>path</code> in pointed <code>workspace</code>.
     *
     * @param path
     *         {@link IPath}
     * @param workspace
     *         {@link WorkspaceResource}
     */
    protected FileResource(IPath path, WorkspaceResource workspace) {
        super(path, workspace);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IFile#appendContents(java.io.InputStream, boolean, boolean,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void appendContents(InputStream content, boolean force, boolean keepHistory,
                               IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
        appendContents(content, updateFlags, monitor);
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#appendContents(java.io.InputStream, int,
     * com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void appendContents(InputStream content, int updateFlags, IProgressMonitor monitor) throws CoreException {
        if (content == null) {
            throw new CoreException(
                    new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), "Content may not be null."));
        }

        try {
            String existingContent = IoUtil.readStream(getContents());
            String contentToAppend = IoUtil.readStream(content);
            InputStream newContentStream = new ByteArrayInputStream((existingContent + contentToAppend).getBytes());
            setContents(newContentStream, true, true, monitor);
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), e.getMessage(), e));
        }
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#create(java.io.InputStream, boolean,
     * com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {
        create(source, (force ? IResource.FORCE : IResource.NONE), monitor);
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#create(java.io.InputStream, int, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
        final ISchedulingRule rule = workspace.getRuleFactory().createRule(this);
        try {
            workspace.prepareOperation(rule, monitor);
            workspace.beginOperation(true);
            workspace.createResource(this, source);
        } finally {
            workspace.endOperation(rule, true, Policy.subMonitorFor(monitor, Policy.endOpWork));
        }
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IFile#createLink(com.codenvy.eclipse.core.runtime.IPath, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IFile#createLink(java.net.URI, int, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IFile#getCharset() */
    @Override
    public String getCharset() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#getCharset(boolean) */
    @Override
    public String getCharset(boolean checkImplicit) throws CoreException {
        return "UTF-8";
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#getCharsetFor(java.io.Reader) */
    @Override
    public String getCharsetFor(Reader reader) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#getContentDescription() */
    @Override
    public IContentDescription getContentDescription() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#getContents() */
    @Override
    public InputStream getContents() throws CoreException {
        return getContents(true);
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#getContents(boolean) */
    @Override
    public InputStream getContents(boolean force) throws CoreException {
        return workspace.getFileContents(this);
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#getEncoding() */
    @Override
    public int getEncoding() throws CoreException {
        // TODO Auto-generated method stub
        return 0;
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#getHistory(com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#setCharset(java.lang.String) */
    @Override
    public void setCharset(String newCharset) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IFile#setCharset(java.lang.String, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void setCharset(String newCharset, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.codenvy.eclipse.core.resources.IFile#setContents(java.io.InputStream, boolean, boolean,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setContents(InputStream source, boolean force, boolean keepHistory,
                            IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
        setContents(source, updateFlags, monitor);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IFile#setContents(com.codenvy.eclipse.core.resources.IFileState, boolean, boolean,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setContents(IFileState source, boolean force, boolean keepHistory,
                            IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
        setContents(source.getContents(), updateFlags, monitor);
    }

    /** @see com.codenvy.eclipse.core.resources.IFile#setContents(java.io.InputStream, int,
     * com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
        workspace.setFileContents(this, source);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IFile#setContents(com.codenvy.eclipse.core.resources.IFileState, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {
        setContents(source.getContents(), updateFlags, monitor);
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#getType() */
    @Override
    public int getType() {
        return FILE;
    }

}
