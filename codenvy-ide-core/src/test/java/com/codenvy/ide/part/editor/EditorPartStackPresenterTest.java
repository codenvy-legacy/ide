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
package com.codenvy.ide.part.editor;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PartStack;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.PartStackView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.part.PartStackPresenter;
import com.codenvy.ide.texteditor.openedfiles.ListOpenedFilesPresenter;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Testing {@link com.codenvy.ide.part.editor.EditorPartStackPresenter} functionality.
 *
 * @author Alexander Andrienko
 */
@GwtModule("com.codenvy.ide.Core")
public class EditorPartStackPresenterTest extends GwtTestWithMockito {

    @Mock
    private PartStackUIResources partStackUIResources;

    @Mock
    private Resources resources;

    @Mock
    private EditorPartStackView view;

    @Mock
    private EventBus eventBus;

    @Mock
    private ListOpenedFilesPresenter listOpenedFilesPresenter;

    @Mock
    private AsyncCallback asyncCallback;

    @Mock
    private List<PartPresenter> parts;

    @Mock
    private  PartStackView.ActionDelegate actionDelegate;

    @Mock
    private EditorPartStackPresenter.PartStackEventHandler partStackEventHandler;

    @InjectMocks
    private EditorPartStackPresenter presenter;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void closeAllTabsTest() {
        EditorPartStackPresenter part = mock(EditorPartStackPresenter.class);
        EditorPartStackPresenter part2 = mock(EditorPartStackPresenter.class);
        EditorPartStackPresenter part3 = mock(EditorPartStackPresenter.class);

//        doReturn(part).when(parts).get(anyInt());
//        doReturn(3).when(parts).size();
//        doReturn(3).when(parts).size();

        presenter.addPart((PartPresenter) part);
        presenter.closeAllTabs(asyncCallback);
    }

    @Test
    public void removePartTest() {
        EditorPartStackPresenter presenter1 = Mockito.spy(presenter);
        PartPresenter part = mock(PartPresenter.class);

        presenter1.removePart(part, asyncCallback);

        verify(presenter1).close(eq(part), eq(asyncCallback));
    }
}
