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
package org.eclipse.che.ide.api.parts;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.util.ListenerManager;
import org.eclipse.che.ide.util.ListenerManager.Dispatcher;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Abstract base implementation of all PartPresenter
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @author Stéphane Daviet
 * @author Valeriy Svydenko
 */
public abstract class AbstractPartPresenter implements PartPresenter {
    private ListenerManager<PropertyListener> manager;
    private Selection<?>                      selection;

    public AbstractPartPresenter() {
        manager = ListenerManager.create();
    }

    /** {@inheritDoc} */
    @Override
    public void onClose(@Nonnull AsyncCallback<Void> callback) {
        callback.onSuccess(null);
    }

    /** {@inheritDoc} */
    @Override
    public void onOpen() {
    }

    /** {@inheritDoc} */
    @Override
    public void addPropertyListener(@Nonnull PropertyListener listener) {
        manager.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removePropertyListener(@Nonnull PropertyListener listener) {
        manager.remove(listener);
    }

    /**
     * Fires a property changed event.
     *
     * @param propId
     *         the id of the property that changed
     */
    protected void firePropertyChange(final int propId) {
        manager.dispatch(new Dispatcher<PropertyListener>() {
            @Override
            public void dispatch(PropertyListener listener) {
                listener.propertyChanged(AbstractPartPresenter.this, propId);
            }
        });
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public Selection<?> getSelection() {
        return this.selection;
    }

    /**
     * Sets the Selection of the Part. It later can be accessible using {@link AbstractPartPresenter#getSelection()}
     *
     * @param selection
     *         instance of Selection
     */
    public void setSelection(@Nonnull Selection<?> selection) {
        this.selection = selection;
        firePropertyChange(SELECTION_PROPERTY);
    }

    /** {@inheritDoc} */
    @Override
    public int getSize() {
        return 285;
    }

    /** {@inheritDoc} */
    @Override
    public IsWidget getTitleWidget() {
        return null;
    }

    @Nullable
    @Override
    public SVGResource getTitleSVGImage() {
        return null;
    }

    @Nullable
    @Override
    public SVGImage decorateIcon(SVGImage svgImage) {
        return svgImage;
    }
}
