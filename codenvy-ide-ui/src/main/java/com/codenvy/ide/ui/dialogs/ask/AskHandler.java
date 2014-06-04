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
package com.codenvy.ide.ui.dialogs.ask;

/**
 * Handler for user interaction in Ask dialog window
 *
 * @author Vitaly Parfonov
 */
public abstract class AskHandler {

    /**
     * Call if user click Ok button
     */
    public abstract void onOk();

    /**
     * Call if user click cancel button.
     * By default nothing todo.
     * If need custom interaction override it.
     */
    public void onCancel() {
        //by default nothing todo
    }
}
