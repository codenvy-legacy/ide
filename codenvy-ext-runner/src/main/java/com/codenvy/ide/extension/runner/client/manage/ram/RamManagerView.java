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
package com.codenvy.ide.extension.runner.client.manage.ram;


import com.codenvy.ide.api.mvp.View;

/**
 * @author Vitaly Parfonov
 */
public interface RamManagerView extends View<RamManagerView.ActionDelegate> {

    public interface ActionDelegate {

        void setDirty(boolean b);

        void validateRamSize(String value);

    }

    String getRam();

    void showRam(String ram);

    void showWarnMessage(String s);
}
