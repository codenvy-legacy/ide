package com.codenvy.ide.ext.gae.client.project.general.logs;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.part.base.BaseActionDelegate;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 06.08.13 vlad $
 */
public interface LogsView extends View<LogsView.ActionDelegate> {
    interface ActionDelegate extends BaseActionDelegate {
        void onGetLogsButtonClicked();
    }

    void setLogsContent(String content);

    int getLogsDaysCount();

    String getLogsSeverity();

    void setTitle(String title);
}
