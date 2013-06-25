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
// $codepro.audit.disable logExceptions
/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.navigation.handler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.*;
import org.exoplatform.ide.client.framework.event.CursorPosition;
import org.exoplatform.ide.client.framework.event.FileOpenedEvent;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.OpenFileHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handlers events for opening files.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class OpenFileCommandHandler implements OpenFileHandler, EditorFileOpenedHandler, EditorFileClosedHandler, ItemsSelectedHandler {

    private static final double MAX_FILE_CONTENT_LENGHT = 1000000;

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    private List<Item> selectedItems = new ArrayList<Item>();

    private CursorPosition cursorPosition;

    public OpenFileCommandHandler() {
        IDE.addHandler(OpenFileEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.event.OpenFileHandler#onOpenFile(org.exoplatform.ide.client.framework.event
     * .OpenFileEvent) */
    public void onOpenFile(OpenFileEvent event) {
        cursorPosition = event.getCursorPosition();

        FileModel file = event.getFile();
        if (file != null) {
            if (!file.isPersisted()) {
                openFile(file);
                return;
            }

            // TODO Check opened file!!!
            if (openedFiles.containsKey(file.getId())) {
                openFile(file);
                return;
            }
        } else {
            file = new FileModel();
            file.setId(event.getFileId());

            ProjectModel project = null;
            if (selectedItems != null && selectedItems.size() != 0) {
                Item item = selectedItems.get(0);

                if (item instanceof IDEProject) {
                    project = (ProjectModel)item;
                } else if (item instanceof ItemContext) {
                    project = ((ItemContext)item).getProject();
                }
            }
            
            file.setProject(project);
        }

        getFileProperties(file);

    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
     */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
    }

    private void getFileProperties(FileModel file) {
        try {
            VirtualFileSystem.getInstance().getItemById(file.getId(),
                                                        new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(file))) {
                                                            @Override
                                                            protected void onSuccess(ItemWrapper result) {

                                                                FileModel file = (FileModel)result.getItem();
                                                                if (MAX_FILE_CONTENT_LENGHT < file.getLength()) {
                                                                    Dialogs.getInstance()
                                                                           .showError("File opening failed. Size limit reached.");
                                                                    return;
                                                                }
                                                                openFile(file);
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                IDE.fireEvent(new ExceptionThrownEvent(exception,
                                                                                                       "Service is not deployed" +
                                                                                                       ".<br>Parent folder not found."));
                                                            }
                                                        });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, "Service is not deployed.<br>Parent folder not found."));
        }
    }

    private FileModel fileToOpen;

    private void openFile(FileModel file) {
        fileToOpen = file;
        IDE.fireEvent(new EditorOpenFileEvent(file, cursorPosition));
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client
     * .framework.editor.event.EditorFileOpenedEvent) */
    public void onEditorFileOpened(final EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();

        if (fileToOpen != null && event.getFile().getId().equals(fileToOpen.getId())) {
            fileToOpen = null;
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    IDE.fireEvent(new FileOpenedEvent(event.getFile()));
                }
            });
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client
     * .framework.editor.event.EditorFileClosedEvent) */
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

}
