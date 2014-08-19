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
package com.codenvy.ide.api.projecttree;

/**
 * The settings for the project tree.
 *
 * @author Artem Zatsarynnyy
 */
public interface TreeSettings {
    /** The default settings for the project tree. */
    TreeSettings DEFAULT = new TreeSettings() {
        @Override
        public boolean isShowHiddenItems() {
            return false;
        }
    };

    /**
     * Checks if hidden items are shown in the "Project Explorer".
     *
     * @return <code>true</code> - if hidden items should be shown, <code>false</code> - otherwise
     */
    boolean isShowHiddenItems();
}
