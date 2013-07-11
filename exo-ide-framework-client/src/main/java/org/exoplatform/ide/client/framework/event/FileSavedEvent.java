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
package org.exoplatform.ide.client.framework.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Event, generated by IDE, to notify, that file was saved.
 * <p/>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class FileSavedEvent extends GwtEvent<FileSavedHandler> {

    public static final GwtEvent.Type<FileSavedHandler> TYPE = new GwtEvent.Type<FileSavedHandler>();

    /** Saved file. */
    private FileModel file;

    /** Source of original file. */
    private String sourceHref;

    /**
     * @param file
     *         - file, that was saved
     * @param sourceHref
     *         - href of original file, that was saved
     */
    public FileSavedEvent(FileModel file, String sourceHref) {
        this.file = file;
        this.sourceHref = sourceHref;
    }

    public FileModel getFile() {
        return file;
    }

    public String getSourceHref() {
        return sourceHref;
    }

    @Override
    protected void dispatch(FileSavedHandler handler) {
        handler.onFileSaved(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<FileSavedHandler> getAssociatedType() {
        return TYPE;
    }

}