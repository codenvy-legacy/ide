/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.api.ui.workspace;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.selection.Selection;
import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Part is a main UI block of the IDE.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface PartPresenter extends Presenter {
    /** The property id for <code>getTitle</code>, <code>getTitleImage</code> and <code>getTitleToolTip</code>. */
    int TITLE_PROPERTY     = 0x001;
    /** The property id for <code>getSelection</code>. */
    int SELECTION_PROPERTY = 0x002;

    /** @return Title of the Part */
    @NotNull
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
     * This method is called when part is going to be closed. Part itself can deny blocking, by returning false, i.e. when document is
     * being edited and accidentally close button pressed.
     *
     * @return allow close
     */
    boolean onClose();

    /**
     * Adds a listener for changes to properties of this part. Has no effect if an identical listener is already registered.
     *
     * @param listener
     *         a property listener
     */
    void addPropertyListener(@NotNull PropertyListener listener);

    /** @return The {@link Selection} of this Part. */
    @NotNull
    Selection<?> getSelection();

    /**
     * Removes the given property listener from this part. Has no effect if an identical listener is not registered.
     *
     * @param listener
     *         a property listener
     */
    void removePropertyListener(@NotNull PropertyListener listener);
}