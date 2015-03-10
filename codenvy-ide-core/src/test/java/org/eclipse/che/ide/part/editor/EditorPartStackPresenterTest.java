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
package org.eclipse.che.ide.part.editor;

import junit.framework.TestCase;

import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.PartStackView;
import org.eclipse.che.ide.api.parts.PropertyListener;

import org.eclipse.che.ide.part.editor.EditorPartStackPresenter;
import org.eclipse.che.ide.part.editor.EditorPartStackView;
import org.eclipse.che.ide.texteditor.openedfiles.ListOpenedFilesPresenter;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
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
@RunWith(GwtMockitoTestRunner.class)
public class EditorPartStackPresenterTest {

    @Mock
    private PartStackUIResources partStackUIResources;
    @Mock
    private Resources            resources;

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
    private EditorPartStackView                            view;
    @Mock
    private EventBus                                       eventBus;
    @Mock
    private EditorPartStackPresenter.PartStackEventHandler partStackEventHandler;
    @Mock
    private ListOpenedFilesPresenter                       listOpenedFilesPresenter;

    @Mock
    private PartStackView.ActionDelegate actionDelegate;
    @Mock
    private AsyncCallback                asyncCallback;
    @Mock
    private SVGImage                     svgImage;
    @Mock
    private SVGResource                  titleSVGResource;
    @Mock
    private OMSVGSVGElement              svgElem;

    private String partTitle1 = "someTitle";
    private String toolTip1   = "someToolTipe";

    private EditorPartStackPresenter presenter;

    @Before
    public void setUp() {
        presenter = new EditorPartStackPresenter(view, eventBus, partStackEventHandler, listOpenedFilesPresenter);

        doReturn(svgElem).when(titleSVGResource).getSvg();
        doReturn(Mockito.mock(Element.class)).when(svgElem).getElement();

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

        doReturn(item).when(view).addTab(svgImage, partTitle1, toolTip1, null, true);
    }

    @Test
    public void closeTabTest() {
        presenter.addPart(part1);
        presenter.addPart(part2);
        presenter.addPart(part3);

        assertEquals(presenter.getActivePart(), part3);
        assertTrue(presenter.getNumberOfParts() == 3);

        presenter.close(part1);

        //check closing first tab
        ArgumentCaptor<AsyncCallback> asyncRequestCallbackCaptor = ArgumentCaptor.forClass(AsyncCallback.class);

        verify(part1).onClose(asyncRequestCallbackCaptor.capture());

        AsyncCallback callback1 = asyncRequestCallbackCaptor.getValue();
        callback1.onSuccess(null);

        verify(view).removeTab(anyInt());

        assertTrue(presenter.getNumberOfParts() == 2);

        verify(part1).removePropertyListener(any(PropertyListener.class));

//        verify(partStackEventHandler, never()).onActivePartChanged(part2);
    }

    @Test
    public void closeAllTabsTest() throws Throwable {
        presenter.addPart(part1);
        presenter.addPart(part2);
        presenter.addPart(part3);

        assertEquals(presenter.getActivePart(), part3);
        assertTrue(presenter.getNumberOfParts() == 3);

        presenter.close(part1);

        //check closing first tab
        ArgumentCaptor<AsyncCallback> asyncRequestCallbackCaptor = ArgumentCaptor.forClass(AsyncCallback.class);

        verify(part1).onClose(asyncRequestCallbackCaptor.capture());

        AsyncCallback callback1 = asyncRequestCallbackCaptor.getValue();

        callback1.onSuccess(null);

        verify(view).removeTab(anyInt());

        assertTrue(presenter.getNumberOfParts() == 2);

        verify(part1).removePropertyListener(any(PropertyListener.class));

//        verify(partStackEventHandler, never()).onActivePartChanged(part2);
    }

    @Test
    public void setNewActivePartTest() {
        presenter.addPart(part1);

        presenter.setActivePart(part1);

        TestCase.assertEquals(presenter.getActivePart(), part1);

        verify(view).setActiveTab(0);

        assertEquals(presenter.getActivePart(), part1);
    }

    @Test
    public void addFirstPartWithoutIconTest() {
        doReturn(null).when(part1).decorateIcon(any(SVGImage.class));

        doReturn(item).when(view).addTab(null, partTitle1, toolTip1, null, true);

        int amountOfPartsBefore = presenter.getNumberOfParts();

        presenter.addPart(part1);

        int amountOfPartsAfter = presenter.getNumberOfParts();

        assertTrue(++amountOfPartsBefore == amountOfPartsAfter);

        verify(part1).addPropertyListener(any(PropertyListener.class));

        verify(part1).getTitleSVGImage();

        verify(view).addTab(null, partTitle1, toolTip1, null, true);

//        verify(item).addClickHandler(any(ClickHandler.class));
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

        assertTrue(presenter.getNumberOfParts() == 2);

        verify(view).setActiveTab(eq(0));

        verify(partStackEventHandler, times(1)).onRequestFocus(presenter);
        verify(part1, never()).addPropertyListener(any(PropertyListener.class));
        verify(view, never()).addTab(svgImage, partTitle1, toolTip1, null, true);

        verify(part1, never()).go(any(AcceptsOneWidget.class));

        assertEquals(presenter.getActivePart(), part1);
    }

}
