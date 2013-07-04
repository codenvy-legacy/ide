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
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.collaboration.CollaborationManager;
import com.google.collide.dto.FileOperationNotification.Operation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.operation.ItemsOperationPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.List;
import java.util.Map;

/**
 * Presenter for renaming folders and files form.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RenameFolderPresenter extends ItemsOperationPresenter implements 
        RenameItemHander, ApplicationSettingsReceivedHandler, ItemsSelectedHandler, 
        EditorFileOpenedHandler, EditorFileClosedHandler, ViewClosedHandler,
        ProjectOpenedHandler, ProjectClosedHandler {

    /** Interface for display for renaming files and folders. */
    public interface Display extends IsView {

        HasValue<String> getNameField();

        HasClickHandlers getRenameButton();

        HasClickHandlers getCancelButton();

        HasKeyPressHandlers getNameFieldKeyPressHandler();

        void enableRenameButton(boolean enable);

        void focusInNameField();

    }

    private Display display;

    private List<Item> selectedItems;
    
    private ProjectModel openedProject;

    public RenameFolderPresenter() {
        IDE.addHandler(RenameItemEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    public void bindDisplay(Display d) {
        display = d;

        display.enableRenameButton(false);

        display.getNameField().setValue(selectedItems.get(0).getName());

        display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableRenameButton(wasItemPropertiesChanged());
            }
        });

        display.getRenameButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (!display.getNameField().getValue().matches("(^[-.a-zA-Z0-9])([-._a-zA-Z0-9])*$")) {
                    if (display.getNameField().getValue().startsWith("_")) {
                        Dialogs.getInstance()
                               .showInfo(org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.noIncorrectProjectNameTitle(),
                                         org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.projectNameStartWith_Message());
                    } else {
                        Dialogs.getInstance()
                               .showInfo(org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.noIncorrectProjectNameTitle(),
                                         org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.noIncorrectProjectNameMessage());
                    }
                }
                else {
                    renameFolder();
                }
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
                    renameFolder();
                }
            }

        });

        display.focusInNameField();
    }

    private boolean wasItemPropertiesChanged() {
        final String newName = display.getNameField().getValue();
        if (newName == null || newName.length() == 0) {
            return false;
        }
        final String oldName = selectedItems.get(0).getName();
        return !newName.equals(oldName);
    }

    private String getDestination() {
        return display.getNameField().getValue();
    }

    private void updateOpenedFiles(FolderModel source, FolderModel destination) {
    //private void updateOpenedFiles(Folder folder, String sourcePath) {
        // TODO
        if (openedFiles == null || openedFiles.isEmpty())
            return;

        for (FileModel file : openedFiles.values()) {
            //if (file.getPath().startsWith(sourcePath)) {
            if (file.getPath().startsWith(source.getPath())) {
                String fileHref = file.getPath().replace(source.getPath(), destination.getPath());
                file.setPath(fileHref);

                IDE.fireEvent(new EditorReplaceFileEvent(file, file));
            }
        }
    }

    private void completeMove(FolderModel folder, String sourceId) {
        if (openedProject != null)
        {
            if (openedProject.getId().equals(folder.getId())) {
                IDE.fireEvent(new RefreshBrowserEvent(openedProject, openedProject));
            } else {
                IDE.fireEvent(new RefreshBrowserEvent(folder.getParent(), folder));
            }
        }
        else
        {
//            if (folder instanceof ProjectModel) {
//                IDE.fireEvent(new OpenProjectEvent((ProjectModel)folder));
//            } else {
//                IDE.fireEvent(new RefreshBrowserEvent(((ItemContext)renamedItem).getParent(), renamedItem));
//            }            
        }

        closeView();
    }

    /**
     * Rename folder
     */
    private void renameFolder() {
        final FolderModel source = (FolderModel)selectedItems.get(0);
        final String originalFolderId = source.getId();
        String newName = getDestination();
        
        try {
            VirtualFileSystem.getInstance().rename(source, null, newName, lockTokens.get(source.getId()),
                   new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper())) {
                       @Override
                       protected void onSuccess(ItemWrapper result) {
                           FolderModel destination = (FolderModel)result.getItem();
                           
//                           ItemContext ic = (ItemContext)result.getItem();
//                           ic.setParent(((ItemContext)item).getParent());
//                           ic.setProject(((ItemContext)item).getProject());
                           //itemMoved((Folder)result.getItem(), item.getPath());
                           
                           source.setId(destination.getId());
                           source.setName(destination.getName());
                           source.setMimeType(destination.getMimeType());
                           source.setPath(destination.getPath());
                           source.setParentId(destination.getParentId());
                           source.setCreationDate(destination.getCreationDate());
                           
                           source.getProperties().clear();
                           source.getProperties().addAll(destination.getProperties());
                           
                           source.getLinks().clear();
                           source.getLinks().putAll(destination.getLinks());
                           
                           //String oldItemPath = item.getPath();
                           //updateOpenedFiles((Folder)result.getItem(), oldItemPath);
                           //updateOpenedFiles(source, destination);
                           //completeMove((FolderModel)result.getItem());
                           
                           completeMove(source, originalFolderId);
                       }

                       @Override
                       protected void onFailure(Throwable exception) {
                           IDE.fireEvent(new ExceptionThrownEvent(exception));
                       }
                   });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        } catch (Exception e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRenameItem(RenameItemEvent event) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            // throwing an exception is in RenameFilePresenter
            return;
        }

        if (selectedItems.get(0) instanceof Folder) {
            CollaborationManager collaborationManager = CollabEditorExtension.get().getCollaborationManager();
            for (Item i : selectedItems) {
                for (String path : collaborationManager.getOpenedFiles().asIterable()) {
                    if (path.startsWith(i.getPath())) {
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

    private void openView() {
        if (display == null) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView(d.asView());
            bindDisplay(d);
        } else {
            IDE.fireEvent(new ExceptionThrownEvent("Display RenameFolder must be null"));
        }
    }

    private void closeView() {
        IDE.getInstance().closeView(display.asView().getId());
    }


    /** {@inheritDoc} */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        openedProject = event.getProject();
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        openedProject = null;
    }

}
