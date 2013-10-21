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

import com.codenvy.ide.factory.client.FactoryExtension;
import com.codenvy.ide.factory.client.generate.GetCodeNowButtonEvent;
import com.codenvy.ide.factory.client.generate.GetCodeNowButtonHandler;
import com.codenvy.ide.factory.client.generate.SendMailEvent;
import com.codenvy.ide.factory.client.generate.SpinnetGenerator;
import com.codenvy.ide.factory.client.marshaller.UserProfileUnmarshaller;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
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
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.client.marshaller.LogResponseUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.factory.client.FactorySpec10.ACTION_PARAMETER;
import static com.codenvy.ide.factory.client.FactorySpec10.COMMIT_ID;
import static com.codenvy.ide.factory.client.FactorySpec10.CURRENT_VERSION;
import static com.codenvy.ide.factory.client.FactorySpec10.DEFAULT_ACTION;
import static com.codenvy.ide.factory.client.FactorySpec10.PROJECT_NAME;
import static com.codenvy.ide.factory.client.FactorySpec10.PROJECT_TYPE;
import static com.codenvy.ide.factory.client.FactorySpec10.VCS;
import static com.codenvy.ide.factory.client.FactorySpec10.VCS_URL;
import static com.codenvy.ide.factory.client.FactorySpec10.VERSION_PARAMETER;
import static com.codenvy.ide.factory.client.FactorySpec10.WORKSPACE_NAME;
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
     * Handler interface for handle changing of Button style.
     */
    public interface ButtonStyleChangedHandler {
        
        void onButtonStyleChanged();
        
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
         * Sets the {@link ButtonStyleChangedHandler} to handle changes of Factory Button style.
         * 
         * @param handler handler to handle changes
         */
        void setButtonStyleChangedHandler(ButtonStyleChangedHandler handler);
                
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
        
        void setEmailFieldValue(String email);
        
        /**
         * Returns value of Author field.
         * 
         * @return value of Author field
         */
        String getAuthorFieldValue();
        
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
         * Sets JSON content for Create Factory request.
         * 
         * @param content JSON content
         */
        void setCreateFactoryRequestContent(String content);        

        /**
         * Creates a new Factory.
         * 
         * @param callback callback to receive the result
         */
        void createFactory(AsyncCallback<String> callback);
        
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
    
    /** A Factory URL itself. */
    private String                factoryURL;
    
    /**
     * Display instance.
     */
    private Display display;
    
    private boolean isAdvanced = false;
    
    private JSONObject createdFactoryJSON;    
    
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
        
        getLatestCommitId();        
    }
    
    /**
     * Fetch ID of last commit.
     */
    private void getLatestCommitId() {
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
        factoryURL = SpinnetGenerator.getBaseFactoryURL() + "?" + //
                     VERSION_PARAMETER + "=" + CURRENT_VERSION + "&" + //
                     PROJECT_NAME + "=" + openedProject.getName() + "&" + //
                     WORKSPACE_NAME + "=" + Utils.getWorkspaceName() + "&" + //
                     VCS + "=git&" + //
                     VCS_URL + "=" + encodeQueryString(vcsURL) + "&" + //
                     COMMIT_ID + "=" + latestCommitId + "&" + //
                     ACTION_PARAMETER + "=" + DEFAULT_ACTION +"&" +//
                     PROJECT_TYPE + "=" + URL.encodeQueryString(openedProject.getProjectType());
        
        logFactoryCreated(UriUtils.fromString(factoryURL).asString());
        
        display = new CreateFactoryView();
        IDE.getInstance().openView(display.asView());
        bindDisplay();

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                createdFactoryJSON = null;
                previewCodeButton();
                //generateWebsitesSnippet();
                
                generateGitHubSnippet();
                generateDirectSharingSnippet();
            }
        });
    }
    
    /**
     * Notify server just after factory created.
     * 
     * @param factoryURL
     */
    private void logFactoryCreated(String factoryURL) {
//TODO check is this code actual anymore 
//        try {
//            FactoryClientService.getInstance().logFactoryCreated(vfs.getId(), openedProject.getId(), factoryURL,
//                   new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
//                       @Override
//                       protected void onSuccess(StringBuilder result) {
//                       }
//
//                       @Override
//                       protected void onFailure(Throwable exception) {
//                       }
//                   });
//        } catch (RequestException e) {
//            String errorMessage = (e.getMessage() != null && e.getMessage().length() > 0) ?
//                e.getMessage() : GitExtension.MESSAGES.initFailed();
//            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
//        }
    }
    
    private void loadUserDetails() {
        try {
            final String requestUrl = Utils.getAuthorizationContext() + "/private/organization/users?alias=" + IDE.user.getName();

            UserProfileUnmarshaller unmarshaller = new UserProfileUnmarshaller(new HashMap<String, String>());
            
            AsyncRequestCallback<Map<String, String>> callback = new AsyncRequestCallback<Map<String, String>>(unmarshaller) {
                @Override
                protected void onSuccess(Map<String, String> result) {
                    String email = IDE.user.getName();
                    display.setEmailFieldValue(email);
                    
                    String author = result.get("firstName") + " " + result.get("lastName");
                    display.setAuthorFieldValue(author);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    //TODO remove this stub
//                    if (Location.getHost().indexOf("gavrik.codenvy-dev.com") >= 0 ||
//                        Location.getHost().indexOf("127.0.0.1:8080") >= 0) {
//
//                        display.setEmailFieldValue(IDE.user.getName());
//                        display.setAuthorFieldValue(IDE.user.getName());
//                        return;
//                    }
                    
                    Dialogs.getInstance().showError(FactoryExtension.LOCALIZATION_CONSTANTS.sendMailErrorGettingProfile());
                }
            };

            AsyncRequest.build(RequestBuilder.GET, requestUrl)
                        .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
    
    
    /**
     * Binds display.
     */
    private void bindDisplay() {
        loadUserDetails();
        
        display.getCreateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createFactory();
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
                createdFactoryJSON = null;
                display.previousPage();
            }
        });
        
        display.getFinishButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
        
        display.setButtonStyleChangedHandler(new ButtonStyleChangedHandler() {
            @Override
            public void onButtonStyleChanged() {
                String uploadFile = display.getUploadImageFieldValue();
                if (uploadFile != null && !uploadFile.isEmpty()) {
                    display.enableDefaultStyleOptions(true);
                } else {
                    display.enableDefaultStyleOptions(false);
                }
                
                // show preview
                previewCodeButton();
                generateGitHubSnippet();
            }
        });
        
        display.setUploadImageFieldValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String path = display.getUploadImageFieldValue();
                
                if (path != null && !path.trim().isEmpty()) {
                    isAdvanced = true;
                    display.enableDefaultStyleOptions(true);
                } else {
                    isAdvanced = false;
                    display.enableDefaultStyleOptions(false);
                }

                previewCodeButton();
            }
        });
        
        final String factoryURLEscaped = encodeQueryString(factoryURL);
        
        /** A summary info to display in a special area in the bottom of Facebook's post. */
        final String facebookSummaryInfo = "Code, Build, Test and Deploy instantly using Codenvy";
        
        display.getShareFacebookButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open("https://www.facebook.com/sharer/sharer.php?s=100&p[url]="
                            + factoryURLEscaped
                            + "&p[images][0]=https://codenvy.com/images/logoCodenvy.png&p[title]=" + openedProject.getName() + " - Codenvy "
                            + "&p[summary]=" + facebookSummaryInfo,
                            "", "width=626,height=436");
            }
        });

        display.getShareGooglePlusButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open("https://plus.google.com/share?url=" + factoryURLEscaped, "",
                            "menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=600");
            }
        });

        display.getShareTwitterButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open("https://twitter.com/share?url=" + factoryURLEscaped + "&text=" + facebookSummaryInfo, "",
                            "menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=260,width=660");
            }
        });

        display.getShareEmailButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new SendMailEvent(factoryURL, openedProject.getName()));
            }
        });
        
        display.setOpenAfterLaunchFieldClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new OpenResourceEvent(new ResourceSelectedCallback() {
                    @Override
                    public void onResourceSelected(Item resource) {
                        if (resource != null) {
                            display.setOpenAfterLaunchFieldValue(resource.getPath());
                        }
                    }
                }));
            }
        });
    }
    
    private void previewAdvancedButton() {
        String jsURL = SpinnetGenerator.getCodeNowButtonJavascriptURL();
        
        String blankImage = "images/blank.png";
        
        String javascriptPreview = "" +
                "<script " +
                    "type=\"text/javascript\" " +
                    "language=\"javascript\" " +
                    "src=\"" + jsURL + "\" " +
                    "style=\"advanced\" " +
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
    
    private void previewDefaultButton(String style, String counterType, int previewOffsetLeft, int previewOffsetTop) {
        String jsURL = SpinnetGenerator.getCodeNowButtonJavascriptURL();
        
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
     * Generates spinnet content for embedding on websites.
     */
    private void previewCodeButton() {
        String uploadFile = display.getUploadImageFieldValue();
        if (uploadFile != null && !uploadFile.isEmpty()) {
            previewAdvancedButton();
            return;
        }

        String style = display.whiteStyleSelected() ? "white" : "dark";

        if (display.showCounterChecked() && display.verticalOrientationSelected()) {
            previewDefaultButton(style, "vertical", 111, 31);
            return;
        }
        
        if (display.showCounterChecked() && !display.verticalOrientationSelected()) {
            previewDefaultButton(style, "horizontal", 91, 51);
            return;
        }
        
        previewDefaultButton(style, "none", 111, 51);
    }

    /**
     * Generates content for embedding on Github.
     */
    private void generateGitHubSnippet() {
        String code = "[![alt](" + 
            SpinnetGenerator.getCodeNowGitHubImageURL(!display.whiteStyleSelected()) + ")](" + factoryURL + ")";
        display.gitHubSnippet().setValue(code);
    }
    
    /**
     * Generates content for direct sharing.
     */
    private void generateDirectSharingSnippet() {
        display.directSharingSnippet().setValue(factoryURL);
    }

    private void createFactory() {
        createdFactoryJSON = null;
        
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

        if (isAdvanced) {
            request.put("style", new JSONString("Advanced"));
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
        
        display.setCreateFactoryRequestContent(request.toString());
        display.createFactory(new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                IDE.fireEvent(new ExceptionThrownEvent(caught, "Factory cannot be created"));
            }

            @Override
            public void onSuccess(String result) {
                try {                    
                    createdFactoryJSON = JSONParser.parseStrict(result).isObject();
                    generateSpinnetsAfterFactoryCreation();
                    display.nextPage();
                } catch (Exception e) {
                    String response = result;
                    while (response.indexOf(". ") > 0) {
                        response = response.replace(". ", ".<br>");
                    }
                    
                    Dialogs.getInstance().showError("IDE", "Factory cannot be created.<br><br>" + response);
                }
            }
        });
    }

    private void generateSpinnetsAfterFactoryCreation() {
        String jsURL = SpinnetGenerator.getCodeNowButtonJavascriptURL();
        
        String self = null;
        
        JSONArray array = createdFactoryJSON.get("links").isArray();
        for (int i = 0; i < array.size(); i++) {
            JSONObject link = array.get(i).isObject();
            String rel = link.get("rel").isString().stringValue();
            if ("self".equals(rel)) {
                self = link.get("href").isString().stringValue();
            }
        }
        
        String script = "" +
            "<script " +
            "type=\"text/javascript\" " +
            "language=\"javascript\" " +
            "src=\"" + jsURL + "\" " +
            "factory=\"" + self + "\" " +
            "></script>";

        display.websitesSnippet().setValue(script);          
    }
    
}
