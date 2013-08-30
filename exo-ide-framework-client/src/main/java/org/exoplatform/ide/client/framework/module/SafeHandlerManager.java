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
package org.exoplatform.ide.client.framework.module;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;

/**
 * This handler manager helps to find the error during execution of the fireEvent(...) method.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SafeHandlerManager extends HandlerManager {

    /** Creates a new instance of this HandlerManager */
    public SafeHandlerManager() {
        super(null);
    }

    /** @see com.google.gwt.event.shared.HandlerManager#fireEvent(com.google.gwt.event.shared.GwtEvent) */
    @Override
    public void fireEvent(GwtEvent<?> event) {
        try {
            super.fireEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
