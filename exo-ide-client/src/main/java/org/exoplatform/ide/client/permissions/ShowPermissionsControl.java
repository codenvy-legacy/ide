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
package org.exoplatform.ide.client.permissions;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;

/**
 * This class represent command in main menu(<var>View/Show Permissions</var>).<br>
 * Fired {@link ShowPermissionsEvent} if clicked. <br>
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 19, 2010 $
 */

@RolesAllowed({"workspace/developer"})
public class ShowPermissionsControl extends SimpleControl implements IDEControl, ItemsSelectedHandler {
    public static final String ID = "View/Permissions";

    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.permissionsControl();

    /**
     *
     */
    public ShowPermissionsControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(IDEImageBundle.INSTANCE.properties(), IDEImageBundle.INSTANCE.propertiesDisabled());
        setEvent(new ShowPermissionsEvent());
        setVisible(true);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     *      .module.navigation.event.selection.ItemsSelectedEvent)
     */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() != 1) {
            setEnabled(false);
            return;
        }
        setEnabled(true);
    }

}
