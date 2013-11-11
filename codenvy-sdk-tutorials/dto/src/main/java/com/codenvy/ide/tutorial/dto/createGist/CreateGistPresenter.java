package com.codenvy.ide.tutorial.dto.createGist;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.tutorial.dto.DtoClientImpls.*;
import com.codenvy.ide.tutorial.dto.TutorialDtoLocalizationConstant;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** Presenter for creating Gist on GitHub from code snippet. */
@Singleton
public class CreateGistPresenter implements CreateGistView.ActionDelegate {
    private static final String GIT_HUB_GISTS_API            = "https://api.github.com/gists";
    private static final String GIT_HUB_ANONYMOUS_GISTS_HOST = "gist.github.com/anonymous";
    private CreateGistView                  view;
    private TutorialDtoLocalizationConstant constant;
    private ConsolePart                     console;

    /**
     * Create presenter.
     *
     * @param view
     * @param console
     * @param constant
     */
    @Inject
    public CreateGistPresenter(CreateGistView view, ConsolePart console,
                               TutorialDtoLocalizationConstant constant) {
        this.view = view;
        this.view.setDelegate(this);
        this.console = console;
        this.constant = constant;
    }

    /** Show dialog. */
    public void showDialog() {
        view.setPublic(true);
        view.setSnippet("sample snippet");
        view.focusInSnippetField();
        view.setEnableCreateButton(true);
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateClicked() {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, GIT_HUB_GISTS_API);
        try {
            builder.sendRequest(getRequestData(), new RequestCallback() {
                public void onResponseReceived(Request request, Response response) {
                    JSONObject json = JSONParser.parseStrict(response.getText()).isObject();
                    if (json == null) {
                        Window.alert(constant.detectGistIdError());
                        return;
                    }
                    afterGistCreated(json.get("id").isString().stringValue());
                }

                public void onError(Request request, Throwable exception) {
                    Window.alert(constant.createGistError());
                }
            });
        } catch (RequestException e) {
            Window.alert(constant.createGistError());
        }
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String message = view.getSnippet();
        view.setEnableCreateButton(!message.isEmpty());
    }

    private String getRequestData() {
        final String content = view.getSnippet();
        final boolean isPublic = view.isPublic();
        final String description = "This snippet created from Codenvy";

        FilesImpl files = FilesImpl.make();
        files.setTitle(TitleImpl.make().setContent(content));
        SnippetImpl snippet = SnippetImpl.make();
        snippet.setDescription(description).setIsPublic(isPublic).setFiles(files);
        return snippet.serialize();
    }

    private void afterGistCreated(String gistId) {
        UrlBuilder builder = new UrlBuilder();
        final String url =
                builder.setProtocol("https").setHost(GIT_HUB_ANONYMOUS_GISTS_HOST + "/" + gistId).buildString();
        console.print("Your Gist available on " + "<a href=\"" + url + "\" target=\"_blank\">" + url + "</a>");
    }
}
