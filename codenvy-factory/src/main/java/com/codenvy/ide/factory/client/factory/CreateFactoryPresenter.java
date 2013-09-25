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

import com.codenvy.ide.factory.client.FactoryClientService;
import com.codenvy.ide.factory.client.generate.GetCodeNowButtonEvent;
import com.codenvy.ide.factory.client.generate.GetCodeNowButtonHandler;
import com.codenvy.ide.factory.client.generate.SendMailEvent;
import com.codenvy.ide.factory.client.generate.SpinnetGenerator;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.UriUtils;
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
 */
public class CreateFactoryPresenter implements GetCodeNowButtonHandler, ViewClosedHandler,
    VfsChangedHandler, ProjectOpenedHandler, ProjectClosedHandler {
    
    public interface StyleChangedHandler {
        
        void onRefresh();
        
    }
    
    public interface Display extends IsView {
        
        int getPageIndex();
        
        void nextPage();
        
        void previousPage();
        
        void setPreviewContent(String content);
        
        
        boolean showCounter();
        
        boolean isVerticalOrientation();
        
        boolean isWhiteStyle();
        
        
        HasValue<String> snippetWebsites();
        
        HasValue<String> snippetGitHub();
        
        HasValue<String> snippetDirectSharing();
        
        
        HasClickHandlers getShareFacebookButton();
        
        HasClickHandlers getShareGooglePlusButton();
        
        HasClickHandlers getShareTwitterButton();
        
        HasClickHandlers getShareEmailButton();
        
        
        HasClickHandlers getCancelButton();
        
        HasClickHandlers getCreateButton();
        
        HasClickHandlers getBackButton();
        
        HasClickHandlers getFinishButton();
        
        
        void addStyleChangedHandler(StyleChangedHandler handler);
        
        void addOpenAfterLaunchClickHandler(ClickHandler clickHandler);
        
        void setOpenAfterLaunch(String path);
        
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
    
    private Display display;
    
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
    
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }    

    @Override
    public void onGetCodeNowButton(GetCodeNowButtonEvent event) {
        if (display != null) {
            return;
        }
        
        getLatestCommitId();        
    }
    
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
                generateWebsitesSnippet();
                generateGitHubSnippet();
                generateDirectSharingSnippet();
            }
        });
        
    }
    
    private void logFactoryCreated(String factoryURL) {
        try {
            FactoryClientService.getInstance().logFactoryCreated(vfs.getId(), openedProject.getId(), factoryURL,
                   new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                       @Override
                       protected void onSuccess(StringBuilder result) {
                       }

                       @Override
                       protected void onFailure(Throwable exception) {
                       }
                   });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null && e.getMessage().length() > 0) ?
                e.getMessage() : GitExtension.MESSAGES.initFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

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
                generateWebsitesSnippet();
                generateGitHubSnippet();
                //generateDirectSharingSnippet();
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
        
        display.addOpenAfterLaunchClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new OpenResourceEvent(new ResourceSelectedCallback() {
                    @Override
                    public void onResourceSelected(Item resource) {
                        if (resource != null) {
                            display.setOpenAfterLaunch(resource.getPath());
                        }
                    }
                }));
            }
        });
    }
    
    private void generateWebsitesSnippet() {
        String jsURL = SpinnetGenerator.getCodeNowButtonJavascriptURL();
        String style = display.isWhiteStyle() ? "white" : "dark";
        boolean counter = display.showCounter();
        boolean vertical = display.isVerticalOrientation();

        String javascript = "" +
        		"<script " +
                    "type=\"text/javascript\" " +
                    "language=\"javascript\" " +
                    "src=\"" + jsURL + "\" " +
                    "style=\"" + style + "\" " +
                    (counter ? (vertical ? "counter=\"vertical\" " : "counter=\"horizontal\" ") : "") + 
                    "target=\"" + factoryURL + "\" " +
                   "></script>";
        
        display.snippetWebsites().setValue(javascript);

        String javascriptPreview = "" +
            "<script " +
                "type=\"text/javascript\" " +
                "language=\"javascript\" " +
                "src=\"" + jsURL + "\" " +
                "style=\"" + style + "\" " +
                (counter ? (vertical ? "counter=\"vertical\" " : " counter=\"horizontal\" ") : "") +
                //"target=\"" + factoryURL + "\"" +
               "></script>";
        
        int top = 35;
        if (display.showCounter() && display.isVerticalOrientation()) {
            top = 12;
        }
        
        int width = 77;
        if (display.showCounter() && !display.isVerticalOrientation()) {
            width = 118;
        }
        
        display.setPreviewContent("" +
            "<html>" +
            "<head></head>" +
            "<body style=\"margin: 0px; padding: 0px;\">" +
                "<div style=\"margin-left:auto; margin-right:auto; width:" + width + "px; height:21px; position:relative; top:" + top + "px;\">" +
                javascriptPreview +
                "</div>" +
            "</body>" +
            "</html>" +
            "");
    }
    
    private void generateGitHubSnippet() {
        String code = "[![alt](" + 
            SpinnetGenerator.getCodeNowGitHubImageURL(!display.isWhiteStyle()) + ")](" + factoryURL + ")";
        display.snippetGitHub().setValue(code);
    }
    
    private void generateDirectSharingSnippet() {
        display.snippetDirectSharing().setValue(factoryURL);
    }

}
