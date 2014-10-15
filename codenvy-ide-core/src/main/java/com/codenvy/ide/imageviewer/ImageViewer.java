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
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.AbstractEditorPresenter;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

/**
 * Is used for displaying images in editor area.
 *
 * @author Ann Shumilova
 */
public class ImageViewer extends AbstractEditorPresenter {

    private Resources resources;
    private CoreLocalizationConstant constant;

    /**
     *
     */
    @Inject
    public ImageViewer(Resources resources,
                       CoreLocalizationConstant constant) {
        this.resources = resources;
        this.constant = constant;
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
    @Nonnull
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
    public void onClose(@Nonnull final AsyncCallback<Void> callback) {
        if (isDirty()) {
            Ask ask = new Ask(constant.askWindowCloseTitle(), constant.messagesSaveChanges(getEditorInput().getName()), new AskHandler() {
                @Override
                public void onOk() {
                    doSave();
                    handleClose();
                    callback.onSuccess(null);
                }

                @Override
                public void onCancel() {
                    handleClose();
                    callback.onSuccess(null);
                }
            });
            ask.show();
        } else {
            handleClose();
            callback.onSuccess(null);
        }
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
        for (Link link : input.getFile().getData().getLinks()) {
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
