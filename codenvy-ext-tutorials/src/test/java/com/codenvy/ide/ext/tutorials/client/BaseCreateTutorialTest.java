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
package com.codenvy.ide.ext.tutorials.client;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardKeys;
import com.codenvy.ide.resources.model.Project;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.codenvy.ide.api.ui.wizard.WizardPage.CommitCallback;
import static org.mockito.Mockito.when;

/**
 * Base test for creating tutorial project page.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseCreateTutorialTest {
    public static final String PROJECT_NAME = "projectName";
    @Mock
    protected TutorialsClientService service;
    @Mock
    protected ResourceProvider       resourceProvider;
    @Mock
    protected CommitCallback         callback;
    @Mock
    protected Project                project;
    @Mock
    protected WizardContext          wizardContext;
    @Mock
    protected Throwable              throwable;

    @Before
    public void setUp() {
        when(wizardContext.getData(WizardKeys.PROJECT_NAME)).thenReturn(PROJECT_NAME);
    }
}