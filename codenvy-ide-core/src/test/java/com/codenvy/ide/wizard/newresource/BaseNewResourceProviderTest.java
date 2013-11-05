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
package com.codenvy.ide.wizard.newresource;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * The base test for testing new resource providers.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseNewResourceProviderTest {
    public static final String  EMPTY_STRING  = "";
    public static final String  RESOURCE_NAME = "resourceName";
    public static final boolean IN_CONTEXT    = true;
    @Mock
    protected Folder                  parent;
    @Mock
    protected Project                 project;
    @Mock
    protected Folder                  folder;
    @Mock
    protected AsyncCallback<Resource> callback;
    @Mock
    protected Resources               resources;
    @Mock
    protected SelectionAgent          selectionAgent;
    @Mock
    protected Throwable               throwable;
    protected NewResourceProvider     newResourceProvider;

    @Test
    public void testCreateWhenRequestIsSuccessful() throws Exception {
        newResourceProvider.create(RESOURCE_NAME, parent, project, callback);
    }

    @Test
    public void testCreateWhenRequestIsFailed() throws Exception {
        newResourceProvider.create(RESOURCE_NAME, parent, project, callback);

        verify(callback).onFailure(eq(throwable));
    }
}