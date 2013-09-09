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

import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ItemsSelectedEvent extends GwtEvent<ItemsSelectedHandler> {

    public static final GwtEvent.Type<ItemsSelectedHandler> TYPE = new GwtEvent.Type<ItemsSelectedHandler>();

    private List<Item> selectedItems;

    private View view;

    public ItemsSelectedEvent(List<Item> selectedItems, View view) {
        this.selectedItems = selectedItems;
        this.view = view;
    }

    public ItemsSelectedEvent(Item item, View view) {
        selectedItems = new ArrayList<Item>();
        if (item != null) {
            selectedItems.add(item);            
        }
        this.view = view;
    }

    @Override
    protected void dispatch(ItemsSelectedHandler handler) {
        handler.onItemsSelected(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ItemsSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public List<Item> getSelectedItems() {
        List<Item> items = new ArrayList<Item>();
        if (selectedItems != null) {
            items.addAll(selectedItems);
        }

        return items;
    }

    public View getView() {
        return view;
    }

}
