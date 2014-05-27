/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.image.viewer;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.AbstractEditorPresenter;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
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
