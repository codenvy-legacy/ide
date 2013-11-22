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

package org.exoplatform.ide.client.framework.application;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Open resource event. May used by factory(in this case method that call this event should pass {@link
 * org.exoplatform.ide.client.project.resource.OpenResourcePresenter.ResourceSelectedCallback} callback into constructor of event).
 */
public class OpenResourceEvent extends GwtEvent<OpenResourceHandler> {

    /** Type, used to register the event. */
    public static final GwtEvent.Type<OpenResourceHandler> TYPE = new GwtEvent.Type<OpenResourceHandler>();

    /** Callback to process selected item in resources list. */
    private ResourceSelectedCallback resourceSelectedCallback;

    /** Construct event. */
    public OpenResourceEvent() {
    }

    /**
     * Construct event with setted callback. Used for factory.
     *
     * @param callback
     *         {@link OpenResourcePresenter.ResourceSelectedCallback} callback.
     */
    public OpenResourceEvent(ResourceSelectedCallback resourceSelectedCallback) {
        this.resourceSelectedCallback = resourceSelectedCallback;
    }

    /** {@inheritDoc} */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<OpenResourceHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(OpenResourceHandler handler) {
        handler.onOpenResource(this);
    }

    /**
     * Get selected item callback.
     *
     * @return {@link OpenResourcePresenter.ResourceSelectedCallback} callback.
     */
    public ResourceSelectedCallback getCallback() {
        return resourceSelectedCallback;
    }
    
}
