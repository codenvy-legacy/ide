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

package org.exoplatform.ide.client.operation.openbyurl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for opening file by URL.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenFileByURLPresenter implements OpenFileByURLHandler, ViewClosedHandler, ItemsSelectedHandler,
                                               InitializeServicesHandler {

    public interface Display extends IsView {

        /**
         * Get URL field.
         *
         * @return {@link TextFieldItem}
         */
        TextFieldItem getURLField();

        /**
         * Get Open button.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getOpenButton();

        /**
         * Enable or disable open button.
         *
         * @param enabled
         *         <b>true</b> or <b>false</b> to make Open button enabled or disabled.
         */
        void setOpenButtonEnabled(boolean enabled);

        /**
         * Get Cancel button.
         *
         * @return {@link HasClickHandlers}
         */
        HasClickHandlers getCancelButton();

    }

    /** Instance of {@link Display} implementation. */
    private Display display;

    /** Selected items in Workspace view. */
    private List<Item> selectedItems = new ArrayList<Item>();

    private Loader loader;

    /**
     * Creates new instance of this presenter.
     *
     * @param eventBus
     *         event handler manager.
     */
    public OpenFileByURLPresenter() {
        IDE.getInstance().addControl(new OpenFileByURLControl());

        IDE.addHandler(OpenFileByURLEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
    }

    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        loader = event.getLoader();
    }

    /** @see org.exoplatform.ide.client.remote.OpenFileByURLHandler#onOpenFileByURL(org.exoplatform.ide.client.remote.OpenFileByURLEvent) */
    @Override
    public void onOpenFileByURL(OpenFileByURLEvent event) {
        if (display != null) {
            return;
        }

        if (selectedItems.size() != 1) {
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();
    }

    /** Binds display. */
    private void bindDisplay() {
        display.getOpenButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doOpenFile();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getURLField().addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    try {
                        doOpenFile();
                    } catch (Exception e) {
                    }
                }
            }
        });

        display.getURLField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String url = display.getURLField().getValue().trim();
                if (url == null || url.trim().isEmpty() || !url.startsWith("http://")) {
                    display.setOpenButtonEnabled(false);
                } else {
                    display.setOpenButtonEnabled(true);
                }
            }
        });

        display.setOpenButtonEnabled(false);
    }

    /** Opens file after pressing "Open" button. */
    private void doOpenFile() {
        final String url = display.getURLField().getValue().trim();
        if (url == null || url.isEmpty() || !url.startsWith("http://")) {
            display.setOpenButtonEnabled(false);
            return;
        }

        String fileName = url;
        fileName = URL.decode(fileName);
        if (fileName.endsWith("/")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }

        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

        final FileModel file = new FileModel();
        file.setName(fileName);
        // file.setId(fileName);

        AsyncRequestCallback<FileModel> callback = new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(file)) {
            @Override
            protected void onSuccess(FileModel result) {
                IDE.fireEvent(new EditorOpenFileEvent(result));
                IDE.getInstance().closeView(display.asView().getId());
            }

            @Override
            protected void onFailure(Throwable exception) {
                if (exception instanceof ServerException) {
                    String message = IDE.IDE_LOCALIZATION_MESSAGES.openFileByURLErrorMessage(file.getName());
                    Dialogs.getInstance().showError(message);
                } else {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            }
        };

        try {
            AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
    }

}
