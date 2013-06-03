/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationInfoPresenter implements ApplicationInfoView.ActionDelegate {
    private ApplicationInfoView           view;
    private OpenShiftLocalizationConstant constant;

    @Inject
    protected ApplicationInfoPresenter(ApplicationInfoView view, OpenShiftLocalizationConstant constant) {
        this.view = view;
        this.constant = constant;

        this.view.setDelegate(this);
    }

    public void showDialog(AppInfo application) {
        if (!view.isShown()) {
            JsonArray<ApplicationProperty> properties = getApplicationProperties(application);
            view.setApplicationProperties(properties);

            view.showDialog();
        }
    }

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

    @Override
    public void onCloseClicked() {
        view.close();
    }
}
