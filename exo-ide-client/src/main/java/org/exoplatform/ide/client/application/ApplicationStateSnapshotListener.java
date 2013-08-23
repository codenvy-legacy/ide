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
package org.exoplatform.ide.client.application;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesEvent;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsHandler;
import org.exoplatform.ide.client.model.Settings;
import org.exoplatform.ide.client.model.SettingsService;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemDeletedHandler;
import org.exoplatform.ide.vfs.client.event.ItemLockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemLockedHandler;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ApplicationStateSnapshotListener implements EditorFileOpenedHandler, EditorFileClosedHandler,
                                                         EditorActiveFileChangedHandler, ApplicationSettingsReceivedHandler,
                                                         VfsChangedHandler, EditorReplaceFileHandler,
                                                         ItemLockedHandler, ItemUnlockedHandler, ItemDeletedHandler,
                                                         ProjectOpenedHandler,
                                                         ProjectClosedHandler, SaveApplicationSettingsHandler, ShowHideHiddenFilesHandler {

    private Map<String, FileModel> openedFiles = new LinkedHashMap<String, FileModel>();

    private ApplicationSettings applicationSettings;

    private Map<String, String> lockTokens;

    public ApplicationStateSnapshotListener() {
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(EditorReplaceFileEvent.TYPE, this);
        IDE.addHandler(ItemLockedEvent.TYPE, this);
        IDE.addHandler(ItemUnlockedEvent.TYPE, this);
        IDE.addHandler(ItemDeletedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(SaveApplicationSettingsEvent.TYPE, this);
        IDE.addHandler(ShowHideHiddenFilesEvent.TYPE, this);
    }

    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        applicationSettings = event.getApplicationSettings();
        if (applicationSettings.getValueAsMap(Settings.LOCK_TOKENS) == null) {
            applicationSettings.setValue(Settings.LOCK_TOKENS, new LinkedHashMap<String, String>(), Store.COOKIES);
        }
        lockTokens = applicationSettings.getValueAsMap(Settings.LOCK_TOKENS);
    }

    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
        storeOpenedFiles();
    }

    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
        storeOpenedFiles();
    }

    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        storeOpenedFiles();
        storeActiveFile(event.getFile());
    }

    private void storeOpenedFiles() {
        List<String> files = new ArrayList<String>();

        Iterator<String> openedFilesIter = openedFiles.keySet().iterator();
        while (openedFilesIter.hasNext()) {
            String fileName = openedFilesIter.next();

            FileModel file = openedFiles.get(fileName);
            if (!file.isPersisted()) {
                continue;
            }

            files.add(file.getId());
        }

        if (applicationSettings != null) {
            applicationSettings.setValue(Settings.OPENED_FILES, files, Store.COOKIES);
            SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
        }
    }

    private void storeActiveFile(FileModel file) {
        String activeFile = "";
        if (null != file) {
            activeFile = file.getPath();
        }

        if (applicationSettings != null) {
            applicationSettings.setValue(Settings.ACTIVE_FILE, activeFile, Store.COOKIES);
            SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
        }
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    public void onVfsChanged(VfsChangedEvent event) {
        String workspace = (event.getVfsInfo() != null) ? event.getVfsInfo().getId() : null;
        applicationSettings.setValue(Settings.ENTRY_POINT, workspace, Store.COOKIES);
        SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
    }

    /** @see org.exoplatform.ide.client.editor.event.EditorReplaceFileHandler#onEditorReplaceFile(org.exoplatform.ide.client.editor.event
     * .EditorReplaceFileEvent) */
    public void onEditorReplaceFile(EditorReplaceFileEvent event) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                storeOpenedFiles();
            }
        });
    }

    /** Store Lock Tokens */
    private void storeLockTokens() {
        applicationSettings.setValue(Settings.LOCK_TOKENS, lockTokens, Store.COOKIES);
        SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
    }

    /** @see org.exoplatform.ide.vfs.client.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.vfs.client.event.ItemDeletedEvent) */
    @Override
    public void onItemDeleted(ItemDeletedEvent event) {
        if (event.getItem() instanceof File) {
            if (lockTokens.containsKey(event.getItem().getId())) {
                lockTokens.remove(event.getItem().getId());
                storeLockTokens();
            }
        }
    }

    /** @see org.exoplatform.ide.vfs.client.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.vfs.client.event
     * .ItemUnlockedEvent) */
    @Override
    public void onItemUnlocked(ItemUnlockedEvent event) {
        lockTokens.remove(event.getItem().getId());
        storeLockTokens();

    }

    /** @see org.exoplatform.ide.vfs.client.event.ItemLockedHandler#onItemLocked(org.exoplatform.ide.vfs.client.event.ItemLockedEvent) */
    @Override
    public void onItemLocked(ItemLockedEvent event) {
        lockTokens.put(event.getItem().getId(), event.getLockToken().getLockToken());
        storeLockTokens();
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        applicationSettings.setValue(Settings.OPENED_PROJECT, "", Store.COOKIES);
        SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        applicationSettings.setValue(Settings.OPENED_PROJECT, event.getProject().getId(), Store.COOKIES);
        SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
    }

    /** @see org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler#onShowHideHiddenFiles(org.exoplatform.ide.client
     * .navigation.event.ShowHideHiddenFilesEvent) */
    @Override
    public void onShowHideHiddenFiles(ShowHideHiddenFilesEvent event) {
        applicationSettings.setValue(Settings.SHOW_HIDDEN_FILES, new Boolean(event.isFilesShown()), Store.COOKIES);
        SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
    }

    /** @see org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsHandler#onSaveApplicationSettings(org.exoplatform
     * .ide.client.framework.settings.event.SaveApplicationSettingsEvent) */
    @Override
    public void onSaveApplicationSettings(SaveApplicationSettingsEvent event) {
        switch (event.getSaveType()) {
            case COOKIES:
                SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
                break;
            case SERVER:
                try {
                    SettingsService.getInstance().saveSettingsToServer(applicationSettings,
                                                                       new AsyncRequestCallback<ApplicationSettings>() {
                                                                           @Override
                                                                           protected void onSuccess(ApplicationSettings result) {
                                                                           }

                                                                           @Override
                                                                           protected void onFailure(Throwable exception) {
                                                                               IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                           }
                                                                       });
                } catch (RequestException e) {
                    IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            default:
                // TODO
                break;
        }
    }

    // /**
    // * @see
    // org.exoplatform.ide.client.framework.vfs.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.client.framework.vfs.event
    // .ItemDeletedEvent)
    // */
    // public void onItemDeleted(ItemDeletedEvent event)
    // {

    // }

    // /**
    // * @see
    // org.exoplatform.ide.client.framework.vfs.event.MoveCompleteHandler#onMoveComplete(org.exoplatform.ide.client.framework.vfs.event
    // .MoveCompleteEvent)
    // */
    // public void onMoveComplete(MoveCompleteEvent event)
    // {
    // if (lockTokens.containsKey(event.getSourceHref()))
    // {
    // String lock = lockTokens.get(event.getSourceHref());
    // lockTokens.remove(event.getSourceHref());
    // lockTokens.put(event.getItem().getHref(), lock);
    // storeLockTokens();
    // }
    // else if (event.getItem() instanceof Folder)
    // {
    // String sourceHref = event.getSourceHref();
    // List<String> keys = new ArrayList<String>();
    // for (String k : lockTokens.keySet())
    // {
    // keys.add(k);
    // }
    //
    // for (String key : keys)
    // {
    // if (key.startsWith(sourceHref))
    // {
    // String lock = lockTokens.get(key);
    // String name = key.substring(sourceHref.length());
    // String path = event.getItem().getHref();
    // if (!path.endsWith("/"))
    // {
    // path += "/";
    // }
    // lockTokens.remove(key);
    // lockTokens.put(path + name, lock);
    // storeLockTokens();
    // }
    // }
    // }
    // }

}
