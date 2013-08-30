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

import java.util.List;
import java.util.Map;

/**
 * This interface describes a visual component that can display views in tabs or in any other form.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Panel extends HasViews, IsWidget {

    /**
     * Returns the ID of this panel.
     *
     * @return id of this panel
     */
    String getPanelId();

    /**
     * Adds a view to the panel.
     *
     * @see org.exoplatform.ide.client.framework.ui.api.HasViews#addView(org.exoplatform.ide.client.framework.ui.api.View)
     */
    void addView(View view);

    /**
     * Removes view from the panel.
     *
     * @see org.exoplatform.ide.client.framework.ui.api.HasViews#removeView(org.exoplatform.ide.client.framework.ui.api.View)
     */
    boolean removeView(View view);

    Map<String, View> getViews();

    List<String> getAcceptedTypes();

    void acceptType(String viewType);

    void setPanelHidden(boolean panelHidden);

    boolean isPanelHidden();

}
