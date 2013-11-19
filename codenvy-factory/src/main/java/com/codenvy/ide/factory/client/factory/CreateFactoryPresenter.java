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
package com.codenvy.ide.factory.client.factory;

import com.codenvy.ide.factory.client.generate.GetCodeNowButtonEvent;
import com.codenvy.ide.factory.client.generate.GetCodeNowButtonHandler;
import com.codenvy.ide.factory.client.generate.SendMailEvent;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.OpenResourceEvent;
import org.exoplatform.ide.client.framework.application.ResourceSelectedCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.client.marshaller.LogResponseUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import static com.google.gwt.http.client.URL.encodeQueryString;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 * This presenter creates and manages functionality of Factory popup.
 * 
 */
public class CreateFactoryPresenter implements GetCodeNowButtonHandler, ViewClosedHandler,
    VfsChangedHandler, ProjectOpenedHandler, ProjectClosedHandler {
    
    /**
     * Handler interface to handling changes.
     */
    public interface ChangeHandler {
        
        void onChange();
        
    }
    
    public interface Display extends IsView {
        
        /**
         * Returns index of currently active page.
         * 
         * @return index of currently active page
         */
        int getPageIndex();
        
        /**
         * Switches to the next page.
         */
        void nextPage();
        
        /**
         * Switches to the previous page.
         */
        void previousPage();
        
        /**
         * Displays preview of Factory Button in preview area.
         * 
         * @param content HTML content to show Button preview
         */
        void previewFactoryButton(String content);
        
        /**
         * Determines whether Show Counter check box is currently checked.
         * 
         * @return <code>true</code> if the check box is checked, false otherwise
         */
        boolean showCounterChecked();
        
        /**
         * Determines whether Vertical Orientation radio button is currently selected.
         * 
         * @return <code>true</code> if the radio button is selected, false otherwise
         */
        boolean verticalOrientationSelected();
        
        /**
         * Determines whether Horizontal Orientation radio button is currently selected.
         * 
         * @return <code>true</code> if the radio button is selected, false otherwise
         */
        boolean horizontalOrientationSelected();
        
        /**
         * Determines whether White style radio button is currently selected.
         * 
         * @return <code>true</code> if the radio button is selected, false otherwise
         */
        boolean whiteStyleSelected();
        
        /**
         * Determines whether Dark style radio button is currently selected.
         * 
         * @return <code>true</code> if the radio button is selected, false otherwise
         */
        boolean darkStyleSelected();
        
        /**
         * Enables or disables controls to customize default style of Code button.
         * 
         * @param enabled <b>true</b> or <b>false</b> to enable or disable controls
         */
        void enableDefaultStyleOptions(boolean enabled);
        
        /**
         * Sets the {@link ChangeHandler} to handle changes of Factory Button style.
         * 
         * @param handler handler to handle changes
         */
        void setButtonStyleChangedHandler(ChangeHandler handler);

        /**
         * Sets {@link ChangeHandler} to handle changes of Factory Button advanced parameters.
         * 
         * @param handler handler to handle changes
         */
        void setAdvancedParametersChangedHandler(ChangeHandler handler);
        
        /**
         * Returns value of Upload Image field.
         * 
         * @return value of Upload Image field
         */
        String getUploadImageFieldValue();        
        
        /**
         * Sets the {@link ValueChangeHandler} to receive value change events. 
         * 
         * @param handler handler to receive {@link ValueChangeEvent}
         */
        void setUploadImageFieldValueChangeHandler(ValueChangeHandler<String> handler);
        
        /**
         * 
         */
        void cancelUploadFile();

        /**
         * Returns value of Description field.
         * 
         * @return value of Description field
         */
        String getDescriptionFieldValue();
        
        /**
         * Returns value of Email field.
         * 
         * @return value of Email field
         */
        String getEmailFieldValue();
        
        /**
         * @param email
         */
        void setEmailFieldValue(String email);
        
        /**
         * Returns value of Author field.
         * 
         * @return value of Author field
         */
        String getAuthorFieldValue();
        
        /**
         * Sets new value of Author field.
         * 
         * @param author
         */
        void setAuthorFieldValue(String author);
        
        /**
         * Returns value of Open After Launch field.
         * 
         * @return value of Open After Launch field
         */
        String getOpenAfterLaunchFieldValue();
        
        /**
         * Returns value of Organization ID field.
         * 
         * @return value of Organization ID field
         */
        String getOrganizationIdFieldValue();
        
        /**
         * Returns value of Affiliate ID field.
         * 
         * @return value of Affiliate ID field
         */
        String getAffiliateIdFieldValue();
                
        /**
         * Sets new value of Open After Launch field.
         * 
         * @param value new value
         */
        void setOpenAfterLaunchFieldValue(String value);

        /**
         * Sets {@link ClickHandler} to handle mouse clicking on Open After Launch field.
         * 
         * @param handler handler to receive {@link ClickEvent}
         */
        void setOpenAfterLaunchFieldClickHandler(ClickHandler handler);                

        /**
         * Creates a new Factory.
         * 
         * @param callback callback to receive the result
         */
        void createFactory(String postData, AsyncCallback<String> callback);
        
        /**
         * Returns snippet with content for embedding on websites.
         * 
         * @return snippet with content for embedding on websites
         */
        HasValue<String> websitesSnippet();
        
        /**
         * Returns snippet with content for embedding on Github.
         * 
         * @return snippet with content for embedding on Github
         */
        HasValue<String> gitHubSnippet();
        
        /**
         * Returns snippet with content for direct sharing.
         * 
         * @return snippet with content for direct sharing
         */
        HasValue<String> directSharingSnippet();
        
        /**
         * Returns Share on Facebook button.
         * 
         * @return Share on Facebook button
         */
        HasClickHandlers getShareFacebookButton();
        
        /**
         * Returns Share on Google button.
         * 
         * @return Share on Google button
         */
        HasClickHandlers getShareGooglePlusButton();
        
        /**
         * Returns Share on Twitter button.
         * 
         * @return Share on Twitter button
         */
        HasClickHandlers getShareTwitterButton();
        
        /**
         * Returns Share by Email button.
         * 
         * @return Share by Email button
         */
        HasClickHandlers getShareEmailButton();
        
        /**
         * Returns Cancel button.
         * 
         * @return Cancel button
         */
        HasClickHandlers getCancelButton();
        
        /**
         * Returns Create Factory button.
         * 
         * @return Create Factory button
         */
        HasClickHandlers getCreateButton();
        
        /**
         * Returns Back button.
         * 
         * @return Back button
         */
        HasClickHandlers getBackButton();
        
        /**
         * Returns Finish button.
         * 
         * @return Finish button.
         */
        HasClickHandlers getFinishButton();
        
    }
    
    /** Current virtual file system. */
    private VirtualFileSystemInfo vfs;
    
    /** Current project. */
    private ProjectModel          openedProject;
    
    /** Latest commit ID **/
    private String                latestCommitId;
    
    private String                vcsURL;
    
    /**
     * Display instance.
     */
    private Display display;
    
    private JSONObject factoryJSON;    
    
    /**
     * Creates new instance of this presenter.
     */
    public CreateFactoryPresenter() {
        IDE.addHandler(GetCodeNowButtonEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }
    
    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfs = event.getVfsInfo();
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
     */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        openedProject = event.getProject();
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
     */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        openedProject = null;
    }
    
    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }    

    /**
     * @see com.codenvy.ide.factory.client.generate.GetCodeNowButtonHandler#onGetCodeNowButton(com.codenvy.ide.factory.client.generate.GetCodeNowButtonEvent)
     */
    @Override
    public void onGetCodeNowButton(GetCodeNowButtonEvent event) {
        if (display != null) {
            return;
        }

        // Fetch ID of last commit
        try {
            GitClientService.getInstance().log(vfs.getId(), openedProject.getId(), false,
                     new AsyncRequestCallback<LogResponse>(new LogResponseUnmarshaller(new LogResponse(), false)) {
                         @Override
                         protected void onSuccess(LogResponse result) {
                             if (result.getCommits().size() > 0) {
                                 latestCommitId = result.getCommits().get(0).getId();
                             }
                             getGitRepositoryURL();
                         }
    
                         @Override
                         protected void onFailure(Throwable exception) {
                             String errorMessage = (exception.getMessage() != null) ?
                                 exception.getMessage() : GitExtension.MESSAGES.logFailed();
                             IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                         }
                     });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.logFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }
    
    /**
     * Fetch Git repository URL.
     */
    private void getGitRepositoryURL() {
        try {
            GitClientService.getInstance().getGitReadOnlyUrl(vfs.getId(), openedProject.getId(),
                   new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                       @Override
                       protected void onSuccess(StringBuilder result) {
                           vcsURL = result.toString();
                           showPopup();
                       }

                       @Override
                       protected void onFailure(Throwable exception) {
                           String errorMessage = (exception.getMessage() != null && exception.getMessage().length() > 0) ?
                               exception.getMessage() : GitExtension.MESSAGES.initFailed();
                           IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                       }
                   });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null && e.getMessage().length() > 0) ?
                e.getMessage() : GitExtension.MESSAGES.initFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }
    
    /**
     * Creates and displays Factory popup.
     */
    private void showPopup() {
        factoryJSON = null;
        display = new CreateFactoryView();
        IDE.getInstance().openView(display.asView());
        bindDisplay();
        
        // Sets current user email
        display.setEmailFieldValue(IDE.user.getUserId());

        // Sets current user full name
        display.setAuthorFieldValue(IDE.getUserFullName());
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                updatePreviewIFame();
            }
        });
    }
    
    /**
     * Binds display.
     */
    private void bindDisplay() {
        display.getCreateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (factoryJSON != null) {
                    display.nextPage();
                } else {                    
                    createFactory();
                }
            }
        });
        
        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
        
        display.getBackButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                display.previousPage();
            }
        });
        
        display.getFinishButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
        
        display.setButtonStyleChangedHandler(new ChangeHandler() {
            @Override
            public void onChange() {
                factoryJSON = null;
                display.enableDefaultStyleOptions(isStyleAdvanced());
                updatePreviewIFame();
            }
        });
        
        display.setAdvancedParametersChangedHandler(new ChangeHandler() {
            @Override
            public void onChange() {
                factoryJSON = null;
            }
        });
        
        display.setUploadImageFieldValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                factoryJSON = null;
                display.enableDefaultStyleOptions(isStyleAdvanced());
                updatePreviewIFame();
            }
        });
        
        display.getShareFacebookButton().addClickHandler(shareOnFacebookClickHandler);
        display.getShareGooglePlusButton().addClickHandler(shareOnGoogleClickHandler);
        display.getShareTwitterButton().addClickHandler(shareOnTwitterClickHandler);
        display.getShareEmailButton().addClickHandler(shareByEmailClickHandler);
        
        display.setOpenAfterLaunchFieldClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new OpenResourceEvent(new ResourceSelectedCallback() {
                    @Override
                    public void onResourceSelected(Item resource) {
                        factoryJSON = null;
                        
                        if (resource != null) {
                            String path = resource.getPath();
                            String projectPath = openedProject.getPath();
                            if (path.startsWith(projectPath)) {
                                path = path.substring(projectPath.length() + 1);
                            }
                            display.setOpenAfterLaunchFieldValue(path);
                        }
                    }
                }));
            }
        });
    }
    
    /**
     * Determines whether advanced style of Factory button is selected.
     * 
     * @return <b>true</b> if advanced style selected, <b>false</b> otherwise
     */
    private boolean isStyleAdvanced() {
        String path = display.getUploadImageFieldValue();
        
        if (path != null && !path.trim().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Shows preview of advanced Code button.
     * 
     * @param jsURL
     */
    private void previewAdvancedButton(String jsURL) {
        String blankImage = "images/blank.png";
        String counter = display.showCounterChecked() ? "counter=\"visible\" " : "";
        
        String javascriptPreview = "" +
                "<script " +
                    "type=\"text/javascript\" " +
                    "language=\"javascript\" " +
                    "src=\"" + jsURL + "\" " +
                    "style=\"advanced\" " +
                    counter +
                    "img=\"" + blankImage + "\" " +
                "></script>";

        display.previewFactoryButton("" +
            "<html>" +
              "<head></head>" +
              "<body style=\"margin: 0px; padding: 0px;\">" +
                "<div style=\"position:absolute; left:94px; top:5px;\">" +
                    javascriptPreview +
                "</div>" +
            "</body>" +
            "</html>" +
            "");
    }
    
    /**
     * Shows preview of default Code button.
     * 
     * @param jsURL
     * @param style
     * @param counterType
     * @param previewOffsetLeft
     * @param previewOffsetTop
     */
    private void previewDefaultButton(String jsURL, String style, String counterType, int previewOffsetLeft, int previewOffsetTop) {
        String preview = "" +
            "<script " +
                "type=\"text/javascript\" " +
                "language=\"javascript\" " +
                "src=\"" + jsURL + "\" " +
                "style=\"" + style + "\" " +
                "counter=\"" + counterType + "\" " +
               "></script>";

        display.previewFactoryButton("" +
            "<html>" +
            "<head></head>" +
            "<body style=\"margin: 0px; padding: 0px;\">" +
                "<div style=\"position:absolute; left:" + previewOffsetLeft + "px; top:" + previewOffsetTop + "px; \">" +
                preview +
                "</div>" +
            "</body>" +
            "</html>" +
            "");
    }

    /**
     * Generates snippet content for embedding on websites.
     */
    private void updatePreviewIFame() {
        String jsURL = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
            .setPath("factory/resources/factory.js").buildString();

        String uploadFile = display.getUploadImageFieldValue();
        
        if (uploadFile != null && !uploadFile.isEmpty()) {
            previewAdvancedButton(jsURL);
            return;
        }

        String style = display.whiteStyleSelected() ? "white" : "dark";

        if (display.showCounterChecked() && display.verticalOrientationSelected()) {
            previewDefaultButton(jsURL, style, "vertical", 111, 31);
            return;
        }
        
        if (display.showCounterChecked() && !display.verticalOrientationSelected()) {
            previewDefaultButton(jsURL, style, "horizontal", 91, 51);
            return;
        }
        
        previewDefaultButton(jsURL, style, "none", 111, 51);
    }

    /**
     * Creates new Factory.
     */
    private void createFactory() {
        factoryJSON = null;
        
        JSONObject request = new JSONObject();
        request.put("v", new JSONString("1.1"));
        request.put("vcs", new JSONString("git"));
        request.put("vcsurl", new JSONString(vcsURL));
        request.put("commitid", new JSONString(latestCommitId));
        request.put("action", new JSONString("openproject"));
        request.put("description", new JSONString(display.getDescriptionFieldValue()));
        request.put("contactmail", new JSONString(display.getEmailFieldValue()));
        request.put("author", new JSONString(display.getAuthorFieldValue()));
        request.put("openfile", new JSONString(display.getOpenAfterLaunchFieldValue()));
        request.put("orgid", new JSONString(display.getOrganizationIdFieldValue()));
        request.put("affiliateid", new JSONString(display.getAffiliateIdFieldValue()));
      
        JSONObject projectAttributes = new JSONObject();
        request.put("projectattributes", projectAttributes);
        projectAttributes.put("pname", new JSONString(openedProject.getName()));
        projectAttributes.put("ptype", new JSONString(openedProject.getProjectType()));        

        if (isStyleAdvanced()) {
            if (display.showCounterChecked()) {
                request.put("style", new JSONString("Advanced with Counter"));
            } else {
                request.put("style", new JSONString("Advanced"));
            }
        } else {
            if (display.showCounterChecked()) {
                  if (display.verticalOrientationSelected()) {
                      if (display.whiteStyleSelected()) {
                          request.put("style", new JSONString("Vertical,White"));
                      } else {
                          request.put("style", new JSONString("Vertical,Dark"));
                      }
                  } else {
                      if (display.whiteStyleSelected()) {
                          request.put("style", new JSONString("Horizontal,White"));
                      } else {
                          request.put("style", new JSONString("Horizontal,Dark"));
                      }                      
                  }
            } else {
                if (display.whiteStyleSelected()) {
                    request.put("style", new JSONString("White"));
                } else {
                    request.put("style", new JSONString("Dark"));
                }                      
            }
        }
        
        display.createFactory(request.toString(), new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                IDE.fireEvent(new ExceptionThrownEvent(caught, "Factory cannot be created"));
            }

            @Override
            public void onSuccess(String result) {
                try {
                    factoryJSON = JSONParser.parseStrict(result).isObject();
                    
                    loadSnippetContent(display.websitesSnippet(), "snippet/html");
                    loadSnippetContent(display.gitHubSnippet(), "snippet/markdown");
                    loadSnippetContent(display.directSharingSnippet(), "snippet/url");
                    findBetterFacebokImage();
                    
                    display.nextPage();
                } catch (Exception e) {
                    while (result.indexOf(". ") > 0) {
                        result = result.replace(". ", ".<br>");
                    }
                    Dialogs.getInstance().showError("IDE", "Factory cannot be created.<br><br>" + result);
                }
            }
        });
    }
    
    /**
     * Returns link by relation.
     * 
     * @param rel relation
     * @return link
     */
    private String getLink(String rel) {
        if (rel == null || rel.trim().isEmpty()) {
            return null;
        }
        
        JSONArray array = factoryJSON.get("links").isArray();
        for (int i = 0; i < array.size(); i++) {
            JSONObject link = array.get(i).isObject();
            if (rel.equals(link.get("rel").isString().stringValue())) {
                return link.get("href").isString().stringValue();
            }
        }
        
        return null;
    }
    
    /**
     * Updates snippet content according to specified relation. 
     * 
     * @param snippet snippet to update
     * @param rel relation
     */
    private void loadSnippetContent(final HasValue<String> snippet, final String rel) {
        snippet.setValue("");
        String url = getLink(rel);
        if (url != null) {
            try {
                final Unmarshallable<String> unmarshaller = new Unmarshallable<String>() {
                    private String text;
                    @Override
                    public void unmarshal(Response response) throws UnmarshallerException {
                        text = response.getText();
                    }
                    @Override
                    public String getPayload() {
                        return text;
                    }
                };
                
                AsyncRequestCallback<String> callback = new AsyncRequestCallback<String>(unmarshaller) {
                    @Override
                    protected void onSuccess(String result) {
                        snippet.setValue(unmarshaller.getPayload());
                    }
                    @Override
                    protected void onFailure(Throwable exception) {
                        IDE.fireEvent(new ExceptionThrownEvent(exception));
                    }
                };
                AsyncRequest.build(RequestBuilder.GET, url).send(callback);            
            } catch (Exception e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }        
        }
    }
    
    /**
     * Indicates whether image checked for sharing on Facebook.
     */
    private boolean facebookImageChecked; 
    
    /**
     * URL to image for sharing on Facebook.
     */
    private String facebookImageURl;
    
    /**
     * Checks dimensions of image for sharing on Facebook.
     */
    private void findBetterFacebokImage() {
        facebookImageChecked = false;
        
        facebookImageURl = getLink("image");
        if (facebookImageURl == null) {
            facebookImageChecked = true;
            facebookImageURl = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                .setPath("factory/resources/codenvy.png").buildString();
            return;
        }        
        
        final Image fbImage = new Image(facebookImageURl);
        fbImage.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        int width = fbImage.getOffsetWidth();
                        int height = fbImage.getOffsetHeight();
                        
                        fbImage.removeFromParent();
                        facebookImageChecked = true;
                        
                        if (width < 200 || height < 200) {
                            facebookImageURl = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                                .setPath("factory/resources/codenvy.png").buildString();                            
                        }
                    }
                });
            }
        });
        
        fbImage.addErrorHandler(new ErrorHandler() {
            @Override
            public void onError(ErrorEvent event) {
                fbImage.removeFromParent();
                facebookImageChecked = true;

                facebookImageURl = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                    .setPath("factory/resources/codenvy.png").buildString();
            }
        });
        RootPanel.get().add(fbImage, -10000, -10000);
    }
    
    /**
     * Share on Facebook button Click handler.
     */
    private ClickHandler shareOnFacebookClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            String createFactoryURL = getLink("create-project");        
            if (createFactoryURL == null || !facebookImageChecked) {
                return;
            }
            
            String factoryId = factoryJSON.get("id").isString().stringValue();        
            String shareURL = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                .setPath("factory/share/" + factoryId).buildString();
            
            Window.open("https://www.facebook.com/sharer/sharer.php" +
                "?u=" + encodeQueryString(shareURL),
                "facebook-share-dialog",
                "menubar=no,toolbar=no,resizable=yes,scrollbars=yes,width=626,height=436");
        }
    };
    
    /**
     * Share on Google button Click handler.
     */
    private ClickHandler shareOnGoogleClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            String createFactoryURL = getLink("create-project");
            if (createFactoryURL == null) {
                return;
            }
            
            String factoryId = factoryJSON.get("id").isString().stringValue();        
            String shareURL = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                .setPath("factory/share/" + factoryId).buildString();
            
            Window.open("https://plus.google.com/share" +
                "?url=" + encodeQueryString(shareURL),
                "", "menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=480,width=550");
        }
    };

    /**
     * Share on Twitter button Click handler.
     */
    private ClickHandler shareOnTwitterClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            String factoryURL = getLink("create-project");
            if (factoryURL == null) {
                return;
            }
            
            String text = "Code,%20Build,%20Test%20and%20Deploy%20my%20Factory%20-%20" + 
                openedProject.getName() + ":%20" +
                encodeQueryString(factoryURL) + "%20%23Codenvy";
            
            Window.open("https://twitter.com/intent/tweet?text=" + text + "&url=/",
                "", "menubar=no,toolbar=no,resizable=yes,scrollbars=yes,width=550,height=420");
        }
    };
    
    /**
     * Share by Email button Click handler.
     */
    private ClickHandler shareByEmailClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            String createFactoryURL = getLink("create-project");
            if (createFactoryURL == null) {
                return;
            }
            
            IDE.fireEvent(new SendMailEvent(createFactoryURL, openedProject.getName()));
        }
    };

}
