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
package com.codenvy.ide.api.parts;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.selection.Selection;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Part is a main UI block of the IDE.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 * @author Stéphane Daviet
 */
public interface PartPresenter extends Presenter {
    /** The property id for <code>getTitle</code>, <code>getTitleImage</code> and <code>getTitleToolTip</code>. */
    int TITLE_PROPERTY     = 0x001;
    /** The property id for <code>getSelection</code>. */
    int SELECTION_PROPERTY = 0x002;

    /** @return Title of the Part */
    @Nonnull
    String getTitle();

    /**
     * Returns the title image of this part.  If this value changes the part must fire a property listener event with
     * <code>PROP_TITLE</code>.
     * <p/>
     * The title image is usually used to populate the title bar of this part's visual container.
     *
     * @return the title image
     */
    @Nullable
    ImageResource getTitleImage();

    /**
     * Returns the title SVG image resource of this part.  If this value changes the part must fire a property listener event with
     * <code>PROP_TITLE</code>.
     * <p/>
     * The title image is usually used to populate the title bar of this part's visual container.
     *
     * @return the title SVG image resource
     */
    @Nullable
    SVGResource getTitleSVGImage();

    /**
     * Decorate the title SVG image of this part. A convenient method to be able to size, color or perform any CSS related styling
     * operation.
     *
     * @param svgImage
     *         the title SVG image
     * @return the image decorated, could be or not the same reference, no matter.
     */
    @Nullable
    SVGImage decorateIcon(SVGImage svgImage);

    /**
     * Returns the widget to be displayed in the title of this part. If this value changes the part must fire a property listener event
     * with
     * <code>PROP_TITLE</code>.
     * <p/>
     * The title widget is usually used to populate the title bar of this part's visual container.
     *
     * @return the title widget
     */
    @Nullable
    IsWidget getTitleWidget();

    /**
     * Returns the title tool tip text of this part.
     * An empty string result indicates no tool tip.
     * If this value changes the part must fire a property listener event with <code>PROP_TITLE</code>.
     * <p>
     * The tool tip text is used to populate the title bar of this part's visual container.
     * </p>
     *
     * @return the part title tool tip (not <code>null</code>)
     */
    @Nullable
    String getTitleToolTip();

    /**
     * Return size of part. If current part is vertical panel then size is height. If current part is horizontal panel then size is width.
     *
     * @return size of part
     */
    int getSize();

    /**
     * This method is called when Part is opened.
     * Note: this method is NOT called when part gets focused. It is called when new tab in PartStack created.
     */
    void onOpen();

    /**
     * This method is called when part is going to be closed. Part itself can deny blocking, by calling onFailure() on callback, i.e. when document is
     * being edited and accidentally close button pressed.
     * @param callback
     */
    void onClose(@Nonnull AsyncCallback<Void> callback);

    /**
     * Adds a listener for changes to properties of this part. Has no effect if an identical listener is already registered.
     *
     * @param listener
     *         a property listener
     */
    void addPropertyListener(@Nonnull PropertyListener listener);

    /** @return The {@link Selection} of this Part. */
    @Nonnull
    Selection<?> getSelection();

    /**
     * Removes the given property listener from this part. Has no effect if an identical listener is not registered.
     *
     * @param listener
     *         a property listener
     */
    void removePropertyListener(@Nonnull PropertyListener listener);
}