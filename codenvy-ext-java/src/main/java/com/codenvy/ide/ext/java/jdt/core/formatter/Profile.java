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
package com.codenvy.ide.ext.java.jdt.core.formatter;

import java.util.Map;

public class Profile {
    private final String name;

    private final String id;

    private final Map<String, String> settings;

    /**
     * @param name
     * @param id
     * @param settings
     */
    public Profile(String name, String id, Map<String, String> settings) {
        super();
        this.name = name;
        this.id = id;
        this.settings = settings;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** @return the id */
    public String getId() {
        return id;
    }

    /** @return the settings */
    public Map<String, String> getSettings() {
        return settings;
    }

}