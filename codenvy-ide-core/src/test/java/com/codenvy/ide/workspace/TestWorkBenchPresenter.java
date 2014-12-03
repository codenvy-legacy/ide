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
package com.codenvy.ide.workspace;

import com.codenvy.ide.api.constraints.Constraints;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.EditorPartStack;
import com.codenvy.ide.api.parts.OutlinePart;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PartStack;
import com.codenvy.ide.api.parts.PartStackType;
import com.codenvy.ide.api.parts.ProjectExplorerPart;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link WorkBenchPresenter} functionality.
 *
 * @author Igor Vinokur
 */
@GwtModule("com.codenvy.ide.Core")
public class TestWorkBenchPresenter extends GwtTestWithMockito {

    @Mock
    private WorkBenchViewImpl         view;
    @Mock
    private EditorPartStack           editorPartStackPresenter;
    @Mock(answer = Answers.RETURNS_MOCKS)
    private PartStackPresenterFactory stackPresenterFactory;
    @Mock
    private PartStackViewFactory      partViewFactory;
    @Mock
    private OutlinePart               outlinePart;
    @Mock
    private ProjectExplorerPart       projectExplorerPart;
    @Mock
    private NotificationManager       notificationManager;
    @Mock
    private HideWidgetCallback        hideWidgetCallback;

    private WorkBenchPresenter workBenchPresenter;

    @Mock
    private PartPresenter toolingPart;
    @Mock
    private PartPresenter informationPart;
    @Mock
    private PartPresenter editorPart;
    @Mock
    private PartPresenter navigationPart;

    private PartStack toolingPartStack;
    private PartStack informationPartStack;
    private PartStack editorPartStack;
    private PartStack navigationPartStack;

    @Before
    public void initialize() {
        view.splitPanel = mock(SplitLayoutPanel.class);

        workBenchPresenter = new WorkBenchPresenter(view, editorPartStackPresenter, stackPresenterFactory, partViewFactory,
                                                    outlinePart, projectExplorerPart, notificationManager, hideWidgetCallback);

        toolingPartStack = workBenchPresenter.getPartStack(PartStackType.TOOLING);
        informationPartStack = workBenchPresenter.getPartStack(PartStackType.INFORMATION);
        editorPartStack = workBenchPresenter.getPartStack(PartStackType.EDITING);
        navigationPartStack = workBenchPresenter.getPartStack(PartStackType.NAVIGATION);
    }

    @Test
    public void shouldRemoveParts() {
        when(toolingPartStack.containsPart(toolingPart)).thenReturn(true);
        when(informationPartStack.containsPart(informationPart)).thenReturn(true);
        when(editorPartStack.containsPart(editorPart)).thenReturn(true);
        when(navigationPartStack.containsPart(navigationPart)).thenReturn(true);

        workBenchPresenter.removePart(toolingPart);
        workBenchPresenter.removePart(informationPart);
        workBenchPresenter.removePart(editorPart);
        workBenchPresenter.removePart(navigationPart);

        verify(toolingPartStack).removePart(toolingPart);
        verify(informationPartStack).removePart(informationPart);
        verify(editorPartStack).removePart(editorPart);
        verify(navigationPartStack).removePart(navigationPart);

        reset(toolingPartStack);
        reset(informationPartStack);
        reset(editorPartStack);
        reset(navigationPartStack);

        workBenchPresenter.removePart(toolingPart);
        workBenchPresenter.removePart(informationPart);
        workBenchPresenter.removePart(editorPart);
        workBenchPresenter.removePart(navigationPart);

        verify(toolingPartStack, never()).removePart(toolingPart);
        verify(informationPartStack, never()).removePart(informationPart);
        verify(editorPartStack, never()).removePart(editorPart);
        verify(navigationPartStack, never()).removePart(navigationPart);
    }

    @Test
    public void shouldHideParts() {
        when(toolingPartStack.containsPart(toolingPart)).thenReturn(true);
        when(informationPartStack.containsPart(informationPart)).thenReturn(true);
        when(editorPartStack.containsPart(editorPart)).thenReturn(true);
        when(navigationPartStack.containsPart(navigationPart)).thenReturn(true);

        workBenchPresenter.hidePart(toolingPart);
        workBenchPresenter.hidePart(informationPart);
        workBenchPresenter.hidePart(editorPart);
        workBenchPresenter.hidePart(navigationPart);

        verify(toolingPartStack).hidePart(toolingPart);
        verify(informationPartStack).hidePart(informationPart);
        verify(editorPartStack).hidePart(editorPart);
        verify(navigationPartStack).hidePart(navigationPart);

        reset(toolingPartStack);
        reset(informationPartStack);
        reset(editorPartStack);
        reset(navigationPartStack);

        workBenchPresenter.hidePart(toolingPart);
        workBenchPresenter.hidePart(informationPart);
        workBenchPresenter.hidePart(editorPart);
        workBenchPresenter.hidePart(navigationPart);

        verify(toolingPartStack, never()).hidePart(toolingPart);
        verify(informationPartStack, never()).hidePart(informationPart);
        verify(editorPartStack, never()).hidePart(editorPart);
        verify(navigationPartStack, never()).hidePart(navigationPart);
    }

    @Test
    public void shouldExpandEditorPart() {
        when(toolingPartStack.getActivePart()).thenReturn(toolingPart);
        when(informationPartStack.getActivePart()).thenReturn(informationPart);
        when(editorPartStack.getActivePart()).thenReturn(editorPart);
        when(navigationPartStack.getActivePart()).thenReturn(navigationPart);

        workBenchPresenter.expandEditorPart();

        verify(toolingPartStack).hidePart(toolingPart);
        verify(informationPartStack).hidePart(informationPart);
        verify(editorPartStack, never()).hidePart(editorPart);
        verify(navigationPartStack).hidePart(navigationPart);
    }

    @Test
    public void shouldRestoreEditorPart() {
        when(toolingPartStack.getActivePart()).thenReturn(toolingPart);
        when(informationPartStack.getActivePart()).thenReturn(informationPart);
        when(editorPartStack.getActivePart()).thenReturn(editorPart);
        when(navigationPartStack.getActivePart()).thenReturn(navigationPart);

        when(toolingPartStack.containsPart(toolingPart)).thenReturn(true);
        when(informationPartStack.containsPart(informationPart)).thenReturn(true);
        when(editorPartStack.containsPart(editorPart)).thenReturn(true);
        when(navigationPartStack.containsPart(navigationPart)).thenReturn(true);

        workBenchPresenter.expandEditorPart();
        workBenchPresenter.restoreEditorPart();

        verify(toolingPartStack).setActivePart(toolingPart);
        verify(informationPartStack).setActivePart(informationPart);
        verify(editorPartStack, never()).setActivePart(editorPart);
        verify(navigationPartStack).setActivePart(navigationPart);
    }

    @Test
    public void shouldSetActivePart() {
        PartPresenter part = mock(PartPresenter.class);

        when(toolingPartStack.containsPart(part)).thenReturn(true);

        workBenchPresenter.setActivePart(part);

        verify(toolingPartStack).setActivePart(part);
    }

    @Test
    public void shouldOpenPart() {
        PartPresenter part = mock(PartPresenter.class);

        Constraints constraints = mock(Constraints.class);

        for (PartStackType partStackType : PartStackType.values()) {
            workBenchPresenter.openPart(part, partStackType, constraints);

            PartStack destPartStack = workBenchPresenter.partStacks.get(partStackType.toString());

            verify(destPartStack).addPart(part, constraints);
        }

        for (PartStackType partStackType : PartStackType.values()) {

            workBenchPresenter.openPart(part, partStackType);

            PartStack destPartStack = workBenchPresenter.partStacks.get(partStackType.toString());

            verify(destPartStack).addPart(part, null);
        }
    }

    @Test
    public void shouldPresenterGo() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        AcceptsOneWidget navigationPanel = mock(AcceptsOneWidget.class);
        AcceptsOneWidget editorPanel = mock(AcceptsOneWidget.class);
        AcceptsOneWidget toolPanel = mock(AcceptsOneWidget.class);
        AcceptsOneWidget informationPanel = mock(AcceptsOneWidget.class);

        when(view.getNavigationPanel()).thenReturn(navigationPanel);
        when(view.getEditorPanel()).thenReturn(editorPanel);
        when(view.getToolPanel()).thenReturn(toolPanel);
        when(view.getInformationPanel()).thenReturn(informationPanel);

        workBenchPresenter.go(container);

        verify(toolingPartStack).go(toolPanel);
        verify(informationPartStack).go(informationPanel);
        verify(editorPartStack).go(editorPanel);
        verify(navigationPartStack).go(navigationPanel);

        verify(container).setWidget(view);
    }
}