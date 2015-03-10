/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ToggleAction;

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
