package com.codenvy.ide.ext.java.client.format;

import elemental.js.util.Json;

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.ext.java.client.editor.JavaParserWorker;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author Roman Nikitenko
 */
public class FormatController {

    private FormatClientService service;
    private JavaParserWorker worker;

    @Inject
    public FormatController(JavaParserWorker worker, FormatClientService formatClientService, EventBus eventBus){
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
    private void getFormattingCodenvySettings(){
        service.formattingCodenvySettings(new AsyncRequestCallback<String>(new com.codenvy.ide.rest.StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                JsoStringMap<String> mapSettings = Jso.deserialize(result).cast();
                worker.preferenceFormatsettings(mapSettings);
            }
            @Override
            protected void onFailure(Throwable throwable) {

            }
        });
    }
}
