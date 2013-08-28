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
package org.exoplatform.ide.client.framework.ui.api;

import com.google.gwt.user.client.ui.IsWidget;

import org.exoplatform.ide.client.framework.ui.api.event.HasClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewVisibilityChangedHandler;

import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Perspective extends HasViewVisibilityChangedHandler, HasViewOpenedHandler, HasViewClosedHandler,
                                     HasClosingViewHandler, IsWidget {

    /**
     * Opens View.
     *
     * @param view
     */
    void openView(View view);

    /**
     * Closes View.
     *
     * @param viewId
     */
    void closeView(String viewId);

    /**
     * Returns map of opened views.
     *
     * @return
     */
    public Map<String, View> getViews();

    /**
     * Returns map of opened panels.
     *
     * @return
     */
    public Map<String, Panel> getPanels();

}
