/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.project.api;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class TreeRefreshedEvent extends GwtEvent<TreeRefreshedHandler> {

    public static final GwtEvent.Type<TreeRefreshedHandler> TYPE = new GwtEvent.Type<TreeRefreshedHandler>();

    private FolderModel                                     folder;

    private Item                                            itemToSelect;

    public TreeRefreshedEvent(FolderModel folder) {
        this.folder = folder;
    }

    public TreeRefreshedEvent(FolderModel folder, Item itemToSelect) {
        this.folder = folder;
        this.itemToSelect = itemToSelect;
    }

    public FolderModel getFolder() {
        return folder;
    }

    public Item getItemToSelect() {
        return itemToSelect;
    }

    @Override
    public GwtEvent.Type<TreeRefreshedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TreeRefreshedHandler handler) {
        handler.onTreeRefreshed(this);
    }

}
