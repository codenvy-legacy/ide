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
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class SaveFileEvent extends GwtEvent<SaveFileHandler> {

    public static final GwtEvent.Type<SaveFileHandler> TYPE = new GwtEvent.Type<SaveFileHandler>();

    private FileModel file;

    public SaveFileEvent() {
    }

    public SaveFileEvent(FileModel file) {
        this.file = file;
    }

    public FileModel getFile() {
        return file;
    }

    @Override
    protected void dispatch(SaveFileHandler handler) {
        handler.onSaveFile(this);
    }

    @Override
    public Type<SaveFileHandler> getAssociatedType() {
        return TYPE;
    }

}
