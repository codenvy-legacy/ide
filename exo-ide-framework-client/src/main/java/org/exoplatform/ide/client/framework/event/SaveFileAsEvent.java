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
package org.exoplatform.ide.client.framework.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Open new dialog window for asking new file name. Save new file with new name. Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileAsEvent extends GwtEvent<SaveFileAsHandler> {
    /** Enum to configure ask for value dialog window: two buttons (Yes, Cancel) or three buttons (Yes, No, Cancel). */
    public enum SaveDialogType {
        YES_CANCEL, EXTENDED
    }

    public static final GwtEvent.Type<SaveFileAsHandler> TYPE = new GwtEvent.Type<SaveFileAsHandler>();

    private FileModel file;

    private boolean saveOnly = false;

    private GwtEvent<?> eventFiredOnNo;

    private GwtEvent<?> eventFiredOnCancel;

    private SaveDialogType dialogType;

    public SaveFileAsEvent() {
    }

    public SaveFileAsEvent(FileModel file) {
        this.file = file;
    }

    public SaveFileAsEvent(FileModel file, boolean saveOnly) {
        this.file = file;
        this.saveOnly = saveOnly;
    }

    /**
     * @param file
     *         - file to save
     * @param type
     *         - type of dialog window (two or three buttons)
     * @param eventFiredOnNo
     *         - event, which will be fired if No button will be clicked
     * @param eventFiredOnCancel
     *         - event, which will be fired if Cancel button will be clicked (if null - window will be closed
     *         without any actions)
     */
    public SaveFileAsEvent(FileModel file, SaveDialogType type, GwtEvent<?> eventFiredOnNo,
                           GwtEvent<?> eventFiredOnCancel) {
        this.file = file;
        this.eventFiredOnNo = eventFiredOnNo;
        this.eventFiredOnCancel = eventFiredOnCancel;
        this.dialogType = type;
    }

    /**
     * @param type
     * @param eventFiredOnNo
     * @param eventFiredOnCancel
     */
    public SaveFileAsEvent(SaveDialogType type, GwtEvent<?> eventFiredOnNo, GwtEvent<?> eventFiredOnCancel) {
        this.eventFiredOnNo = eventFiredOnNo;
        this.eventFiredOnCancel = eventFiredOnCancel;
        this.dialogType = type;
    }

    public boolean isSaveOnly() {
        return saveOnly;
    }

    @Override
    protected void dispatch(SaveFileAsHandler handler) {
        handler.onSaveFileAs(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<SaveFileAsHandler> getAssociatedType() {
        return TYPE;
    }

    public FileModel getFile() {
        return file;
    }

    /** @return the eventFiredOnCancel */
    public GwtEvent<?> getEventFiredOnCancel() {
        return eventFiredOnCancel;
    }

    /** @return the eventFiredOnNo */
    public GwtEvent<?> getEventFiredOnNo() {
        return eventFiredOnNo;
    }

    /** @return the dialogType */
    public SaveDialogType getDialogType() {
        return dialogType;
    }

}
