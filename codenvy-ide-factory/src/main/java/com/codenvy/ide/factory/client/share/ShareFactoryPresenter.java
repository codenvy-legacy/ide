/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.factory.client.share;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.factory.dto.Factory;
import com.codenvy.api.factory.dto.FactoryV1_0;
import com.codenvy.api.factory.dto.FactoryV1_1;
import com.codenvy.api.factory.dto.ProjectAttributes;
import com.codenvy.api.factory.gwt.client.FactoryServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.factory.client.FactoryLocalizationConstant;
import com.codenvy.ide.factory.client.FactoryResources;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;
import static com.google.gwt.http.client.RequestBuilder.GET;
import static com.google.gwt.http.client.URL.encodeQueryString;

/**
 * Presenter for Factory creation and sharing.
 * 
 * @author Ann Shumilova
 */
public class ShareFactoryPresenter extends BasePresenter implements ShareFactoryView.ActionDelegate {
    private final static String               NOT_GIT_REPO        = "Not a git repository.";
    private final static String               VERSION             = "1.1";
    private final static String               VSC                 = "git";
    private final static String               OPEN_PROJECT_ACTION = "openproject";
    private final String                      SEARCH_URL;

    private final FactoryServiceClient        factoryServiceClient;
    private final ShareFactoryView            view;
    private boolean                           isViewClosed        = true;
    private final WorkspaceAgent              workspaceAgent;
    private final FactoryLocalizationConstant locale;
    private final ResourceProvider            resourceProvider;
    private final GitServiceClient            gitService;
    private final DtoUnmarshallerFactory      dtoUnmarshallerFactory;
    private final GitLocalizationConstant     gitLocale;
    private final NotificationManager         notificationManager;
    private final DtoFactory                  dtoFactory;

    private String                            lastCommitId;
    private String                            gitUrl;
    private MessageBus                        wsMessageBus;
    private CoreLocalizationConstant          coreLocale;
    private FactoryResources                  factoryResources;

    private Factory                           factory;

    /**
     * 
     */
    @Inject
    public ShareFactoryPresenter(FactoryServiceClient factoryServiceClient, ShareFactoryView view,
                                 WorkspaceAgent workspaceAgent,
                                 FactoryLocalizationConstant locale,
                                 ResourceProvider resourceProvider,
                                 GitServiceClient gitService,
                                 DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                 GitLocalizationConstant gitLocale,
                                 NotificationManager notificationManager,
                                 DtoFactory dtoFactory,
                                 MessageBus wsMessageBus,
                                 @Named("workspaceId") String workspaceId,
                                 CoreLocalizationConstant coreLocale,
                                 FactoryResources factoryResources) {
        this.factoryServiceClient = factoryServiceClient;
        this.view = view;
        this.view.setDelegate(this);
        this.workspaceAgent = workspaceAgent;
        this.locale = locale;
        this.resourceProvider = resourceProvider;
        this.gitService = gitService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.gitLocale = gitLocale;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        this.wsMessageBus = wsMessageBus;
        this.coreLocale = coreLocale;
        this.factoryResources = factoryResources;

        SEARCH_URL = "/project/" + workspaceId + "/search";
    }

    public void showDialog() {
        getLogs();
    }

    /**
     * Opens Share Factory view and sets initial state.
     */
    private void openView() {
        if (isViewClosed) {
            workspaceAgent.openPart(this, PartStackType.NAVIGATION);
            isViewClosed = false;
        }
        view.showGenerateButton(true);
        view.showEncodedPanel(false);
        view.setStyleInitialState();
        view.showSocialEncoded(false);
        view.showSocialNonEncoded(false);

        PartPresenter activePart = partStack.getActivePart();
        if (activePart == null || !activePart.equals(this)) {
            partStack.setActivePart(this);
        }
        updatePreviewIFame();
        updateNonEncodedFactoryUrl();
    }

    private void updateNonEncodedFactoryUrl() {
        String url = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                                     .setPath("factory").buildString();

        view.setNonEncodedLink(url + "?" + buildNonEncoded(createFactoryFromParameters()));
    }

    private Factory createFactoryFromParameters() {
        Factory factory = dtoFactory.createDto(Factory.class);
        factory.setV(VERSION);
        factory.setVcs(VSC);
        factory.setVcsurl(gitUrl);
        factory.setCommitid(lastCommitId);
        factory.setAction(OPEN_PROJECT_ACTION);
        factory.setDescription(view.getDescription());
        factory.setAuthor(view.getAuthor());
        factory.setOpenfile(view.getOpenFile());
        factory.setProjectattributes(dtoFactory.createDto(ProjectAttributes.class).withPname(resourceProvider.getActiveProject().getName())
                                               .withPtype(resourceProvider.getActiveProject().getDescription().getProjectTypeId()));


        // TODO factory.setVariables(variable);
        if (view.getShowCounter()) {
            if (view.getVerticalAlign()) {
                factory.setStyle(view.getWhiteTheme() ? "Vertical,White" : "Vertical,Dark");
            } else {
                factory.setStyle(view.getWhiteTheme() ? "Horizontal,White" : "Horizontal,Dark");
            }
        } else {
            factory.setStyle(view.getWhiteTheme() ? "White" : "Dark");
        }
        return factory;
    }

    public String buildNonEncoded(Factory factory) {
        StringBuilder result = new StringBuilder();
        buildNonEncoded(factory, result);
        return result.toString();
    }

    private void buildNonEncoded(FactoryV1_0 factory, StringBuilder builder) {
        builder.append("v=").append(factory.getV());
        builder.append("&vcs=").append(factory.getVcs());
        builder.append("&vcsurl=").append(URL.encode(factory.getVcsurl()));
        if (factory.getCommitid() != null) {
            builder.append("&commitid=").append(factory.getCommitid());
        }
        if (factory.getCommitid() == null) {
            builder.append("&commitid=").append(factory.getCommitid());
        }
        if (factory.getAction() != null) {
            builder.append("&action=").append(factory.getAction());
        }
        if (factory.getOpenfile() != null && !factory.getOpenfile().isEmpty()) {
            builder.append("&openfile=").append(factory.getOpenfile());
        }
    }

    private void buildNonEncoded(FactoryV1_1 factory, StringBuilder builder) {
        buildNonEncoded(((FactoryV1_0)factory), builder);
        ProjectAttributes projectattributes = factory.getProjectattributes();
        if (projectattributes != null) {
            if (projectattributes.getPname() != null) {
                builder.append("&projectattributes.pname=").append(projectattributes.getPname());
            }

            if (projectattributes.getPtype() != null) {
                builder.append("&projectattributes.ptype=").append(projectattributes.getPtype());
            }
        }

        if (factory.getContactmail() != null) {
            builder.append("&contactmail=").append(factory.getContactmail());
        }

        if (factory.getAuthor() != null && !factory.getAuthor().isEmpty()) {
            builder.append("&author=").append(factory.getAuthor());
        }

        if (factory.getDescription() != null && !factory.getDescription().isEmpty()) {
            builder.append("&description=").append(factory.getDescription());
        }

        if (factory.getOrgid() != null) {
            builder.append("&orgid=").append(factory.getOrgid());
        }

        if (factory.getAffiliateid() != null) {
            builder.append("&affiliateid=").append(factory.getAffiliateid());
        }

        if (factory.getVcsbranch() != null) {
            builder.append("&vcsbranch=").append(factory.getVcsbranch());
        }
        /*
         * TODO if (factory.getVariables() != null) {
         * builder.append("&variables=").append(encode(DtoFactory.getInstance().toJson(factory.getVariables()))); }
         */
    }

    /**
     * The the history of commits.
     */
    private void getLogs() {
        gitService.log(resourceProvider.getActiveProject().getId(), false,
                       new AsyncRequestCallback<LogResponse>(dtoUnmarshallerFactory.newUnmarshaller(LogResponse.class)) {

                           @Override
                           protected void onSuccess(LogResponse result) {
                               lastCommitId = result.getCommits().get(0).getId();
                               getGitUrl();
                           }

                           @Override
                           protected void onFailure(Throwable exception) {
                               if (NOT_GIT_REPO.equals(exception.getMessage())) {
                                   initGitRepository();
                               } else {
                                   String errorMessage =
                                                         (exception.getMessage() != null && !exception.getMessage().isEmpty())
                                                             ? exception.getMessage() : gitLocale.logFailed();
                                   Notification notification = new Notification(errorMessage, ERROR);
                                   notificationManager.showNotification(notification);
                               }
                           }
                       });
    }

    /**
     * Initializes GIT repository.
     */
    private void initGitRepository() {
        try {
            gitService.init(resourceProvider.getActiveProject().getId(), resourceProvider.getActiveProject().getName(), false,
                            new RequestCallback<Void>() {

                                @Override
                                protected void onSuccess(Void result) {
                                    Notification notification = new Notification(gitLocale.initSuccess(), INFO);
                                    notificationManager.showNotification(notification);
                                    getLogs();
                                }

                                @Override
                                protected void onFailure(Throwable exception) {
                                    handleError(exception);
                                }
                            });
        } catch (WebSocketException e) {
            handleError(e);
        }
    }

    private void getGitUrl() {
        gitUrl = resourceProvider.getActiveProject().getPath();
        openView();
        /*
         * TODO gitService.getGitReadOnlyUrl(resourceProvider.getActiveProject().getId(), new AsyncRequestCallback<String>(new
         * StringUnmarshaller()) {
         * @Override protected void onSuccess(String result) { gitUrl = result; openView(); }
         * @Override protected void onFailure(Throwable exception) { Notification notification = new Notification(exception.getMessage(),
         * ERROR); notificationManager.showNotification(notification); } });
         */
    }

    /**
     * Handler some action whether some exception happened.
     * 
     * @param e exception what happened
     */
    private void handleError(@NotNull Throwable e) {
        String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : gitLocale.initFailed();
        Notification notification = new Notification(errorMessage, ERROR);
        notificationManager.showNotification(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onNonEncodedSocialClicked(boolean isDown) {
        view.showSocialNonEncoded(isDown);
    }

    /** {@inheritDoc} */
    @Override
    public void onEncodedSocialClicked(boolean isDown) {
        view.showSocialEncoded(isDown);
    }

    /** {@inheritDoc} */
    @Override
    public void onGenerateEncodedUrlClicked() {
        createFactory(new AsyncCallback<Factory>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Factory result) {
                view.showGenerateButton(false);
                view.showEncodedPanel(true);
                for (Link link : result.getLinks()) {
                    if ("create-project".equals(link.getRel())) {
                        view.setEncodedLink(link.getHref());
                    }
                }
            }
        });
    }

    private void createFactory(final AsyncCallback<Factory> callback) {
        String post = dtoFactory.toJson(createFactoryFromParameters());

        view.submitCreateFactoryForm(post, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(String result) {
                try {
                    factory = dtoFactory.createDtoFromJson(result, Factory.class);
                    callback.onSuccess(factory);
                } catch (Exception e) {
                    while (result.indexOf(". ") > 0) {
                        result = result.replace(". ", ".<br>");
                    }
                    Notification notification = new Notification(result, ERROR);
                    notificationManager.showNotification(notification);
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return locale.shareViewTitle();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public int getSize() {
        return 290;
    }

    /** {@inheritDoc} */
    @Override
    public void onFactoryButtonStyleChanged() {
        updatePreviewIFame();
        updateNonEncodedFactoryUrl();
        view.showEncodedPanel(false);
        view.showGenerateButton(true);
    }

    /**
     * Updates the factory button preview iframe.
     */
    private void updatePreviewIFame() {
       
        String jsURL = GWT.getModuleBaseForStaticFiles() + "factory-preview.js";

        String style = view.getWhiteTheme() ? "white" : "dark";

        if (view.getShowCounter() && view.getVerticalAlign()) {
            previewDefaultButton(jsURL, style, "vertical", 20, 10);
            return;
        }

        if (view.getShowCounter() && !view.getVerticalAlign()) {
            previewDefaultButton(jsURL, style, "horizontal", 20, 10);
            return;
        }

        previewDefaultButton(jsURL, style, "none", 20, 10);
    }

    /**
     * @param jsURL preview factory button script location
     * @param style button's style
     * @param counterType type of the counter
     * @param previewOffsetLeft preview offset left
     * @param previewOffsetTop preview offset top
     */
    private void previewDefaultButton(String jsURL, String style, String counterType, int previewOffsetLeft,
                                      int previewOffsetTop) {
        String preview = "" +
                         "<script " +
                         "type=\"text/javascript\" " +
                         "language=\"javascript\" " +
                         "src=\"" + jsURL + "\" " +
                         "style=\"" + style + "\" " +
                         "counter=\"" + counterType + "\" " +
                         "></script>";

        view.previewFactoryButton("" +
                                  "<html>" +
                                  "<head></head>" +
                                  "<body style=\"margin: 0px; padding: 0px;\">" +
                                  "<div style=\"position:absolute; left:" + previewOffsetLeft + "px; top:" +
                                  previewOffsetTop + "px; \">" +
                                  preview +
                                  "</div>" +
                                  "</body>" +
                                  "</html>" +
                                  "");
    }

    /** {@inheritDoc} */
    @Override
    public void onRequestFileSuggestions(String query, final AsyncCallback<Array<String>> callback) {
        // add '*' to allow search files by first letters
        search(query + "*", new AsyncCallback<Array<ItemReference>>() {
            @Override
            public void onSuccess(Array<ItemReference> result) {
                Array<String> suggestions = Collections.createArray();
                for (ItemReference item : result.asIterable()) {
                    // skip hidden items
                    if (!item.getPath().contains("/.")) {
                        suggestions.add(item.getPath());
                    }
                }
                callback.onSuccess(suggestions);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /**
     * Search all files that match the entered value in open file field.
     * 
     * @param fileName file name or part of it
     * @param callback
     */
    private void search(String fileName, final AsyncCallback<Array<ItemReference>> callback) {
        final String url = SEARCH_URL + resourceProvider.getActiveProject().getPath() + "?name=" + fileName;
        Message message = new MessageBuilder(GET, url).header(ACCEPT, APPLICATION_JSON).build();
        Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newWSArrayUnmarshaller(ItemReference.class);
        try {
            wsMessageBus.send(message, new RequestCallback<Array<ItemReference>>(unmarshaller) {
                @Override
                protected void onSuccess(Array<ItemReference> result) {
                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (WebSocketException e) {
            callback.onFailure(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onParametersChanged() {
        updateNonEncodedFactoryUrl();
        view.showEncodedPanel(false);
        view.showGenerateButton(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onFacebookClicked(boolean isEncoded) {
        // TODO
        String shareURL = "TODO path";

        Window.open("https://www.facebook.com/sharer/sharer.php" +
                    "?u=" + encodeQueryString(shareURL),
                    "facebook-share-dialog",
                    "menubar=no,toolbar=no,resizable=yes,scrollbars=yes,width=626,height=436");
    }

    /** {@inheritDoc} */
    @Override
    public void onTwitterClicked(boolean isEncoded) {
        // TODO
        String shareURL = "TODO path";
        String text = "Code,%20Build,%20Test%20and%20Deploy%20my%20Factory%20-%20" +
                      resourceProvider.getActiveProject().getName() + ":%20" +
                      encodeQueryString(shareURL) + "%20%23Codenvy";

        Window.open("https://twitter.com/intent/tweet?text=" + text + "&url=/",
                    "", "menubar=no,toolbar=no,resizable=yes,scrollbars=yes,width=550,height=420");

    }

    /** {@inheritDoc} */
    @Override
    public void onGooglePlusClicked(boolean isEncoded) {
        // TODO
        String shareURL = "TODO path";

        Window.open("https://plus.google.com/share" +
                    "?url=" + encodeQueryString(shareURL),
                    "", "menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=480,width=550");
    }

    /** {@inheritDoc} */
    @Override
    public void onMailClicked(boolean isEncoded) {
        // TODO
    }

    /** {@inheritDoc} */
    @Override
    public void onHtmlSnippetClicked(boolean isEncoded) {
        // TODO Auto-generated method stub
        new SnippetPopup("TODO", "TODO", coreLocale, factoryResources);

    }

    /** {@inheritDoc} */
    @Override
    public void onGitHubSnippetClicked(boolean isEncoded) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void onIFrameSnippetClicked(boolean isEncoded) {
        // TODO Auto-generated method stub

    }


}
