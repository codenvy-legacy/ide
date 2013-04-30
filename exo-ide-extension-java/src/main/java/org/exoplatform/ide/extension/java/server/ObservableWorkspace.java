/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
