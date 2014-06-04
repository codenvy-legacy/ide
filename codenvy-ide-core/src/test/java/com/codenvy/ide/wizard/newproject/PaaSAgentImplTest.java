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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.google.inject.Provider;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link PaaSAgentImpl} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@GwtModule("com.codenvy.ide.Core")
public class PaaSAgentImplTest extends GwtTestWithMockito {
    @Mock
    private NewProjectWizard newProjectWizard;
    private PaaSAgentImpl    agent;

    @Before
    public void setUp() {
        agent = new PaaSAgentImpl(newProjectWizard);
    }

    @Test
    public void testRegister() throws Exception {
        assertEquals(agent.getPaaSes().size(), 1);

        Provider<? extends AbstractPaasPage> pageProvider = mock(Provider.class);
        Array<Provider<? extends AbstractPaasPage>> pages = Collections.createArray(pageProvider, pageProvider);

        Array<String> projectTypes = Collections.createArray();
        projectTypes.add("projectType1");
        projectTypes.add("projectType2");

        agent.register("id", "title", null, projectTypes, pages, false);

        assertEquals(agent.getPaaSes().size(), 2);
        verify(newProjectWizard, times(pages.size())).addPaaSPage(eq(pageProvider));
    }

    @Test
    public void testRegisterWhenPaaSWithGivenIdIsExist() throws Exception {
        assertEquals(agent.getPaaSes().size(), 1);

        agent.register("id", "title", null, null, Collections.<Provider<? extends AbstractPaasPage>>createArray(), false);

        assertEquals(agent.getPaaSes().size(), 2);

        agent.register("id", "title", null, null, Collections.<Provider<? extends AbstractPaasPage>>createArray(), false);

        assertEquals(agent.getPaaSes().size(), 2);
    }
}