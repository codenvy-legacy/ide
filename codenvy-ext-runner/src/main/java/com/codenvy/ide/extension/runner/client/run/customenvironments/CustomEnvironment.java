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
package com.codenvy.ide.extension.runner.client.run.customenvironments;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents custom environment.
 *
 * @author Artem Zatsarynnyy
 */
public class CustomEnvironment {
    private String name;

    public CustomEnvironment(String name) {
        this.name = name;
    }

    public List<String> getScriptNames() {
        List<String> list = new ArrayList<>(2);
        list.add(name + ".run.dc5y");
        list.add(name + ".dockerenv.c5y.json");
        return list;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomEnvironment that = (CustomEnvironment)o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
