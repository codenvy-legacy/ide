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
import static org.mockito.Mockito.mock;
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

    private EditorPartStackPresenter presenter;

    @Before
    public void setUp() {
        presenter = new EditorPartStackPresenter(view, eventBus, partStackEventHandler, listOpenedFilesPresenter);
    }

    @Test
    public void closeAllTabsTest() throws InvocationTargetException, IllegalAccessException {
        //create three tabs
        doReturn(item).when(view).addTabButton(null, null, null, null, true);
        doReturn(item2).when(view).addTabButton(null, null, null, null, true);
        doReturn(item3).when(view).addTabButton(null, null, null, null, true);

        presenter.addPart(part1);
        presenter.addPart(part2);
        presenter.addPart(part3);
        stopSchedulers();

        assertEquals(presenter.getActivePart(), part3);
        assertTrue(presenter.getNumberOfParts() == 3);

        presenter.closeAllTabs(asyncCallback);

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

        //check closing second tab
        reset(view);
        ArgumentCaptor<AsyncCallback> asyncRequestCallbackCaptor2 = ArgumentCaptor.forClass(AsyncCallback.class);

        verify(part2).onClose(asyncRequestCallbackCaptor2.capture());

        AsyncCallback callback2 = asyncRequestCallbackCaptor2.getValue();

        Method onSuccess2 = GwtReflectionUtils.getMethod(callback2.getClass(), "onSuccess");
        onSuccess2.invoke(callback2, (Void) null);

        verify(view).removeTab(anyInt());

        assertTrue(presenter.getNumberOfParts() == 1);

        verify(part2).removePropertyListener(any(PropertyListener.class));

        verify(partStackEventHandler, never()).onActivePartChanged(part2);

        //check closing third tab
        reset(view);
        reset(partStackEventHandler);
        ArgumentCaptor<AsyncCallback> asyncRequestCallbackCaptor3 = ArgumentCaptor.forClass(AsyncCallback.class);

        verify(part3).onClose(asyncRequestCallbackCaptor3.capture());

        AsyncCallback callback3 = asyncRequestCallbackCaptor3.getValue();

        Method onSuccess3 = GwtReflectionUtils.getMethod(callback3.getClass(), "onSuccess");
        onSuccess3.invoke(callback3, (Void) null);

        verify(view).removeTab(anyInt());

        assertTrue(presenter.getNumberOfParts() == 0);

        verify(part3).removePropertyListener(any(PropertyListener.class));

        verify(partStackEventHandler).onActivePartChanged(any(EditorPartPresenter.class));

        verify(asyncCallback, times(1)).onSuccess(null);
    }

    @Test
    public void closeAllTabsWithoutCloseProjectTest() throws Throwable {
        //create three tabs
        doReturn(item).when(view).addTabButton(null, null, null, null, true);
        doReturn(item2).when(view).addTabButton(null, null, null, null, true);
        doReturn(item3).when(view).addTabButton(null, null, null, null, true);

        presenter.addPart(part1);
        presenter.addPart(part2);
        presenter.addPart(part3);
        stopSchedulers();

        assertEquals(presenter.getActivePart(), part3);
        assertTrue(presenter.getNumberOfParts() == 3);

        presenter.closeAllTabs(null);

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

        //check closing second tab
        reset(view);
        ArgumentCaptor<AsyncCallback> asyncRequestCallbackCaptor2 = ArgumentCaptor.forClass(AsyncCallback.class);

        verify(part2).onClose(asyncRequestCallbackCaptor2.capture());

        AsyncCallback callback2 = asyncRequestCallbackCaptor2.getValue();

        Method onSuccess2 = GwtReflectionUtils.getMethod(callback2.getClass(), "onSuccess");
        onSuccess2.invoke(callback2, (Void) null);

        verify(view).removeTab(anyInt());

        assertTrue(presenter.getNumberOfParts() == 1);

        verify(part2).removePropertyListener(any(PropertyListener.class));

        verify(partStackEventHandler, never()).onActivePartChanged(part2);

        //check closing third tab
        reset(view);
        reset(partStackEventHandler);
        ArgumentCaptor<AsyncCallback> asyncRequestCallbackCaptor3 = ArgumentCaptor.forClass(AsyncCallback.class);

        verify(part3).onClose(asyncRequestCallbackCaptor3.capture());

        AsyncCallback callback3 = asyncRequestCallbackCaptor3.getValue();

        Method onSuccess3 = GwtReflectionUtils.getMethod(callback3.getClass(), "onSuccess");
        onSuccess3.invoke(callback3, (Void) null);

        verify(view).removeTab(anyInt());

        assertTrue(presenter.getNumberOfParts() == 0);

        verify(part3).removePropertyListener(any(PropertyListener.class));

        verify(partStackEventHandler).onActivePartChanged(any(EditorPartPresenter.class));

        verify(asyncCallback, never()).onSuccess(null);
    }

    @Test
    public void setNewActivePartTest() {
        doReturn(item).when(view).addTabButton(null, null, null, null, true);
        presenter.addPart(part1);
        stopSchedulers();

        presenter.setActivePart(part1);

        TestCase.assertEquals(presenter.getActivePart(), part1);

        verify(view).setActiveTab(0);

        assertEquals(presenter.getActivePart(), part1);
    }

    @Test
    public void addFirstPartWithoutIconTest() {
        doReturn(item).when(view).addTabButton(null, null, null, null, true);

        int amountOfPartsBefore = presenter.getNumberOfParts();

        presenter.addPart(part1);
        stopSchedulers();

        int amountOfPartsAfter = presenter.getNumberOfParts();

        assertTrue(++amountOfPartsBefore == amountOfPartsAfter);

        verify(part1).addPropertyListener(any(PropertyListener.class));

        verify(part1).getTitleSVGImage();

        verify(view).addTabButton(null, null, null, null, true);

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
        SVGResource titleSVGResource = mock(SVGResource.class);
        SVGImage svgImage = mock(SVGImage.class);
        OMSVGSVGElement svgElem = mock(OMSVGSVGElement.class);

        doReturn(svgElem).when(titleSVGResource).getSvg();
        doReturn(GWT.create(Node.class)).when(svgElem).getElement();
        doReturn(titleSVGResource).when(part1).getTitleSVGImage();
        doReturn(svgImage).when(part1).decorateIcon(any(SVGImage.class));
        doReturn(item).when(view).addTabButton(svgImage, null, null, null, true);

        presenter.addPart(part1);
        stopSchedulers();

        verify(part1).getTitleSVGImage();

        verify(part1).decorateIcon(any(SVGImage.class));
    }

    @Test
    public void addTabWhichIsAlreadyExistTest() {
        doReturn(item).when(view).addTabButton(null, null, null, null, true);

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
        verify(view, never()).addTabButton(null, null, null, null, true);

        verify(part1, never()).go(any(AcceptsOneWidget.class));

        assertEquals(presenter.getActivePart(), part1);
    }

    private void stopSchedulers() {
        getBrowserSimulator().fireLoopEnd();
    }
}