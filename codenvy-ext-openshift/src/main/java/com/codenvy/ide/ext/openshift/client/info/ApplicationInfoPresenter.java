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
package com.codenvy.ide.ext.openshift.client.info;

import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Inject;

import java.util.Date;

/**
 * Show application properties.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationInfoPresenter implements ApplicationInfoView.ActionDelegate {
    private ApplicationInfoView           view;
    private OpenShiftLocalizationConstant constant;

    /**
     * Create presenter.
     *
     * @param view
     * @param constant
     */
    @Inject
    protected ApplicationInfoPresenter(ApplicationInfoView view, OpenShiftLocalizationConstant constant) {
        this.view = view;
        this.constant = constant;

        this.view.setDelegate(this);
    }

    /**
     * Show dialog.
     *
     * @param application
     *         object of application to view properties
     */
    public void showDialog(AppInfo application) {
        if (!view.isShown()) {
            JsonArray<ApplicationProperty> properties = getApplicationProperties(application);
            view.setApplicationProperties(properties);

            view.showDialog();
        }
    }

    /**
     * Get all necessary properties from current application.
     *
     * @param application
     *         object of application fom which properties getted
     * @return json array with properties value
     */
    public JsonArray<ApplicationProperty> getApplicationProperties(AppInfo application) {
        JsonArray<ApplicationProperty> properties = JsonCollections.createArray();
        properties.add(new ApplicationProperty(constant.applicationInfoViewNameField(), application.getName()));
        properties.add(new ApplicationProperty(constant.applicationInfoViewTypeField(), application.getType()));
        properties.add(new ApplicationProperty(constant.applicationInfoViewPublicUrlField(), "<a href =\"" + application.getPublicUrl() +
                                                                                             "\" target=\"_blank\">" +
                                                                                             application.getPublicUrl() +
                                                                                             "</a>"));
        properties.add(new ApplicationProperty(constant.applicationInfoViewGitUrlField(), application.getGitUrl()));

        String time = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM)
                                    .format(new Date((long)application.getCreationTime()));

        properties.add(new ApplicationProperty(constant.applicationInfoViewCreationTimeField(), time));

        return properties;
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }
}
