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
package com.codenvy.ide.extension.android.client;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface AndroidExtensionLocalizationConstant extends com.google.gwt.i18n.client.Messages {
    @Key("run.application.control.title")
    String runApplicationControlTitle();

    @Key("run.application.control.prompt")
    String runApplicationControlPrompt();

    @Key("starting.project.message")
    String startingProjectMessage(String project);

    @Key("project.started.message")
    String projectStartedMessage(String project);

    @Key("application.started.url")
    String applicationStartedUrl(String application, String url);

    @Key("start.application.failed")
    String startApplicationFailed();

    @Key("build.application.failed")
    String buildApplicationFailed();

    @Key("not.android.project")
    String notAndroidProject();
}
