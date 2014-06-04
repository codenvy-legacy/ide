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
package com.codenvy.ide.tutorial.gin.factory;

import com.codenvy.ide.api.parts.ConsolePart;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * The class that uses {@link Assisted} annotation for defining string value. It for using in {@link MyFactory}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class MyFactoryClass {
    private ConsolePart console;
    private String      someText;

    @Inject
    public MyFactoryClass(ConsolePart console, @Assisted String someText) {
        this.console = console;
        this.someText = someText;
    }

    public void doSomething() {
        console.print(someText);
    }
}