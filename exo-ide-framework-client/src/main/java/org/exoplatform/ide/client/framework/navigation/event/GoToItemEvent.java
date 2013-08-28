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
package org.exoplatform.ide.client.framework.navigation.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.client.framework.event.CursorPosition;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GoToItemEvent extends GwtEvent<GoToItemHandler> {

    public static final GwtEvent.Type<GoToItemHandler> TYPE = new GwtEvent.Type<GoToItemHandler>();


    private final boolean needOpen;


    private final FileModel fileToOpen;


    private final CursorPosition cursorPosition;

    /** Will be opened parent folder for active file in Editor */
    public GoToItemEvent() {
        this(null, null);
    }

    public GoToItemEvent(FileModel fileToOpen, CursorPosition cursorPosition) {
        this(fileToOpen, cursorPosition, false);
    }

    public GoToItemEvent(FileModel fileToOpen, CursorPosition cursorPosition, boolean needOpen) {

        this.fileToOpen = fileToOpen;
        this.cursorPosition = cursorPosition;
        this.needOpen = needOpen;
    }


    @Override
    protected void dispatch(GoToItemHandler handler) {
        handler.onGoToFolder(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<GoToItemHandler> getAssociatedType() {
        return TYPE;
    }

    public FileModel getFileToOpen() {
        return fileToOpen;
    }

    public CursorPosition getCursorPosition() {
        return cursorPosition;
    }

    public boolean isNeedOpen() {
        return needOpen;
    }

}
