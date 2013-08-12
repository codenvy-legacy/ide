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
package com.codenvy.ide.api.selection;

import com.codenvy.ide.api.event.SelectionChangedEvent;
import com.codenvy.ide.api.extension.SDK;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Selection API allows to provide a way of data-based communication, when Parts provide a static
 * access to the data selected in active Part.
 * In order to listen to dynamic Selection changes, please subscribe to {@link SelectionChangedEvent}
 * on {@link EventBus}.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@SDK(title = "ide.api.ui.selection")
public interface SelectionAgent {

    /**
     * Provides a way of getting current app-wide Selection.
     *
     * @return
     */
    public Selection<?> getSelection();

}