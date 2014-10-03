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
package com.codenvy.ide.build;

import com.codenvy.ide.api.build.BuildContext;

/**
 * Implementation of BuildContext
 *
 * @author Evgen Vidolob
 */
public class BuildContextImpl implements BuildContext {

    private boolean building;

    @Override
    public boolean isBuilding() {
        return building;
    }

    @Override
    public void setBuilding(boolean building) {
        this.building = building;
    }
}
