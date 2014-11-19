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
package com.codenvy.ide.copy;

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.vfs.gwt.client.VfsServiceClient;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.event.NodeChangedEvent;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.generic.*;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.navigation.NavigateToFileView;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import java.util.Iterator;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;
import static com.google.gwt.http.client.RequestBuilder.GET;

/**
 * @author Ann Shumilova
 */
public class CopyItemPresenter implements CopyItemView.ActionDelegate {

    String parentPath;
    private CopyItemView view;
    private SelectionAgent selectionAgent;
    private CoreLocalizationConstant locale;
    private AppContext appContext;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private DtoFactory dtoFactory;
    private ProjectServiceClient projectServiceClient;
    private EventBus eventBus;
    private StringMap<ItemReference> resultMap;
    private Array<StorableNode> itemsToCopy;
    private String pathForRetrieving;
    private Array<ItemReference> directoryContent = Collections.<ItemReference>createArray();

    @Inject
    public CopyItemPresenter(CopyItemView view, SelectionAgent selectionAgent, CoreLocalizationConstant locale, AppContext appContext, EventBus eventBus,

                             DtoUnmarshallerFactory dtoUnmarshallerFactory, DtoFactory dtoFactory,
                             ProjectServiceClient projectServiceClient) {
        this.selectionAgent = selectionAgent;
        this.locale = locale;
        this.appContext = appContext;
        this.view = view;
        this.view.setDelegate(this);
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
    }

    public void doCopy() {
        itemsToCopy = Collections.createArray();
        view.showView();
        boolean isSingleSelection = selectionAgent.getSelection().isSingleSelection();
        view.setNewNameValue("");
        view.setNewNameVisible(isSingleSelection);
        view.setErrorMessage("");
        if (isSingleSelection && selectionAgent.getSelection().getFirstElement() instanceof StorableNode) {
            itemsToCopy.add((ItemNode) selectionAgent.getSelection().getFirstElement());
            view.setCopyItemTitle(locale.copyItemViewItemTitle(((StorableNode) selectionAgent.getSelection().getFirstElement()).getPath()));
            new Timer() {
                @Override
                public void run() {
                    view.setNewNameValue(((StorableNode) selectionAgent.getSelection().getFirstElement()).getName());
                }
            }.schedule(300);

        } else {
            view.setCopyItemTitle(locale.copyItemViewItemsTitle());
            for (Object item : selectionAgent.getSelection().getAll().asIterable()) {
                if (item instanceof StorableNode) {
                    itemsToCopy.add((ItemNode) item);
                }
            }
        }

        String path = itemsToCopy.asIterable().iterator().next().getPath();
        //Set parents path:
        parentPath = path.lastIndexOf("/") > 0 ? path.substring(0, path.lastIndexOf("/")) : path;
        view.setDirectory(parentPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestSuggestions(String query, final AsyncCallback<Array<ItemReference>> callback) {
        resultMap = Collections.createStringMap();
        final String part = query.substring(query.lastIndexOf("/") + 1);
        String path = query.lastIndexOf("/") > 0 ? query.substring(0, query.lastIndexOf("/")) : query;

        getChildren(path, new AsyncCallback<Array<ItemReference>>() {
            @Override
            public void onSuccess(Array<ItemReference> result) {
                for (ItemReference item : result.asIterable()) {
                    // skip hidden items and files
                    if (!"file".equals(item.getType()) && !item.getPath().contains("/.") && item.getName().contains(part)) {
                        resultMap.put(item.getPath(), item);
                    }
                }
                callback.onSuccess(resultMap.getValues());
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    @Override
    public void onValueChanged() {
        view.setCopyButtonEnabled(view.getDirectory() != null && !view.getDirectory().isEmpty());
    }

    /**
     * Get children of the item with pointed path.
     *
     * @param path path to retrieve children
     * @param callback callback
     */
    private void getChildren(String path, final AsyncCallback<Array<ItemReference>> callback) {
        if (path.equals(pathForRetrieving)) {
            callback.onSuccess(directoryContent);
            return;
        }
        pathForRetrieving = path;

        projectServiceClient.getChildren(path, new AsyncRequestCallback<Array<ItemReference>>(dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class)) {
            @Override
            protected void onSuccess(Array<ItemReference> itemReferenceArray) {
                directoryContent = itemReferenceArray;
                callback.onSuccess(directoryContent);
            }

            @Override
            protected void onFailure(Throwable throwable) {
                directoryContent = Collections.<ItemReference>createArray();
                callback.onSuccess(directoryContent);
            }
        });
    }

    @Override
    public void onOkClicked() {
        copyNextItem();
    }

    /** Copies next item from the list of items to copy if it's not empty. */
    private void copyNextItem() {
        if (itemsToCopy.size() > 0) {
            copy(itemsToCopy.get(0), view.getOpenInEditor());
        } else {
            completeCopyOperation();
        }
    }

    /** Performs copy of pointed item.
     *
     * @param item item to copy
     * @param isOpenInEditor open in editor
     */
    public void copy(StorableNode item, boolean isOpenInEditor) {
        projectServiceClient.copy(item.getPath(), view.getDirectory(), view.getNewNameValue(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void aVoid) {
                //TODO Open in editor
                itemsToCopy.remove(0);
                copyNextItem();
            }

            @Override
            protected void onFailure(Throwable throwable) {
                if (throwable instanceof ServerException) {
                    ServiceError e = dtoFactory.createDtoFromJson(throwable.getMessage(), ServiceError.class);
                    view.setErrorMessage(e.getMessage());
                } else {
                    view.setErrorMessage(throwable.getMessage());
                }
            }
        });
    }

    /** Refresh the Project Explorer tree after copy operation is finished. */
    private void completeCopyOperation() {
        appContext.getCurrentProject().getCurrentTree().getNodeByPath(view.getDirectory(), new AsyncCallback<TreeNode<?>>() {
            @Override
            public void onSuccess(TreeNode<?> result) {
                if (result instanceof FolderNode) {
                    eventBus.fireEvent(NodeChangedEvent.createNodeChildrenChangedEvent((FolderNode) result));
                } else if (result instanceof ProjectNode) {
                    eventBus.fireEvent(NodeChangedEvent.createNodeChildrenChangedEvent((ProjectNode) result));
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                //TODO
            }
        });
        view.close();
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }
}
