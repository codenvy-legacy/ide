package com.codenvy.ide.ext.gae.client.project.limit;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.gae.shared.ResourceLimit;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link LimitTabPanePresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
public interface LimitTabPaneView extends View<LimitTabPaneView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    interface ActionDelegate {
    }

    /**
     * Set list of application resources limits.
     *
     * @param limits
     *         list of {@link com.codenvy.ide.ext.gae.shared.ResourceLimit}.
     */
    void setResourceLimits(JsonArray<ResourceLimit> limits);
}
