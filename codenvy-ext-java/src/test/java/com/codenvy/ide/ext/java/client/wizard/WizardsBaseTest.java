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
package com.codenvy.ide.ext.java.client.wizard;

import com.codenvy.ide.ext.java.client.BaseTest;

import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Resource;

import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class WizardsBaseTest extends BaseTest {
    @Mock
    protected ResourceProvider resourceProvider;

    @Mock
    protected JavaProject project;

    @Mock
    protected SourceFolder sourceFolder;

    @Mock
    protected com.codenvy.ide.ext.java.client.projectmodel.Package aPackage;

    @Mock
    protected WizardPagePresenter.WizardUpdateDelegate updateDelegate;

    @Before
    public void init() {
        when(resourceProvider.getActiveProject()).thenReturn(project);
        when(project.getSourceFolders()).thenReturn(JsonCollections.createArray(sourceFolder));
        when(sourceFolder.getChildren()).thenReturn(JsonCollections.<Resource>createArray(aPackage));
        when(sourceFolder.getName()).thenReturn("src");
        when(aPackage.getName()).thenReturn("com.ide");
    }
}
