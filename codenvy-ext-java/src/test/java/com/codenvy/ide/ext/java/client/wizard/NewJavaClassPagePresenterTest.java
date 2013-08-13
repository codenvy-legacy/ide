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

import com.codenvy.ide.ext.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.ext.java.client.wizard.NewJavaClassPagePresenter;
import com.codenvy.ide.ext.java.client.wizard.NewJavaClassPageView;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NewJavaClassPagePresenterTest extends WizardsBaseTest {

    @Mock
    private NewJavaClassPageView view;

    @Mock
    private EditorAgent editorAgent;

    @Mock
    private SelectionAgent selectionAgent;

    private NewJavaClassPagePresenter presenter;

    @Before
    public void setUp() {
        presenter = new NewJavaClassPagePresenter(view, resourceProvider, editorAgent, selectionAgent);
        presenter.setUpdateDelegate(updateDelegate);
        when(view.getClassName()).thenReturn("MyClass");
        when(view.getClassType()).thenReturn("Class");
    }

    @Test
    public void testIsCompleted() throws Exception {
        presenter.checkTypeName();
        assertThat(presenter.isCompleted()).isTrue();
    }

    @Test
    public void testCheckTypeName() throws Exception {
        when(view.getClassName()).thenReturn("@$@#$@!!!");
        presenter.checkTypeName();
        assertThat(presenter.canFinish()).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDoFinish() throws Exception {
        presenter.checkTypeName();
        presenter.doFinish();
        verify(project).createCompilationUnit(eq(sourceFolder), eq("MyClass.java"), contains("class MyClass"),
                                              (AsyncCallback<CompilationUnit>)any());
    }
}
