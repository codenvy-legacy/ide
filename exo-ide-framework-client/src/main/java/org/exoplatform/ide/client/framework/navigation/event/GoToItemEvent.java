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
