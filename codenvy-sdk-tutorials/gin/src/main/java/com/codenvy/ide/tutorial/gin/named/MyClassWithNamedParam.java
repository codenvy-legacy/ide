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
package com.codenvy.ide.tutorial.gin.named;

import com.codenvy.ide.api.parts.ConsolePart;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * The class that uses {@link Named} annotation for defining string value.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class MyClassWithNamedParam {
    private ConsolePart console;
    private String      someText;

    @Inject
    public MyClassWithNamedParam(ConsolePart console, @Named("myString") String someText) {
        this.console = console;
        this.someText = someText;
    }

    public void doSomething() {
        console.print(someText);
    }
}