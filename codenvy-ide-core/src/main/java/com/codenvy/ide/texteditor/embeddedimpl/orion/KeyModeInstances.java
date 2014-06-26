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
package com.codenvy.ide.texteditor.embeddedimpl.orion;

import java.util.HashMap;
import java.util.Map;

import com.codenvy.ide.texteditor.embeddedimpl.orion.jso.OrionKeyModeOverlay;

/**
 * An instance repository for keymode instances.
 * 
 * @author "Mickaël Leduque"
 */
public class KeyModeInstances {

    private Map<KeyMode, OrionKeyModeOverlay> instances = new HashMap<>();

    public void add(final KeyMode key, final OrionKeyModeOverlay instance) {
        this.instances.put(key, instance);
    }

    public OrionKeyModeOverlay getInstance(final KeyMode keyMode) {
        return instances.get(keyMode);
    }
}
