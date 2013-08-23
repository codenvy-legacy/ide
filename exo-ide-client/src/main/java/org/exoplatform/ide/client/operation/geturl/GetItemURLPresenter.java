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
package org.exoplatform.ide.client.operation.geturl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GetItemURLPresenter implements GetItemURLHandler, ItemsSelectedHandler, ViewClosedHandler {

    public interface Display extends IsView {

        HasClickHandlers getOkButton();

        HasValue<String> getPrivateURLField();

        HasValue<String> getPublicURLField();

    }

    private Display display;

    private List<Item> selectedItems = new ArrayList<Item>();

    public GetItemURLPresenter() {
        IDE.getInstance().addControl(new GetItemURLControl());

        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(GetItemURLEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
        updateURLFields();
    }

    @Override
    public void onGetItemURL(GetItemURLEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
    }

    private void updateURLFields() {
        if (display != null && !selectedItems.isEmpty()) {
            Item item = selectedItems.get(0);
            Link link = item.getLinkByRelation(Link.REL_CONTENT_BY_PATH);
            if (link != null) {
                String privateUrl = link.getHref();
                display.getPrivateURLField().setValue(privateUrl);

                String publicUrl = privateUrl.replaceFirst("/IDE/rest/private/ide/vfs", "/IDE/rest/ide/vfs");
                display.getPublicURLField().setValue(publicUrl);
            }
        }
    }

    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        updateURLFields();
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}
