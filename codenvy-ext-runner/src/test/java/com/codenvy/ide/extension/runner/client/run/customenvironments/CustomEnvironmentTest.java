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

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Testing {@link CustomEnvironment}.
 *
 * @author Artem Zatsarynnyy
 */
public class CustomEnvironmentTest {
    private static final String ENV_NAME = "env_1";
    private CustomEnvironment customEnvironment;

    @Test
    public void testGetScriptNames() throws Exception {
        customEnvironment = new CustomEnvironment(ENV_NAME);
        final List<String> scriptNames = customEnvironment.getScriptNames(false);

        Assert.assertEquals("Custom environment should contain 2 scripts.", 2, scriptNames.size());
        Assert.assertTrue("Custom environment should contain run.dc5y script",
                          scriptNames.contains(ENV_NAME + '/' + ENV_NAME + ".run.dc5y"));
        Assert.assertTrue("Custom environment should contain dockerenv.c5y.json file",
                          scriptNames.contains(ENV_NAME + '/' + ENV_NAME + ".dockerenv.c5y.json"));
    }
}
