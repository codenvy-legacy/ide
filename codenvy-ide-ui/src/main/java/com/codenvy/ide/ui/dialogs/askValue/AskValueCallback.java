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
package com.codenvy.ide.ui.dialogs.askValue;

/**
 * Handler for user interaction in {@link AskValueDialog}.
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyy
 */
public abstract class AskValueCallback {

    /**
     * Call if user click Ok button.
     *
     * @param value
     *         entered value
     */
    public abstract void onOk(String value);

    /**
     * Call if user click cancel button.
     * If need custom interaction override it.
     */
    public void onCancel() {
    }
}
