/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ToggleAction;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class SortByStatusAction extends ToggleAction {

    private boolean                  selected;
    private ExtensionManagerViewImpl view;

    public SortByStatusAction(ExtensionManagerViewImpl view, Resources resources) {
        super("Sort by Status", "Sort by Status", resources.extension(), null);
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
