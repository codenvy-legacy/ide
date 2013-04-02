/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.operation.openbypath;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.navigation.event.GoToItemEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class OpenFileByPathPresenter implements ViewClosedHandler, OpenFileByPathHandler,
                                                EditorActiveFileChangedHandler {

    public interface Display extends IsView {
        HasClickHandlers getOpenButton();

        HasClickHandlers getCancelButton();

        void enableOpenButton();

        void disableOpenButton();

        HasKeyPressHandlers getFilePathField();

        void selectPathField();

        void focusInPathField();

        TextFieldItem getFilePathFieldOrigin();
    }

    private Display display;

    /** Active file, opened in tab. */
    private FileModel activeFile;

    /** Is need go to folder on active file changed (when file opened in new tab by {@link OpenFileByPathControl}). */
    private boolean isNeedGoToFolderOnActiveFileChanged = false;

    public OpenFileByPathPresenter() {
        IDE.getInstance().addControl(new OpenFileByPathControl());

        IDE.addHandler(OpenFileByPathEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    void bindDisplay(Display d) {
        display = d;

        display.getOpenButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                openFile();
            }

        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getFilePathField().addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                if (event.getCharCode() == KeyCodes.KEY_ENTER) {
                    openFile();
                }
            }

        });

        display.getFilePathFieldOrigin().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                updateOpenButtonState(event.getValue());
            }
        });

        display.disableOpenButton();

    }

    private void updateOpenButtonState(Object filePath) {
        if (filePath == null || filePath.toString().trim().length() == 0) {
            display.disableOpenButton();
        } else {
            display.enableOpenButton();
        }
    }

    private void openFile() {
        String filePath = display.getFilePathFieldOrigin().getValue();

        if (filePath == null || filePath.trim().length() == 0) {
            display.disableOpenButton();
            return;
        }

        filePath = retrieveRelativeFilePath(filePath);
        final String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

        try {
            VirtualFileSystem.getInstance().getItemByPath(filePath,
                                                          new AsyncRequestCallback<ItemWrapper>(
                                                                  new ItemUnmarshaller(new ItemWrapper(new FileModel()))) {

                                                              @Override
                                                              protected void onSuccess(ItemWrapper result) {
                                                                  doOpenFile(result.getItem());
                                                              }

                                                              @Override
                                                              protected void onFailure(Throwable exception) {
                                                                  String message = IDE.IDE_LOCALIZATION_MESSAGES
                                                                                      .openFileByPathErrorMessage(fileName);
                                                                  Dialogs.getInstance().showError(message);
                                                              }
                                                          });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }

        IDE.getInstance().closeView(display.asView().getId());
    }

    /**
     * Retrieves relative path to the file from absolute path.
     *
     * @param absoluteFilePath
     *         link to the file
     * @return relative path to the file
     */
    private String retrieveRelativeFilePath(String absoluteFilePath) {
        String vfsURL = VirtualFileSystem.getInstance().getURL();

        if (!absoluteFilePath.startsWith(vfsURL)) {
            return absoluteFilePath;
        }

        int index = absoluteFilePath.indexOf('/', vfsURL.length() + 1);

        return absoluteFilePath.substring(index + 1);
    }

    /**
     * Open file and/or go to parent folder.
     *
     * @param item
     *         file which must be opened
     */
    private void doOpenFile(Item item) {
        if (item.getItemType() == ItemType.FILE) {
            // if tab with file content is active
            if (activeFile != null && activeFile.getId().equals(item.getId())) {
                IDE.fireEvent(new GoToItemEvent());
                return;
            }

            isNeedGoToFolderOnActiveFileChanged = true;

            IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
            IDE.fireEvent(new OpenFileEvent((FileModel)item));
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        this.activeFile = event.getFile();

        // if file opened by OpenFileByPathControl
        if (isNeedGoToFolderOnActiveFileChanged) {
            IDE.fireEvent(new GoToItemEvent());
            isNeedGoToFolderOnActiveFileChanged = false;
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

    /** @see org.exoplatform.ide.client.navigation.event.OpenFileByPathHandler#onOpenFileByPath(org.exoplatform.ide.client.navigation
     * .event.OpenFileByPathEvent) */
    @Override
    public void onOpenFileByPath(OpenFileByPathEvent event) {
        if (display == null) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView(d.asView());
            bindDisplay(d);
            display.focusInPathField();
        } else {
            IDE.fireEvent(new ExceptionThrownEvent("Display OpenFileByPath must be null"));
        }
    }

}
