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

import com.codenvy.eclipse.core.internal.watson.IPathRequestor;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.Path;

import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class VfsTreeIterator implements IPathRequestor {


    //for path requestor
    private String[] segments = new String[10];

    private int nextFreeSegment;

    WorkspaceResource ws;

    private IPath path;

    private Folder root;

    public VfsTreeIterator(WorkspaceResource ws, IPath path) {
        this.ws = ws;
        this.path = path;
        try {
            root = ws.getVFS().getInfo().getRoot();
        } catch (VirtualFileSystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    /**
     * Iterates through the given element tree and visit each element (node)
     * passing in the element's ID and element object.
     */
    private void doIteration(IResource node, IElementContentVisitor visitor) {
        //push the name of this node to the requestor stack
        if (nextFreeSegment >= segments.length) {
            grow();
        }
        segments[nextFreeSegment++] = node.getName();

        //do the visit
        if (visitor.visitElement(this, ws.newElement(node.getType()))) {
            //recurse
            try {
                if (node.getType() != IResource.FILE) {
                    IResource[] children = ws.getMembers(node.getFullPath(), 0);
                    for (int i = children.length; --i >= 0; ) {
                        doIteration(children[i], visitor);
                    }
                }
            } catch (CoreException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        //pop the segment from the requestor stack
        nextFreeSegment--;
        if (nextFreeSegment < 0) {
            nextFreeSegment = 0;
        }
    }

    /**
     * Iterates through this iterator's tree and visits each element in the
     * subtree rooted at the given path.  The visitor is passed each element's
     * data and a request callback for obtaining the path.
     */
    public void iterate(IElementContentVisitor visitor) {
        if (path.isRoot()) {


            if (visitor.visitElement(this, ws.newElement(getItemType(root)))) {
                try {
                    IResource[] children = ws.getMembers(path, 0);
                    for (int i = children.length; --i >= 0; ) {
                        doIteration(children[i], visitor);
                    }
                } catch (CoreException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        } else {
            push(path, path.segmentCount() - 1);
            try {
                Item item = ws.getVfsItemByFullPath(path);
                ItemResource itemResource = ws.newResource(path, getItemType(item));
                doIteration(itemResource, visitor);
            } catch (CoreException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ItemNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private int getItemType(Item item) {
        if (item.equals(root)) {
            return IResource.ROOT;
        }
        if (item instanceof File) {
            return IResource.FILE;
        }
        if (item instanceof Folder) {
            return IResource.FOLDER;
        }
        if (item instanceof Project) {
            return IResource.PROJECT;
        }
        return -1;
    }


    /** Method grow. */
    private void grow() {
        //grow the segments array
        int oldLen = segments.length;
        String[] newPaths = new String[oldLen * 2];
        System.arraycopy(segments, 0, newPaths, 0, oldLen);
        segments = newPaths;
    }

    /** Push the first "toPush" segments of this path. */
    private void push(IPath pathToPush, int toPush) {
        if (toPush <= 0) {
            return;
        }
        for (int i = 0; i < toPush; i++) {
            if (nextFreeSegment >= segments.length) {
                grow();
            }
            segments[nextFreeSegment++] = pathToPush.segment(i);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String requestName() {
        if (nextFreeSegment == 0) {
            return ""; //$NON-NLS-1$
        }
        return segments[nextFreeSegment - 1];
    }

    /** {@inheritDoc} */
    @Override
    public IPath requestPath() {
        if (nextFreeSegment == 0) {
            return Path.ROOT;
        }
        int length = nextFreeSegment;
        for (int i = 0; i < nextFreeSegment; i++) {
            length += segments[i].length();
        }
        StringBuffer pathBuf = new StringBuffer(length);
        for (int i = 0; i < nextFreeSegment; i++) {
            pathBuf.append('/');
            pathBuf.append(segments[i]);
        }
        return new Path(null, pathBuf.toString());
    }
}
