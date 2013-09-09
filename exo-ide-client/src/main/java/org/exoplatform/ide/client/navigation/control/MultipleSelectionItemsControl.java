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
package org.exoplatform.ide.client.navigation.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public abstract class MultipleSelectionItemsControl extends SimpleControl implements IDEControl, VfsChangedHandler,
                                                                                     ViewVisibilityChangedHandler {

    protected boolean browserSelected = true;

    private VirtualFileSystemInfo vfsInfo;

    /** @param id */
    protected MultipleSelectionItemsControl(String id) {
        super(id);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    /**
     * @param items
     * @return
     */
    public boolean isItemsInSameFolder(List<Item> items) {
        List<String> hrefs = new ArrayList<String>();
        for (Item i : items) {
            if (i.getId().equals(vfsInfo.getRoot().getId())) {
                return false;
            }
            String p = i.getPath();
            p = p.substring(0, p.lastIndexOf("/"));
            hrefs.add(p);
        }

        for (int i = 0; i < hrefs.size(); i++) {
            String path = hrefs.get(i);
            for (int j = i + 1; j < hrefs.size(); j++) {
                if (!path.equals(hrefs.get(j))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     *
     */
    protected abstract void updateEnabling();

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfsInfo = event.getVfsInfo();
        if (event.getVfsInfo() != null) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide
     * .client.framework.ui.api.event.ViewVisibilityChangedEvent) */
    @Override
    public void onViewVisibilityChanged(ViewVisibilityChangedEvent event) {
        if (event.getView() instanceof NavigatorDisplay) {
            browserSelected = event.getView().isViewVisible();
            updateEnabling();
        }
    }
}
