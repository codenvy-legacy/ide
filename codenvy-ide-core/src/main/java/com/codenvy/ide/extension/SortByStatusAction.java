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
package com.codenvy.ide.extension;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.ToggleAction;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class SortByStatusAction extends ToggleAction {

    private boolean                  selected;
    private ExtensionManagerViewImpl view;

    public SortByStatusAction(ExtensionManagerViewImpl view, Resources resources) {
        super("Sort by Status", "Sort by Status", resources.extension());
        this.view = view;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSelected(ActionEvent e) {
        return selected;
    }

    /** {@inheritDoc} */
    @Override
    public void setSelected(ActionEvent e, boolean state) {
        selected = state;
        view.sortByStatus(state);
    }
}
