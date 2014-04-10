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

import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.google.gwt.user.client.ui.IsWidget;

import javax.validation.constraints.NotNull;


/**
 * Abstract base implementation of all PartPresenter
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public abstract class AbstractPartPresenter implements PartPresenter {
    private ListenerManager<PropertyListener> manager;
    private Selection<?>                      selection;

    public AbstractPartPresenter() {
        manager = ListenerManager.create();
    }

    /** {@inheritDoc} */
    @Override
    public boolean onClose() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void onOpen() {
    }

    /** {@inheritDoc} */
    @Override
    public void addPropertyListener(@NotNull PropertyListener listener) {
        manager.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removePropertyListener(@NotNull PropertyListener listener) {
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
    public void setSelection(@NotNull Selection<?> selection) {
        this.selection = selection;
        firePropertyChange(SELECTION_PROPERTY);
    }

    /** {@inheritDoc} */
    @Override
    public int getSize() {
        return 0;
    }
    
    /** {@inheritDoc} */
    @Override
    public IsWidget getTitleWidget() {
        return null;
    }
}
