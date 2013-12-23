/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [2013] Codenvy, S.A. 
 * All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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

        StringMap<Array<String>> natures = Collections.createStringMap();
        natures.put("primaryNature1", Collections.<String>createArray("secondaryNature1"));
        natures.put("primaryNature2", Collections.<String>createArray("secondaryNature2"));

        agent.register("id", "title", null, natures, pages, false);

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