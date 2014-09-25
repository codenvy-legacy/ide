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
package com.codenvy.ide.extension.runner.client.run.customrun;

import com.codenvy.ide.extension.runner.client.run.customenvironments.CustomEnvironment;

/**
 * Adapter that allows to display {@link CustomEnvironment} in {@link CustomRunViewImpl}.
 *
 * @author Artem Zatsarynnyy
 */
public class CustomEnvironmentAdapter implements Environment {
    private final CustomEnvironment customEnvironment;

    public CustomEnvironmentAdapter(CustomEnvironment customEnvironment) {
        this.customEnvironment = customEnvironment;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getDisplayName() {
        return customEnvironment.getName();
    }

    @Override
    public String getDescription() {
        return "";
    }

    /** Get adapted {@link CustomEnvironment}. */
    public CustomEnvironment getCustomEnvironment() {
        return customEnvironment;
    }
}
