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
import com.codenvy.ide.factory.client.generate.SpinnetGenerator;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
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
    
    public interface StyleChangedHandler {
        
        void onRefresh();
        
    }
    
    public interface Display extends IsView {
        
        /**
         * Returns index of current page.
         * 
         * @return current page index
         */
        int getPageIndex();
        
        /**
         * Switches to next page.
         */
        void nextPage();
        
        /**
         * Switches to previous page.
         */
        void previousPage();
        
        /**
         * Sets new content in preview area.
         * 
         * @param content new content
         */
        void setPreviewContent(String content);
        
        /**
         * Determines whether Show counter field selected.
         * 
         * @return <b>true</b> if Show counter field selected, <b>false</b> otherwise
         */
        boolean isShowCounter();
        
        /**
         * Determines whether Vertical orientation field selected.
         * 
         * @return <b>true</b> if Vertical orientation field selected, <b>false</b> otherwise
         */
        boolean isVerticalOrientation();
        
        /**
         * Determines whether White style field selected.
         * 
         * @return <b>true</b> if White style field selected, <b>false</b> otherwise
         */
        boolean isWhiteStyle();
        
        void showButtonAdvanced(boolean showAdvanced);
        
        /**
         * Adds Style changed handler of Factory button.
         * 
         * @param handler handler
         */
        void addStyleChangedHandler(StyleChangedHandler handler);
        
        void setUploadImageValueChangeHandler(ValueChangeHandler<String> handler);
        
        String getUploadImageFieldValue();
        
        /**
         * Adds Click handler to Open after launch field.
         * 
         * @param clickHandler click handler
         */
        void setOpenAfterLaunchClickHandler(ClickHandler clickHandler);
        
        /**
         * Sets Open after launch field value.
         * 
         * @param path
         */
        void setOpenAfterLaunchValue(String path);
        
        /**
         * Returns snippet with content for embedding on websites.
         * 
         * @return snippet with content for embedding on websites
         */
        HasValue<String> snippetWebsites();
        
        /**
         * Returns snippet with content for embedding on Github.
         * 
         * @return snippet with content for embedding on Github
         */
        HasValue<String> snippetGitHub();
        
        /**
         * Returns snippet with content for direct sharing.
         * 
         * @return snippet with content for direct sharing
         */
        HasValue<String> snippetDirectSharing();
        
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
        
        display = new CreateFactoryView();
        IDE.getInstance().openView(display.asView());
        bindDisplay();

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                generateWebsitesSnippet();
                generateGitHubSnippet();
                generateDirectSharingSnippet();
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
                display.nextPage();
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
        
        display.addStyleChangedHandler(new StyleChangedHandler() {
            @Override
            public void onRefresh() {
                String uploadFile = display.getUploadImageFieldValue();
                if (uploadFile != null && !uploadFile.isEmpty()) {
                    display.showButtonAdvanced(true);
                } else {
                    display.showButtonAdvanced(false);
                }
                
                generateWebsitesSnippet();
                generateGitHubSnippet();
                //generateDirectSharingSnippet();
            }
        });
        
        display.setUploadImageValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String uploadFile = display.getUploadImageFieldValue();
                if (uploadFile != null && !uploadFile.isEmpty()) {
                    display.showButtonAdvanced(true);
                } else {
                    display.showButtonAdvanced(false);
                }

                generateWebsitesSnippet();
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
                IDE.fireEvent(new SendMailEvent(factoryURLEscaped, openedProject.getName()));
            }
        });
        
        display.setOpenAfterLaunchClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new OpenResourceEvent(new ResourceSelectedCallback() {
                    @Override
                    public void onResourceSelected(Item resource) {
                        if (resource != null) {
                            display.setOpenAfterLaunchValue(resource.getPath());
                        }
                    }
                }));
            }
        });
    }
    
    private void generateWebsitesAdvancedButton() {
        String jsURL = SpinnetGenerator.getCodeNowButtonJavascriptURL();
        
        display.snippetWebsites().setValue(
                    "<script " +
                        "type=\"text/javascript\" " +
                        "language=\"javascript\" " +
                        "src=\"" + jsURL + "\" " +
                        "style=\"advanced\" " +
                        "target=\"" + factoryURL + "\" " +
                        "img=\"\" " +
                    "></script>");
        
        String blankImage = "images/blank.png";
        
        String javascriptPreview = "" +
                "<script " +
                    "type=\"text/javascript\" " +
                    "language=\"javascript\" " +
                    "src=\"" + jsURL + "\" " +
                    "style=\"advanced\" " +
                    "img=\"" + blankImage + "\" " +
                "></script>";

        display.setPreviewContent("" +
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
    
    private void generateWebsitesDefaultButtonWithCounter(String style, String counterType, int previewOffsetLeft, int previewOffsetTop) {
        String jsURL = SpinnetGenerator.getCodeNowButtonJavascriptURL();
        
        String script = "" +
            "<script " +
                "type=\"text/javascript\" " +
                "language=\"javascript\" " +
                "src=\"" + jsURL + "\" " +
                "style=\"" + style + "\" " +
                "counter=\"" + counterType + "\" " +
                "target=\"" + factoryURL + "\" " +
               "></script>";
    
        display.snippetWebsites().setValue(script);
        
        String preview = "" +
            "<script " +
                "type=\"text/javascript\" " +
                "language=\"javascript\" " +
                "src=\"" + jsURL + "\" " +
                "style=\"" + style + "\" " +
                "counter=\"" + counterType + "\" " +
                "target=\"" + factoryURL + "\" " +
               "></script>";
        
        display.setPreviewContent("" +
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
    private void generateWebsitesSnippet() {
        String uploadFile = display.getUploadImageFieldValue();
        if (uploadFile != null && !uploadFile.isEmpty()) {
            generateWebsitesAdvancedButton();
            return;
        }
        
        String style = display.isWhiteStyle() ? "white" : "dark";

        if (display.isShowCounter() && display.isVerticalOrientation()) {
            generateWebsitesDefaultButtonWithCounter(style, "vertical", 111, 31);
            return;
        }
        
        if (display.isShowCounter() && !display.isVerticalOrientation()) {
            generateWebsitesDefaultButtonWithCounter(style, "horizontal", 91, 51);
            return;
        }
        
        generateWebsitesDefaultButtonWithCounter(style, "none", 111, 51);
    }
    
    /**
     * Generates content for embedding on Github.
     */
    private void generateGitHubSnippet() {
        String code = "[![alt](" + 
            SpinnetGenerator.getCodeNowGitHubImageURL(!display.isWhiteStyle()) + ")](" + factoryURL + ")";
        display.snippetGitHub().setValue(code);
    }
    
    /**
     * Generates content for direct sharing.
     */
    private void generateDirectSharingSnippet() {
        display.snippetDirectSharing().setValue(factoryURL);
    }

}
