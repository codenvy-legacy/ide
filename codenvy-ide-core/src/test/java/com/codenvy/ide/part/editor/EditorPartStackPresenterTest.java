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
package com.codenvy.ide.part.editor;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.PartStackView;
import com.codenvy.ide.api.parts.PropertyListener;
import com.codenvy.ide.texteditor.openedfiles.ListOpenedFilesPresenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Alexander Andrienko
 */
@GwtModule("com.codenvy.ide.Core")
public class EditorPartStackPresenterTest extends GwtTestWithMockito {

    @Mock
    private PartStackUIResources partStackUIResources;
    @Mock
    private Resources resources;

    @Mock
    private EditorPartPresenter part1;
    @Mock
    private EditorPartPresenter part2;
    @Mock
    private EditorPartPresenter part3;
    @Mock
    private List<PartPresenter> parts;

    @Mock
    private PartStackView.TabItem item;
    @Mock
    private PartStackView.TabItem item2;
    @Mock
    private PartStackView.TabItem item3;

    /*
     * Variables for constructor
     */
    @Mock
    private EditorPartStackView view;
    @Mock
    private EventBus eventBus;
    @Mock
    private EditorPartStackPresenter.PartStackEventHandler partStackEventHandler;
    @Mock
    private ListOpenedFilesPresenter listOpenedFilesPresenter;

    @Mock
    private PartStackView.ActionDelegate actionDelegate;
    @Mock
    private AsyncCallback asyncCallback;
    @Mock
    private SVGImage svgImage;
    @Mock
    private SVGResource titleSVGResource;
    @Mock
    private OMSVGSVGElement svgElem;

    private String partTitle1 = "someTitle";
    private String toolTip1 = "someToolTipe";

    private EditorPartStackPresenter presenter;

    @Before
    public void setUp() {
        presenter = new EditorPartStackPresenter(view, eventBus, partStackEventHandler, listOpenedFilesPresenter);

        doReturn(svgElem).when(titleSVGResource).getSvg();
        doReturn(GWT.create(Node.class)).when(svgElem).getElement();

        //define behavior part1
        doReturn(titleSVGResource).when(part1).getTitleSVGImage();
        doReturn(svgImage).when(part1).decorateIcon(any(SVGImage.class));
        doReturn(partTitle1).when(part1).getTitle();
        doReturn(toolTip1).when(part1).getTitleToolTip();

        //define behavior part2
        doReturn(titleSVGResource).when(part2).getTitleSVGImage();
        doReturn(svgImage).when(part2).decorateIcon(any(SVGImage.class));
        doReturn(partTitle1).when(part2).getTitle();
        doReturn(toolTip1).when(part2).getTitleToolTip();

        //define behavior part3
        doReturn(titleSVGResource).when(part3).getTitleSVGImage();
        doReturn(svgImage).when(part3).decorateIcon(any(SVGImage.class));
        doReturn(partTitle1).when(part3).getTitle();
        doReturn(toolTip1).when(part3).getTitleToolTip();

        doReturn(item).when(view).addTabButton(svgImage, partTitle1, toolTip1, null, true);
    }

    @Test
    public void closeTabTest() throws InvocationTargetException, IllegalAccessException {
        presenter.addPart(part1);
        presenter.addPart(part2);
        presenter.addPart(part3);
        stopSchedulers();

        assertEquals(presenter.getActivePart(), part3);
        assertTrue(presenter.getNumberOfParts() == 3);

        presenter.close(part1);

        //check closing first tab
        ArgumentCaptor<AsyncCallback> asyncRequestCallbackCaptor = ArgumentCaptor.forClass(AsyncCallback.class);

        verify(part1).onClose(asyncRequestCallbackCaptor.capture());

        AsyncCallback callback1 = asyncRequestCallbackCaptor.getValue();

        Method onSuccess = GwtReflectionUtils.getMethod(callback1.getClass(), "onSuccess");
        onSuccess.invoke(callback1, (Void) null);

        verify(view).removeTab(anyInt());

        assertTrue(presenter.getNumberOfParts() == 2);

        verify(part1).removePropertyListener(any(PropertyListener.class));

        verify(partStackEventHandler, never()).onActivePartChanged(part2);
        stopSchedulers();
    }

    @Test
    public void closeAllTabsTest() throws Throwable {
        presenter.addPart(part1);
        presenter.addPart(part2);
        presenter.addPart(part3);
        stopSchedulers();

        assertEquals(presenter.getActivePart(), part3);
        assertTrue(presenter.getNumberOfParts() == 3);

        presenter.close(part1);

        //check closing first tab
        ArgumentCaptor<AsyncCallback> asyncRequestCallbackCaptor = ArgumentCaptor.forClass(AsyncCallback.class);

        verify(part1).onClose(asyncRequestCallbackCaptor.capture());

        AsyncCallback callback1 = asyncRequestCallbackCaptor.getValue();

        Method onSuccess = GwtReflectionUtils.getMethod(callback1.getClass(), "onSuccess");
        onSuccess.invoke(callback1, (Void) null);

        verify(view).removeTab(anyInt());

        assertTrue(presenter.getNumberOfParts() == 2);

        verify(part1).removePropertyListener(any(PropertyListener.class));

        verify(partStackEventHandler, never()).onActivePartChanged(part2);
        stopSchedulers();
    }

    @Test
    public void setNewActivePartTest() {
        presenter.addPart(part1);
        stopSchedulers();

        presenter.setActivePart(part1);

        TestCase.assertEquals(presenter.getActivePart(), part1);

        verify(view).setActiveTab(0);

        assertEquals(presenter.getActivePart(), part1);
    }

    @Test
    public void addFirstPartWithoutIconTest() {
        doReturn(null).when(part1).decorateIcon(any(SVGImage.class));

        doReturn(item).when(view).addTabButton(null, partTitle1, toolTip1, null, true);

        int amountOfPartsBefore = presenter.getNumberOfParts();

        presenter.addPart(part1);
        stopSchedulers();

        int amountOfPartsAfter = presenter.getNumberOfParts();

        assertTrue(++amountOfPartsBefore == amountOfPartsAfter);

        verify(part1).addPropertyListener(any(PropertyListener.class));

        verify(part1).getTitleSVGImage();

        verify(view).addTabButton(null, partTitle1, toolTip1, null, true);

        verify(item).addClickHandler(any(ClickHandler.class));
        verify(item).addCloseHandler(any(CloseHandler.class));

        verify(part1).go(any(AcceptsOneWidget.class));

        assertEquals(presenter.getActivePart(), part1);
        //because this is first tab
        verify(view).setActiveTab(eq(0));

        verify(partStackEventHandler).onRequestFocus(eq(presenter));
    }

    @Test
    public void addFirstPartWithIconTest() {
        presenter.addPart(part1);
        stopSchedulers();

        verify(part1).getTitleSVGImage();

        verify(part1).decorateIcon(any(SVGImage.class));
    }

    @Test
    public void addTabWhichIsAlreadyExistTest() {
        presenter.addPart(part1);
        presenter.addPart(part2);

        reset(partStackEventHandler);
        reset(view);
        reset(part1);

        presenter.addPart(part1);
        stopSchedulers();

        assertTrue(presenter.getNumberOfParts() == 2);

        verify(view).setActiveTab(eq(0));

        verify(partStackEventHandler, times(1)).onRequestFocus(presenter);
        verify(part1, never()).addPropertyListener(any(PropertyListener.class));
        verify(view, never()).addTabButton(svgImage, partTitle1, toolTip1, null, true);

        verify(part1, never()).go(any(AcceptsOneWidget.class));

        assertEquals(presenter.getActivePart(), part1);
    }

    private void stopSchedulers() {
        getBrowserSimulator().fireLoopEnd();
    }
}
