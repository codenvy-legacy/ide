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
package org.exoplatform.ide.extension.java.server;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.resources.WorkspaceResource;

import org.exoplatform.ide.extension.java.shared.Action;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class ObservableWorkspace extends WorkspaceResource {
 
    private List<Action> actions = new ArrayList<Action>();
    
    public ObservableWorkspace(VirtualFileSystem vfs) throws VirtualFileSystemException {
        super(vfs);        
    }
    
    public List<Action> getActions() {
        return actions;
    }

    @Override
    public void moveResource(IResource resource, IPath destination) throws CoreException {
        super.moveResource(resource, destination);
        actions.add(new Action(Action.MOVE, resource.getFullPath().toString(), destination.toString()));
    }

    @Override
    public void setFileContents(IFile file, InputStream newContent) throws CoreException {
        super.setFileContents(file, newContent);
        actions.add(new Action(Action.UPDATE_CONTENT, file.getFullPath().toString()));
    }

}
