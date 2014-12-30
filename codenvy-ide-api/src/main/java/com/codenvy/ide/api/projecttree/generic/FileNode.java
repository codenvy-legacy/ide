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
package com.codenvy.ide.api.projecttree.generic;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.VirtualFile;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A node that represents a file.
 *
 * @author Artem Zatsarynnyy
 */
public class FileNode extends ItemNode implements VirtualFile {

    @AssistedInject
    public FileNode(@Assisted TreeNode<?> parent, @Assisted ItemReference data, EventBus eventBus,
                    ProjectServiceClient projectServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(parent, data, eventBus, projectServiceClient, dtoUnmarshallerFactory);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLeaf() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void processNodeAction() {
        eventBus.fireEvent(new FileEvent(this, FileEvent.FileOperation.OPEN));
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final DeleteCallback callback) {
        super.delete(new DeleteCallback() {
            @Override
            public void onDeleted() {
                eventBus.fireEvent(new FileEvent(FileNode.this, FileEvent.FileOperation.CLOSE));
                callback.onDeleted();
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    @Nullable
    @Override
    public String getMediaType() {
        return data.getMediaType();
    }

    @Override
    public boolean isReadOnly() {
        //todo add permissions check here
        return false;
    }

    @Override
    public String getContentUrl() {
        List<Link> links = data.getLinks();
        Link li = null;
        for (Link link : links) {
            if (link.getRel().equals("get content")) {
                li = link;
                break;
            }
        }
        return li == null ? null : li.getHref();
    }

    /**
     * Get content of the file which this node represents.
     *
     * @param callback
     *         callback to return retrieved content
     */
    public void getContent(final AsyncCallback<String> callback) {
        projectServiceClient.getFileContent(data.getPath(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /**
     * Update content of the file which this node represents.
     *
     * @param content
     *         new content of the file
     * @param callback
     *         callback to return retrieved content
     */
    public void updateContent(String content, final AsyncCallback<Void> callback) {
        projectServiceClient.updateFile(data.getPath(), content, null, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileNode)) {
            return false;
        }
        final FileNode other = (FileNode)o;
        return data.equals(other.data);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        return result;
    }
}
