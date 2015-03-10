/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.workspace;

import org.eclipse.che.ide.api.constraints.Constraints;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.EditorPartStack;
import org.eclipse.che.ide.api.parts.OutlinePart;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStack;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.ProjectExplorerPart;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
@RunWith(GwtMockitoTestRunner.class)
public class TestWorkBenchPresenter {

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

    private static PartStack toolingPartStack;
    private static PartStack informationPartStack;
    private static PartStack editorPartStack;
    private static PartStack navigationPartStack;

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
    public void shouldRestoreEditorPartWithHiddenParts() {
        when(toolingPartStack.getActivePart()).thenReturn(toolingPart);
        when(informationPartStack.getActivePart()).thenReturn(informationPart);
        when(editorPartStack.getActivePart()).thenReturn(editorPart);
        when(navigationPartStack.getActivePart()).thenReturn(navigationPart);

        workBenchPresenter.expandEditorPart();

        reset(toolingPartStack);
        reset(informationPartStack);
        reset(editorPartStack);
        reset(navigationPartStack);

        when(toolingPartStack.getActivePart()).thenReturn(null);
        when(informationPartStack.getActivePart()).thenReturn(null);
        when(editorPartStack.getActivePart()).thenReturn(null);
        when(navigationPartStack.getActivePart()).thenReturn(null);

        when(toolingPartStack.containsPart(toolingPart)).thenReturn(true);
        when(informationPartStack.containsPart(informationPart)).thenReturn(true);
        when(editorPartStack.containsPart(editorPart)).thenReturn(true);
        when(navigationPartStack.containsPart(navigationPart)).thenReturn(true);

        workBenchPresenter.restoreEditorPart();

        verify(toolingPartStack).setActivePart(toolingPart);
        verify(informationPartStack).setActivePart(informationPart);
        verify(editorPartStack, never()).setActivePart(editorPart);
        verify(navigationPartStack).setActivePart(navigationPart);
    }

    @Test
    public void shouldNotRestoreEditorPartWithActiveParts() {
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

        verify(toolingPartStack, never()).setActivePart(toolingPart);
        verify(informationPartStack, never()).setActivePart(informationPart);
        verify(editorPartStack, never()).setActivePart(editorPart);
        verify(navigationPartStack, never()).setActivePart(navigationPart);
    }

    @Test
    public void shouldSetActivePart() {
        PartPresenter part = mock(PartPresenter.class);

        when(toolingPartStack.containsPart(part)).thenReturn(true);

        workBenchPresenter.setActivePart(part);

        verify(toolingPartStack).setActivePart(part);
    }

    @Test
    public void shouldNotSetUnexistingActivePart() {
        PartPresenter part = mock(PartPresenter.class);

        when(toolingPartStack.containsPart(part)).thenReturn(false);

        workBenchPresenter.setActivePart(part);

        verify(toolingPartStack, never()).setActivePart(part);
    }

    @Test
    public void shouldNavigationContainerOpenPart() {
        PartPresenter part = mock(PartPresenter.class);
        Constraints constraints = mock(Constraints.class);

        workBenchPresenter.openPart(part, PartStackType.NAVIGATION, constraints);

        verify(navigationPartStack).addPart(part, constraints);


        workBenchPresenter.openPart(part, PartStackType.NAVIGATION);

        verify(navigationPartStack).addPart(part, null);
    }

    @Test
    public void shouldEditorContainerOpenPart() {
        PartPresenter part = mock(PartPresenter.class);
        Constraints constraints = mock(Constraints.class);

        workBenchPresenter.openPart(part, PartStackType.EDITING, constraints);

        verify(editorPartStack).addPart(part, constraints);


        workBenchPresenter.openPart(part, PartStackType.EDITING);

        verify(editorPartStack).addPart(part, null);
    }

    @Test
    public void shouldToolingContainerOpenPart() {
        PartPresenter part = mock(PartPresenter.class);
        Constraints constraints = mock(Constraints.class);

        workBenchPresenter.openPart(part, PartStackType.TOOLING, constraints);

        verify(toolingPartStack).addPart(part, constraints);


        workBenchPresenter.openPart(part, PartStackType.TOOLING);

        verify(toolingPartStack).addPart(part, null);
    }

    @Test
    public void shouldInformationContainerOpenPart() {
        PartPresenter part = mock(PartPresenter.class);
        Constraints constraints = mock(Constraints.class);

        workBenchPresenter.openPart(part, PartStackType.INFORMATION, constraints);

        verify(informationPartStack).addPart(part, constraints);


        workBenchPresenter.openPart(part, PartStackType.INFORMATION);

        verify(informationPartStack).addPart(part, null);
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