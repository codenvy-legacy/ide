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
package com.codenvy.ide.ext.git.client;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * Base test for git extension.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseTest {
    public static final String  PROJECT_ID      = "projectID";
    public static final String  PROJECT_PATH    = "/";
    public static final String  VFS_ID          = "vfsid";
    public static final boolean SELECTED_ITEM   = true;
    public static final boolean UNSELECTED_ITEM = false;
    public static final boolean ENABLE_BUTTON   = true;
    public static final boolean DISABLE_BUTTON  = false;
    public static final String  EMPTY_TEXT      = "";
    public static final String  PROJECT_NAME    = "test";
    public static final String  REMOTE_NAME     = "codenvy";
    public static final String  REMOTE_URI      = "git@github.com:codenvy/test.git";
    public static final String  REPOSITORY_NAME = "origin";
    public static final String  LOCAL_BRANCH    = "localBranch";
    public static final String  REMOTE_BRANCH   = "remoteBranch";
    @Mock
    protected Project                 project;
    @Mock
    protected ResourceProvider        resourceProvider;
    @Mock
    protected GitClientService        service;
    @Mock
    protected GitLocalizationConstant constant;
    @Mock
    protected ConsolePart             console;
    @Mock
    protected GitResources            resources;
    @Mock
    protected EventBus                eventBus;
    @Mock
    protected SelectionAgent          selectionAgent;

    @Before
    public void disarm() {
        // don't throw an exception if GWT.create() invoked
        GWTMockUtilities.disarm();

        when(resourceProvider.getVfsId()).thenReturn(VFS_ID);
        when(resourceProvider.getActiveProject()).thenReturn(project);
        when(project.getId()).thenReturn(PROJECT_ID);
        when(project.getPath()).thenReturn(PROJECT_PATH);
    }

    @After
    public void restore() {
        GWTMockUtilities.restore();
    }
}