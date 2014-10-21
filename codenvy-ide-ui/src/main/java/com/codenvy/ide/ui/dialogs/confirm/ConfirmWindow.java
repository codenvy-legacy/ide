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
package com.codenvy.ide.ui.dialogs.confirm;

/**
 * Interface to the confirm window component.
 * 
 * @author "Mickaël Leduque"
 */
public interface ConfirmWindow {

    /** Operate the confirmation window: show it and manage user actions. */
    void confirm();
}
