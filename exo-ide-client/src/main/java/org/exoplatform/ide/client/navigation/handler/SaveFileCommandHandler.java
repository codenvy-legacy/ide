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
package org.exoplatform.ide.client.navigation.handler;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.event.SaveFileEvent;
import org.exoplatform.ide.client.framework.event.SaveFileHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.navigation.control.SaveFileControl;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;

import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileCommandHandler implements SaveFileHandler, EditorActiveFileChangedHandler,
                                               ApplicationSettingsReceivedHandler {

    private FileModel activeFile;

    public SaveFileCommandHandler() {
        IDE.getInstance().addControl(new SaveFileControl(), Docking.TOOLBAR);

        IDE.addHandler(SaveFileEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
    }

    public void onSaveFile(SaveFileEvent event) {
        final FileModel file = event.getFile() != null ? event.getFile() : activeFile;
        if (file == null) {
            return;
        }

        if (!file.isPersisted()) {
            IDE.fireEvent(new SaveFileAsEvent(file, SaveFileAsEvent.SaveDialogType.YES_CANCEL, null, null));
            return;
        }

        if (file.isContentChanged()) {
            try {
                VirtualFileSystem.getInstance().updateContent(file, new AsyncRequestCallback<FileModel>() {

                    @Override
                    protected void onSuccess(FileModel result) {
                        getProperties(file);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        IDE.fireEvent(new ExceptionThrownEvent(exception, "Service is not deployed.<br>Resource not found."));
                    }
                });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e, "Service is not deployed.<br>Resource not found."));
            }
        } else {
            IDE.fireEvent(new FileSavedEvent(file, null));
        }

    }

    private void getProperties(final FileModel file) {
        // TODO
        try {
            VirtualFileSystem.getInstance().getItemById(file.getId(),
                                                        new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(file))) {
                                                            @Override
                                                            protected void onSuccess(ItemWrapper result) {
                                                                IDE.fireEvent(new FileSavedEvent((FileModel)result.getItem(), null));
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                            }
                                                        });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
    }

    /**
     * @see org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org
     *      .exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent)
     */
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null) {
            event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
        }
    }

}
