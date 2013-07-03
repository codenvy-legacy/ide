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
package com.codenvy.ide.ext.aws.client.beanstalk.environments;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface EnvironmentTabPainView extends View<EnvironmentTabPainView.ActionDelegate> {
    interface ActionDelegate {
        void onEditConfigurationButtonClicked(EnvironmentInfo environment);

        void onRestartButtonClicked(EnvironmentInfo environment);

        void onRebuildButtonClicked(EnvironmentInfo environment);

        void onTerminateButtonClicked(EnvironmentInfo environment);

        void onGetLogsButtonCLicked(EnvironmentInfo environment);
    }

    void setEnvironments(List<EnvironmentInfo> environments);
}
