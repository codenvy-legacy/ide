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
package org.exoplatform.ide.extension.ssh.client.keymanager.ui;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;

/**
 * A widget that implements this interface provides registration for {@link ClickHandler} instances, for two action column in grid
 * component. Need to add buttons click handlers in presenter. <br />
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: HasSshKeyButtonsClickHandler May 18, 2011 3:57:06 PM evgen $
 */
public interface HasSshGrid<T> extends ListGridItem<T> {

    /**
     * Add handler to View public key button
     *
     * @param handler
     * @return {@link HandlerRegistration}
     */
    HandlerRegistration addViewButtonSelectionHandler(SelectionHandler<T> handler);

    /**
     * Add handler to Delete Key button
     *
     * @param handler
     * @return {@link HandlerRegistration}
     */
    HandlerRegistration addDeleteButtonSelectionHandler(SelectionHandler<T> handler);

}
