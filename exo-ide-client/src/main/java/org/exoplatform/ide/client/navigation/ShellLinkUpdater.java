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
package org.exoplatform.ide.client.navigation;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.shared.ItemType;

/**
 * This class updates link to working directory which uses by Shell.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class ShellLinkUpdater implements ItemsSelectedHandler {

    public ShellLinkUpdater() {
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().isEmpty()) {
            return;
        }

        String path = event.getSelectedItems().get(0).getItemType().value().equals(ItemType.FILE.value()) ? event.getSelectedItems().get(0)
                                                                                                                 .getParentId()
                                                                                                          : event.getSelectedItems().get(0)
                                                                                                                 .getId();

        Element ae = DOM.getElementById("shell-link");
        if (ae == null) {
            return;
        }

        AnchorElement a = AnchorElement.as(ae);
        String newHref = "/ide/" + Utils.getWorkspaceName() + "/_app/shell?workdir=" + path;
        a.setHref(newHref);
    }

}
