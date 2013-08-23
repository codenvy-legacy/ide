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
package org.exoplatform.ide.client.operation.createfolder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFolderPresenter implements CreateFolderHandler, ItemsSelectedHandler, ViewClosedHandler {

    public interface Display extends IsView {

        HasValue<String> getFolderNameField();

        HasClickHandlers getCreateButton();

        HasClickHandlers getCancelButton();

        HasKeyPressHandlers getFolderNameFiledKeyPressed();

        void setFocusInNameField();

        void selectFolderName();
    }

    private static final String NEW_FOLDER_NAME = IDE.IDE_LOCALIZATION_CONSTANT.newFolderName();

    private Display display;

    private List<Item> selectedItems = new ArrayList<Item>();

    public CreateFolderPresenter() {
        IDE.getInstance().addControl(new CreateFolderControl());

        IDE.addHandler(CreateFolderEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getCreateButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                createFolder();
            }
        });

        display.getFolderNameField().setValue(NEW_FOLDER_NAME);

        display.getFolderNameFiledKeyPressed().addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    createFolder();
                }
            }
        });

    }

    protected void createFolder() {
        final String newFolderName = display.getFolderNameField().getValue();
        Item selectedItem = selectedItems.get(0);

        final Folder baseFolder = (selectedItem instanceof FileModel) ?
                                  ((FileModel)selectedItem).getParent() : (Folder)selectedItem;

        FolderModel newFolder = new FolderModel();
        newFolder.setName(newFolderName);
        try {
            VirtualFileSystem.getInstance().createFolder(baseFolder,
                                                         new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(newFolder)) {
                                                             @Override
                                                             protected void onSuccess(FolderModel result) {
                                                                 IDE.getInstance().closeView(display.asView().getId());
                                                                 IDE.fireEvent(new RefreshBrowserEvent(baseFolder, result));
                                                             }

                                                             @Override
                                                             protected void onFailure(Throwable exception) {
                                                                 IDE.fireEvent(new ExceptionThrownEvent(exception,
                                                                                                        "Service is not deployed" +
                                                                                                        ".<br>Resource already exist" +
                                                                                                        ".<br>Parent folder not found."));
                                                             }
                                                         });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e,
                                                   "Service is not deployed.<br>Resource already exist.<br>Parent folder not found."));
        }
    }

    @Override
    public void onCreateFolder(CreateFolderEvent event) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            IDE.fireEvent(new ExceptionThrownEvent(IDE.ERRORS_CONSTANT.createFolderSelectParentFolder()));
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                display.setFocusInNameField();
                display.selectFolderName();
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}
