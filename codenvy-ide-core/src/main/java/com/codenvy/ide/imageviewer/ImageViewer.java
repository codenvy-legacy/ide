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
package com.codenvy.ide.imageviewer;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.AbstractEditorPresenter;
import com.codenvy.ide.api.editor.EditorInput;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

/**
 * Is used for displaying images in editor area.
 *
 * @author Ann Shumilova
 */
public class ImageViewer extends AbstractEditorPresenter {

    private Resources resources;

    /**
     *
     */
    @Inject
    public ImageViewer(Resources resources) {
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public void doSave() {
    }

    @Override
    public void doSave(AsyncCallback<EditorInput> callback) {

    }

    /** {@inheritDoc} */
    @Override
    public void doSaveAs() {
    }

    /** {@inheritDoc} */
    @Override
    public void activate() {
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return input.getName();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return input.getImageResource();
    }

    /** {@inheritDoc} */
    @Override
    public SVGResource getTitleSVGImage() {
        return input.getSVGResource();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        VerticalPanel panel = new VerticalPanel();
        panel.setSize("100%", "100%");
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.add(getImage());
        ScrollPanel scrollable = new ScrollPanel(panel);
        scrollable.getElement().getFirstChildElement().getStyle().setHeight(100, Unit.PCT);
        container.setWidget(scrollable);
    }

    /**
     * Image to display file with image type.
     *
     * @return {@link Image}
     */
    private Image getImage() {
        Link contentLink = null;
        for (Link link : input.getFile().getLinks()) {
            if ("get content".equals(link.getRel())) {
                contentLink = link;
            }
        }
        Image image = (contentLink != null) ? new Image(contentLink.getHref()) : new Image();
        image.setStyleName(resources.workspaceEditorCss().imageViewer());
        return image;
    }

    /** {@inheritDoc} */
    @Override
    protected void initializeEditor() {
    }

}
