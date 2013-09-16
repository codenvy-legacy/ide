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
package org.exoplatform.gwtframework.ui.client.component.event;

import com.google.gwt.event.logical.shared.CloseEvent;


/**
 * Implementation of {@link CloseEvent}.
 *
 * @param <T>
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jan 17, 2011 4:45:49 PM anya $
 */
public class CloseEventImpl<T> extends CloseEvent<T> {
    /**
     * @param target
     *         closed target
     */
    public CloseEventImpl(T target) {
        super(target, false);
    }
}
