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
package org.exoplatform.ide.client.edit.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.edit.event.LockFileEvent;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.vfs.client.event.ItemLockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemLockedHandler;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Control for manual lock or unlock file.
 * <p/>
 * Changes selection status after pressure.
 * <p/>
 * If active file changes, check is file is locked and also changes selection status.
 *
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 */
@RolesAllowed({"workspace/developer"})
public class LockUnlockFileControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
                                                                    ApplicationSettingsReceivedHandler, ItemUnlockedHandler,
                                                                    ItemLockedHandler {

    // Edit/Show \\ Hide Line Numbers
    public static final String ID = "Edit/Lock \\ Unlock File";

    public static final String TITLE_LOCK = IDE.IDE_LOCALIZATION_CONSTANT.lockFileLockControl();

    public static final String TITLE_UNLOCK = IDE.IDE_LOCALIZATION_CONSTANT.lockFileUnlockControl();

    private boolean fileLocked = false;

    private Map<String, String> lockTokens;

    private FileModel activeFile;

    /**
     *
     */
    public LockUnlockFileControl() {
        super(ID);
        setTitle(TITLE_LOCK);
        setImages(IDEImageBundle.INSTANCE.lockUnlockFile(), IDEImageBundle.INSTANCE.lockUnlockFileDisabled());
        setEvent(new LockFileEvent(true));
        setEnabled(true);
        setDelimiterBefore(true);
        setCanBeSelected(true);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
        IDE.addHandler(ItemLockedEvent.TYPE, this);
        IDE.addHandler(ItemUnlockedEvent.TYPE, this);
    }

    /**
     * Handle this event to update status of button according to status of active file: locked or unlocked.
     *
     * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client
     * .editor.event.EditorActiveFileChangedEvent)
     */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        FileModel file = event.getFile();
        activeFile = file;

        if (file == null || event.getEditor() == null) {
            setVisible(false);
            return;
        }

        setVisible(true);

        if (!file.isPersisted()) {
            fileLocked = false;
            update();
            setEnabled(false);
            return;
        }

        if (activeFile.isLocked()) {
            if (!lockTokens.containsKey(activeFile.getId())) {
                fileLocked = false;
                update();
                setEnabled(false);
                return;
            }
        }

        setEnabled(true);

        String lockToken = lockTokens.get(file.getId());

        if (lockToken == null) {
            fileLocked = false;
        } else {
            fileLocked = true;
        }
        update();
    }

    /** Update selection status, prompt and event of button according to status of active file: locked or unlocked. */
    private void update() {
        setSelected(fileLocked);

        if (fileLocked) {
            setTitle(TITLE_UNLOCK);
            setPrompt(TITLE_UNLOCK);
            setEvent(new LockFileEvent(false));
        } else {
            setTitle(TITLE_LOCK);
            setPrompt(TITLE_LOCK);
            setEvent(new LockFileEvent(true));
        }
    }

    /** @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org
     * .exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent) */
    @Override
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null) {
            event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
        }

        lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
    }

    /**
     * Handle ItemunlockEvent to deselect button, if active file was unlocked.
     *
     * @see org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.framework.vfs
     * .event.ItemUnlockedEvent)
     */
    public void onItemUnlocked(ItemUnlockedEvent event) {
        if (isItemActiveFile(event.getItem())) {
            fileLocked = false;
            update();
        }
    }

    /**
     * Checks, if item is instance of file. If item is file, checks, is active file equals to item.
     *
     * @param item
     *         - item to check is equals to active file
     * @return true - item is active file, false - item is not active file
     */
    private boolean isItemActiveFile(Item item) {
        if (item == null || activeFile == null || !(item instanceof File)) {
            return false;
        }

        if (activeFile.getId().equals(item.getId())) {
            return true;
        }

        return false;
    }

    /**
     * Handle this event, to select button, if active file was locked.
     *
     * @see org.exoplatform.ide.vfs.client.event.ItemLockedHandler#onItemLocked(org.exoplatform.ide.vfs.client.event.ItemLockedEvent)
     */
    @Override
    public void onItemLocked(ItemLockedEvent event) {
        if (isItemActiveFile(event.getItem())) {
            fileLocked = true;
            update();
        }
    }
}
