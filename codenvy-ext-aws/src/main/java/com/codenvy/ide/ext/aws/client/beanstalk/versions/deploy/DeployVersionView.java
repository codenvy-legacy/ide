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
package com.codenvy.ide.ext.aws.client.beanstalk.versions.deploy;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface DeployVersionView extends View<DeployVersionView.ActionDelegate> {
    interface ActionDelegate {
        void onDeployButtonClicked();

        void onCancelButtonClicked();

        void onNewEnvironmentModeClicked();

        void onExistingEnvironmentModeClicked();
    }

    boolean getNewEnvironmentMode();

    boolean getExistingEnvironmentMode();

    String getEnvironmentsField();

    void setEnvironmentsValues(JsonArray<String> values);

    void enableEnvironmentsField(boolean value);

    void enableDeployButton(boolean enable);

    boolean isShown();

    void showDialog();

    void close();
}
