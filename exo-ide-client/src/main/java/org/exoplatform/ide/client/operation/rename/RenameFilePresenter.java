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
package org.exoplatform.ide.client.operation.rename;

import com.codenvy.ide.collaboration.ResourceLockedPresenter;
import com.google.collide.client.CollabEditor;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.collaboration.CollaborationManager;
import com.google.collide.dto.FileOperationNotification.Operation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasValue;

import org.eclipse.jdt.client.refactoring.rename.RefactoringRenameEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.Language;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.operation.ItemsOperationPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Presenter for renaming file and changing mime-type of file.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RenameFilePresenter extends ItemsOperationPresenter
        implements RenameItemHander, ItemsSelectedHandler, ViewClosedHandler {

    /** Interface for display for renaming files and folders. */
    public interface Display extends IsView {

        HasValue<String> getItemNameField();

        HasClickHandlers getRenameButton();

        HasClickHandlers getCancelButton();

        HasKeyPressHandlers getNameFieldKeyPressHandler();

        HasValue<String> getMimeType();

        void setMimeTypes(String[] mimeTypes);

        void enableMimeTypeSelect();

        void disableMimeTypeSelect();

        void setDefaultMimeType(String mimeType);

        void enableRenameButton(boolean enable);

        void addLabel(String text);

        void focusInNameField();

        void selectAllText();
    }

    private static final String CANT_CHANGE_MIME_TYPE_TO_OPENED_FILE = IDE.ERRORS_CONSTANT
                                                                          .renameItemCantRenameMimeTypeToOpenedFile();

    private static final String SELECT_ITEM_TO_RENAME = "";

    private Display display;

    private List<Item> selectedItems;

    private FileModel renamedFile;

    public RenameFilePresenter() {
        IDE.addHandler(RenameItemEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay(Display d) {
        display = d;

        display.enableRenameButton(false);

        display.getItemNameField().setValue(selectedItems.get(0).getName());
        display.selectAllText();
        display.getItemNameField().addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableRenameButton(wasItemPropertiesChanged());
            }
        });

        display.getRenameButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                rename();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                closeView();
            }
        });

        display.getNameFieldKeyPressHandler().addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER && wasItemPropertiesChanged()) {
                    rename();
                }
            }

        });

        FileModel file = (FileModel)selectedItems.get(0);

        List<String> mimeTypes = getSupportedMimeTypes();
        Collections.sort(mimeTypes);

        String[] valueMap = mimeTypes.toArray(new String[0]);

        display.setMimeTypes(valueMap);

        display.setDefaultMimeType(file.getMimeType());

        display.getMimeType().addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableRenameButton(wasItemPropertiesChanged());
            }
        });
        if (openedFiles.containsKey(file.getId())) {
            display.disableMimeTypeSelect();
            display.addLabel(CANT_CHANGE_MIME_TYPE_TO_OPENED_FILE);
        }

        display.focusInNameField();

    }

    private List<String> getSupportedMimeTypes() {
        FileType[] fileTypes = IDE.getInstance().getFileTypeRegistry().getSupportedFileTypes();
        List<String> mimeTypeList = new ArrayList<String>();
        for (FileType fileType : fileTypes) {
            mimeTypeList.add(fileType.getMimeType());
        }

        return mimeTypeList;
    }

    private boolean wasItemPropertiesChanged() {
        FileModel file = (FileModel)selectedItems.get(0);

        // if name is not set
        final String newName = display.getItemNameField().getValue();

        if (newName == null || newName.length() == 0) {
            return false;
        }

        // if mime-type is not set
        final String newMimeType = display.getMimeType().getValue();

        if (newMimeType == null || newMimeType.length() == 0) {
            return false;
        }

        // if file name was changed or file mime-type was changed, than return true;
        if (!file.getName().equals(newName) || !file.getMimeType().equals(newMimeType)) {
            return true;
        }
        return false;
    }

    protected void rename() {
        FileModel file = (FileModel)selectedItems.get(0);
        String newName = display.getItemNameField().getValue();
        newName = (file.getName().equals(newName)) ? null : newName;

        String newMimeType = display.getMimeType().getValue();
        newMimeType = (file.getMimeType().equals(newMimeType)) ? null : newMimeType;
        if (newMimeType != null && newMimeType.length() > 0) {
            file.setMimeType(newMimeType);
        }
        moveItem(file, newName, newMimeType);
    }

    private void completeMove() {
        IDE.fireEvent(new RefreshBrowserEvent(renamedFile.getParent(), renamedFile));

        closeView();
    }

    /**
     * Mote item.
     *
     * @param file
     *         - the file to rename (with old properties: href and name)
     * @param newName
     *         - the new name of file
     */
    private void moveItem(final FileModel file, final String newName, String newMimeType) {
        try {
            VirtualFileSystem.getInstance().rename(file, newMimeType, newName, lockTokens.get(file.getId()),
                                                   new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper())) {

                                                       @Override
                                                       protected void onSuccess(ItemWrapper result) {
                                                           renamedFile = (FileModel)result.getItem();
                                                           FileModel renamedFile = (FileModel)result.getItem();
                                                           renamedFile.setParent(file.getParent());
                                                           if (openedFiles.containsKey(file.getId())) {
                                                               renamedFile.setContent(openedFiles.get(file.getId()).getContent());
                                                               openedFiles.remove(file.getId());
                                                               openedFiles.put(renamedFile.getId(), renamedFile);

                                                               IDE.fireEvent(new EditorReplaceFileEvent(file, renamedFile));
                                                           }

                                                           completeMove();
                                                       }

                                                       @Override
                                                       protected void onFailure(Throwable exception) {
                                                           IDE.fireEvent(new ExceptionThrownEvent(exception,
                                                                                                  "Service is not deployed" +
                                                                                                  ".<br>Destination path does not " +
                                                                                                  "exist<br>Folder already has item with " +
                                                                                                  "same name."));
                                                       }
                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e,
                                                   "Service is not deployed.<br>Destination path does not exist<br>Folder already has " +
                                                   "item with same name."));
        } catch (Exception e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }

    }

    /** {@inheritDoc} */
    @Override
    public void onRenameItem(RenameItemEvent event) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            IDE.fireEvent(new ExceptionThrownEvent(SELECT_ITEM_TO_RENAME));
            return;
        }


        if (selectedItems.get(0) instanceof FileModel) {
            FileModel file = (FileModel)selectedItems.get(0);
            if (file.getMimeType().equals(MimeType.APPLICATION_JAVA)) {
                ProjectType projectType = ProjectType.fromValue(file.getProject().getProjectType());
                if (ProjectResolver.getProjectTypesByLanguage(Language.JAVA).contains(projectType)) {
                    IDE.fireEvent(new RefactoringRenameEvent(file));
                    return;
                }
            }
            CollaborationManager collaborationManager = CollabEditorExtension.get().getCollaborationManager();
            for (Item i : selectedItems) {
                if (openedEditors.containsKey(i.getId())) {
                    if (openedEditors.get(i.getId()) instanceof CollabEditor) {
                        if (collaborationManager.isFileOpened(i.getPath())) {
//                     Dialogs.getInstance().showError("Can't rename <b>" + i.getName() + "</b>. This file opened by other users.");
                            new ResourceLockedPresenter(
                                    new SafeHtmlBuilder().appendHtmlConstant("Can't rename file <b>").appendEscaped(
                                            i.getName()).appendHtmlConstant("</b>").toSafeHtml(), collaborationManager, i.getPath(), i,
                                    i.getPath(),
                                    Operation.RENAME);
                            return;
                        }
                    }
                }
                if (collaborationManager.isFileOpened(i.getPath())) {
//               Dialogs.getInstance().showError("Can't rename <b>" + i.getName() + "</b>. This file opened by other users.");
                    new ResourceLockedPresenter(
                            new SafeHtmlBuilder().appendHtmlConstant("Can't rename file <b>").appendEscaped(
                                    i.getName()).appendHtmlConstant("</b>").toSafeHtml(), collaborationManager, i.getPath(), i,
                            i.getPath(), Operation.RENAME);
                    return;
                }
                for (String path : collaborationManager.getOpenedFiles().asIterable()) {
                    if (path.startsWith(i.getPath())) {
                        Dialogs.getInstance()
                               .showError("Can't rename <b>" + i.getName() + "</b>. This folder contains file(s) opened by other users.");
                        new ResourceLockedPresenter(
                                new SafeHtmlBuilder().appendHtmlConstant("Can't rename folder <b>").appendEscaped(
                                        i.getName()).appendHtmlConstant("</b>").toSafeHtml(), collaborationManager, path, i,
                                i.getPath(), Operation.RENAME);
                        return;
                    }
                }
            }
            openView();
        }
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
    }

    private void openView() {
        if (display == null) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView(d.asView());
            bindDisplay(d);
        } else {
            IDE.fireEvent(new ExceptionThrownEvent("Display RenameFile must be null"));
        }
    }

    private void closeView() {
        IDE.getInstance().closeView(display.asView().getId());
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
