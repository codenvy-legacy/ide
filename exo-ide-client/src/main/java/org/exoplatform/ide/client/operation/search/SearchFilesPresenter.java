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
package org.exoplatform.ide.client.operation.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.event.SearchResultReceivedEvent;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: $
 */
public class SearchFilesPresenter implements SearchFilesHandler, ViewOpenedHandler, ViewClosedHandler,
                                 ItemsSelectedHandler {

    public interface Display extends IsView {

        HasClickHandlers getSearchButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getSearchContentItem();

        HasValue<String> getPathItem();

        HasValue<String> getMimeTypeItem();

        void setMimeTypeValues(String[] mimeTypes);

    }

    private static final String SEARCH_ERROR_MESSAGE = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
                                                                                                     .searchFileSearchError();

    private Display display;

    private List<Item> selectedItems = new ArrayList<Item>();

    public SearchFilesPresenter() {
        IDE.getInstance().addControl(new SearchFilesControl(), Docking.TOOLBAR);

        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(SearchFilesEvent.TYPE, this);
        IDE.addHandler(ViewOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getSearchButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doSearch();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        setPath();
        fillMimeTypes();
    }

    private void setPath() {
        if (selectedItems.size() > 0) {
            Item selectedItem = selectedItems.get(0);

            String path = selectedItem.getPath();
            if (selectedItem instanceof FileModel && ((FileModel)selectedItem).getParent() != null) {
                path = ((FileModel)selectedItem).getParent().getPath();
            }
            display.getPathItem().setValue(path);
        }
    }

    private void doSearch() {
        // TODO
        HashMap<String, String> query = new HashMap<String, String>();
        String content = display.getSearchContentItem().getValue();
        String contentType = display.getMimeTypeItem().getValue();
        query.put("text", URL.encode(content));
        query.put("mediaType", contentType);

        Item item = selectedItems.get(0);

        String path = item.getPath();
        if (item instanceof File) {
            path = path.substring(0, path.lastIndexOf("/"));
        }

        if (!"".equals(path) && !path.startsWith("/")) {
            path = "/" + path;
        }

        query.put("path", path);
        final FolderModel folder = new FolderModel();
        folder.setId(path);
        folder.setPath(path);
        if (item instanceof ProjectModel)
        {
            folder.setProject((ProjectModel)item);
        } else
        {
            folder.setProject(((ItemContext)item).getProject());
        }
        try {
            VirtualFileSystem.getInstance().search(
                                                   query,
                                                   -1,
                                                   0,
                                                   new AsyncRequestCallback<List<Item>>(
                                                                                        new ChildrenUnmarshaller(new ArrayList<Item>())) {

                                                       @Override
                                                       protected void onSuccess(List<Item> result) {
                                                           folder.getChildren().setItems(result);
                                                           IDE.fireEvent(new SearchResultReceivedEvent(folder));
                                                           IDE.getInstance().closeView(display.asView().getId());
                                                       }

                                                       @Override
                                                       protected void onFailure(Throwable exception) {
                                                           IDE.fireEvent(new ExceptionThrownEvent(exception, SEARCH_ERROR_MESSAGE));
                                                       }
                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, SEARCH_ERROR_MESSAGE));
        }
    }

    private void fillMimeTypes() {
        String[] mimeTypes = new String[12];
        mimeTypes[0] = MimeType.TEXT_HTML;
        mimeTypes[1] = MimeType.TEXT_CSS;
        mimeTypes[2] = MimeType.TEXT_PLAIN;
        mimeTypes[3] = MimeType.APPLICATION_X_JAVASCRIPT;
        mimeTypes[4] = MimeType.APPLICATION_JAVASCRIPT;
        mimeTypes[5] = MimeType.TEXT_JAVASCRIPT;
        mimeTypes[6] = MimeType.TEXT_XML;
        mimeTypes[7] = MimeType.APPLICATION_JAVA;
        mimeTypes[8] = MimeType.APPLICATION_JSP;
        mimeTypes[9] = MimeType.APPLICATION_PHP;
        mimeTypes[10] = MimeType.APPLICATION_RUBY;
        mimeTypes[11] = MimeType.TEXT_X_PYTHON;
        display.setMimeTypeValues(mimeTypes);
    }

    public void onSearchFiles(SearchFilesEvent event) {
        if (display != null) {
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    @Override
    public void onViewOpened(ViewOpenedEvent event) {
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
        if (display != null) {
            setPath();
        }
    }

}
