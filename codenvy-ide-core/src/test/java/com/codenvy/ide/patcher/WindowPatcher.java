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
package com.codenvy.ide.patcher;

import com.google.gwt.user.client.Window;
import com.googlecode.gwt.test.patchers.PatchClass;
import com.googlecode.gwt.test.patchers.PatchMethod;

/**
 * Patcher for Window class. Replace native method into Window.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@PatchClass(Window.class)
public class WindowPatcher {
    public static final String RETURNED_MESSAGE = "returned text";

    /** Patch prompt method. */
    @PatchMethod(override = true)
    public static String prompt(String msg, String initialValue) {
        return RETURNED_MESSAGE;
    }

    /** Patch confirm method. */
    @PatchMethod(override = true)
    public static boolean confirm(String msg) {
        return true;
    }

    /** Patch alert method. */
    @PatchMethod(override = true)
    public static void alert(String msg) {
        // do nothing
    }
}