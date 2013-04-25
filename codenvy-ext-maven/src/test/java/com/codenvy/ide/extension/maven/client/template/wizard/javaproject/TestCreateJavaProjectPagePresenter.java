/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.extension.maven.client.template.wizard.javaproject;


import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.java.client.JavaClientBundle;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.inject.matcher.Matchers.any;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link CreateJavaProjectPagePresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestCreateJavaProjectPagePresenter {
    @Mock
    private CreateJavaProjectPageView      view;
    @Mock
    private JavaClientBundle               resources;
    @Mock
    private PaaSAgent                      paasAgent;
    @Mock
    private CreateJavaProjectPresenter     createJavaProjectPresenter;
    private CreateJavaProjectPagePresenter presenter;

    @Before
    public void setUp() throws Exception {
        // don't throw an exception if GWT.create() invoked
        GWTMockUtilities.disarm();

        presenter = new CreateJavaProjectPagePresenter(resources, view, paasAgent, createJavaProjectPresenter);
        when(view.getSourceFolder()).thenReturn("src");
    }

    @After
    public void tearDown() throws Exception {
        GWTMockUtilities.restore();
    }

    @Test
    public void testIsCompleted() throws Exception {
        presenter.checkSourceFolederInput();
        assertThat(presenter.isCompleted()).isTrue();
    }

    @Test
    public void testGetNotice() throws Exception {
        when(view.getSourceFolder()).thenReturn("%*^%^%$^%");
        presenter.checkSourceFolederInput();
        assertThat(presenter.getNotice()).contains("Incorrect source folder name.");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDoFinish() throws Exception {
        presenter.checkSourceFolederInput();
        presenter.doFinish();
        verify(createJavaProjectPresenter).create((AsyncCallback<Project>)any());
    }
}