/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.navigation;

import org.eclipse.che.api.project.shared.dto.ItemReference;

import org.eclipse.che.ide.CoreLocalizationConstant;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.websocket.Message;
import org.eclipse.che.ide.websocket.MessageBuilder;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.WebSocketException;
import org.eclipse.che.ide.websocket.rest.RequestCallback;
import org.eclipse.che.ide.websocket.rest.Unmarshallable;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static org.eclipse.che.ide.MimeType.APPLICATION_JSON;
import static org.eclipse.che.ide.rest.HTTPHeader.ACCEPT;
import static com.google.gwt.http.client.RequestBuilder.GET;

/**
 * Presenter for file navigation (find file by name and open it).
 *
 * @author Ann Shumilova
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NavigateToFilePresenter implements NavigateToFileView.ActionDelegate {

    private final String                   SEARCH_URL;
    private       MessageBus               wsMessageBus;
    private       DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private       DialogFactory            dialogFactory;
    private       CoreLocalizationConstant localizationConstant;
    private       NavigateToFileView       view;
    private       AppContext               appContext;
    private       EventBus                 eventBus;
    private       StringMap<ItemReference> resultMap;

    @Inject
    public NavigateToFilePresenter(NavigateToFileView view,
                                   AppContext appContext,
                                   EventBus eventBus,
                                   MessageBus wsMessageBus,
                                   @Named("workspaceId") String workspaceId,
                                   DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                   DialogFactory dialogFactory,
                                   CoreLocalizationConstant localizationConstant) {
        this.view = view;
        this.appContext = appContext;
        this.eventBus = eventBus;
        this.wsMessageBus = wsMessageBus;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dialogFactory = dialogFactory;
        this.localizationConstant = localizationConstant;

        resultMap = Collections.createStringMap();

        SEARCH_URL = "/project/" + workspaceId + "/search";
        view.setDelegate(this);
    }

    /** Show dialog with view for navigation. */
    public void showDialog() {
        view.showDialog();
        view.clearInput();
    }

    /** {@inheritDoc} */
    @Override
    public void onRequestSuggestions(String query, final AsyncCallback<Array<ItemReference>> callback) {
        resultMap = Collections.createStringMap();

        // add '*' to allow search files by first letters
        search(query + "*", new AsyncCallback<Array<ItemReference>>() {
            @Override
            public void onSuccess(Array<ItemReference> result) {
                for (ItemReference item : result.asIterable()) {
                    String path = item.getPath();
                    // skip hidden items and items that don't belong to the project
                    if (!isItemHidden(path) && isItemBelongingToProject(path)) {
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

    /** {@inheritDoc} */
    @Override
    public void onFileSelected() {
        view.close();
        ItemReference selectedItem = resultMap.get(view.getItemPath());
        appContext.getCurrentProject().getCurrentTree().getNodeByPath(selectedItem.getPath(), new AsyncCallback<TreeNode<?>>() {
            @Override
            public void onSuccess(TreeNode<?> result) {
                if (result instanceof FileNode) {
                    eventBus.fireEvent(new FileEvent((FileNode)result, FileEvent.FileOperation.OPEN));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                dialogFactory.createMessageDialog("", localizationConstant.navigateToFileCanNotOpenFile(), null).show();
            }
        });
    }

    private void search(String fileName, final AsyncCallback<Array<ItemReference>> callback) {
        final String projectPath = appContext.getCurrentProject().getRootProject().getPath();
        final String url = SEARCH_URL + projectPath + "?name=" + URL.encodePathSegment(fileName);
        Message message = new MessageBuilder(GET, url).header(ACCEPT, APPLICATION_JSON).build();
        Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newWSArrayUnmarshaller(ItemReference.class);
        try {
            wsMessageBus.send(message, new RequestCallback<Array<ItemReference>>(unmarshaller) {
                @Override
                protected void onSuccess(Array<ItemReference> result) {
                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (WebSocketException e) {
            callback.onFailure(e);
        }
    }

    private boolean isItemBelongingToProject(String path) {
        path = path.startsWith("/") ? path.substring(1) : path;
        String[] items = path.split("/");
        String projectName = appContext.getCurrentProject().getProjectDescription().getName();
        return items[0].equals(projectName);
    }

    private boolean isItemHidden(String path) {
        return path.contains("/.");
    }
}
