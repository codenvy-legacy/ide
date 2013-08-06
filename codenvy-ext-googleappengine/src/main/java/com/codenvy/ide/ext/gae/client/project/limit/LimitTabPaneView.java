package com.codenvy.ide.ext.gae.client.project.limit;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.gae.shared.ResourceLimit;
import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
public interface LimitTabPaneView extends View<LimitTabPaneView.ActionDelegate> {
    interface ActionDelegate {

    }

    void setResourceLimits(JsonArray<ResourceLimit> limits);
}
