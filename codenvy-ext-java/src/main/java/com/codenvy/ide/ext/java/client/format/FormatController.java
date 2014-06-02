/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [$today.year] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.client.format;

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.ext.java.client.editor.JavaParserWorker;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author Roman Nikitenko
 */
public class FormatController {

    private FormatClientService service;
    private JavaParserWorker    worker;

    @Inject
    public FormatController(JavaParserWorker worker, FormatClientService formatClientService, EventBus eventBus) {
        this.service = formatClientService;
        this.worker = worker;
        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                getFormattingCodenvySettings();
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {

            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {

            }
        });
    }

    private void getFormattingCodenvySettings() {
        service.formattingCodenvySettings(new AsyncRequestCallback<String>(new com.codenvy.ide.rest.StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                JsoStringMap<String> mapSettings = Jso.deserialize(result).cast();
                worker.preferenceFormatsettings(mapSettings);
            }

            @Override
            protected void onFailure(Throwable throwable) {
                Log.error(getClass(), "Can not get formatting settings from file 'codenvy-codestyle-eclipse_.xml'");
            }
        });
    }
}
