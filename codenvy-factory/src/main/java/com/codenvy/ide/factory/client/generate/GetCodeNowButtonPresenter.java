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
package com.codenvy.ide.factory.client.generate;

import com.codenvy.ide.factory.client.FactoryClientService;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
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
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import static com.codenvy.ide.factory.client.FactorySpec10.*;
import static com.google.gwt.http.client.URL.encodeQueryString;

/**
 * Presenter to generate a CodeNow button.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GetCodeNowButtonPresenter.java Jun 11, 2013 12:17:04 PM azatsarynnyy $
 */
public class GetCodeNowButtonPresenter implements GetCodeNowButtonHandler, ViewClosedHandler, VfsChangedHandler,
                                      ProjectOpenedHandler, ProjectClosedHandler {

    /** A title to display in a special area in the top of Facebook's post. */
    private String socialPostTitle = "Check out my Codenvy project";

    /** A summary info to display in a special area in the bottom of Facebook's post. */
    private String facebookSummaryInfo = "Code, Build, Test and Deploy instantly using Codenvy";

//    /** Base URL for Codenvy Factory. */
//    private final String BASE_FACTORY_URL = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost()).setPath("factory").buildString();
    
    
    public interface Display extends IsView {

        //HasValue<Boolean> getShowCounterField();

//        /**
//         * Get 'Vertical' radio field.
//         * 
//         * @return {@link HasValue}
//         */
//        HasValue<Boolean> getVerticalStyleField();

//        /**
//         * Get 'Horizontal' radio field.
//         * 
//         * @return {@link HasValue}
//         */
//        HasValue<Boolean> getHorizontalStyleField();

        /**
         * Preview area is displayed to let the user see the style of configured CodeNow button.
         * 
         * @return frame to preview the CodeNow button
         */
        Frame getPreviewFrame();

        /**
         * Returns 'on Websites' field.
         * 
         * @return 'on Websites' field
         */
        HasValue<String> getWebsitesURLField();

        /**
         * Returns 'on GitHub Pages' field.
         * 
         * @return 'on GitHub Pages' field
         */
        HasValue<String> getGitHubURLField();

        /**
         * Returns 'Direct Sharing' field.
         * 
         * @return 'Direct Sharing' field
         */
        HasValue<String> getDirectSharingURLField();

        /**
         * Returns button to share factory URL on Facebook.
         * 
         * @return share to Facebook button
         */
        HasClickHandlers getShareFacebookButton();

        /**
         * Returns button to share factory URL on Google+.
         * 
         * @return share to Google+ button
         */
        HasClickHandlers getShareGooglePlusButton();

        /**
         * Returns button to share factory URL on Twitter.
         * 
         * @return share to Twitter button
         */
        HasClickHandlers getShareTwitterButton();

        /**
         * Returns button to share factory URL by e-mail.
         * 
         * @return share by e-mail button
         */
        HasClickHandlers getShareEmailButton();

        /**
         * Returns the 'Ok' button.
         * 
         * @return 'Ok' button
         */
        HasClickHandlers getOkButton();
    }

    /** Current virtual file system. */
    private VirtualFileSystemInfo vfs;

    /** Current project. */
    private ProjectModel          openedProject;

    /** Display. */
    private Display               display;

    /** A snippet to embed the 'CodeNow' button on web-pages. */
    private String                websitesSnippet;
    
    private String                  websitesPreviewSnippet;

    /** A snippet to embed the 'CodeNow' button on GitHub Pages. */
    private String                gitHubPagesSnippet;

    /** A Factory URL itself. */
    private String                factoryURL;

    private String                latestCommitId;

    private String                vcsURL;

    public GetCodeNowButtonPresenter() {
        IDE.addHandler(GetCodeNowButtonEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }    
    
    public void bindDisplay() {
        final String factoryURLEscaped = encodeQueryString(factoryURL);

//        display.getShowCounterField().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
//            @Override
//            public void onValueChange(ValueChangeEvent<Boolean> event) {
//                if (event.getValue() == true) {
//                    display.getPreviewFrame().setUrl(UriUtils.fromString(SpinnetGenerator.getCodeNowButtonJavascriptURL() + "?counter=true"));
//
//                    generateSnippetForWebsites(true, display.getVerticalStyleField().getValue());
//                    display.getWebsitesURLField().setValue(websitesSnippet);
//                } else if (event.getValue() == false) {
//                    display.getPreviewFrame().setUrl(UriUtils.fromString(SpinnetGenerator.getCodeNowButtonJavascriptURL()));
//
//                    generateSnippetForWebsites(false, display.getVerticalStyleField().getValue());
//                    display.getWebsitesURLField().setValue(websitesSnippet);
//                }
//            }
//        });

//        display.getVerticalStyleField().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
//            @Override
//            public void onValueChange(ValueChangeEvent<Boolean> event) {
//                if (event.getValue() == true) {
//                    generateSnippetForWebsites(display.getShowCounterField().getValue(), true);
//                    display.getWebsitesURLField().setValue(websitesSnippet);
//                }
//            }
//        });

//        display.getHorizontalStyleField().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
//            @Override
//            public void onValueChange(ValueChangeEvent<Boolean> event) {
//                if (event.getValue() == true) {
//                    generateSnippetForWebsites(display.getShowCounterField().getValue(), false);
//                    display.getWebsitesURLField().setValue(websitesSnippet);
//                }
//            }
//        });

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
                // Window.open("mailto:?subject=Codenvy Factory URL&body=" + factoryURLEscaped, "",
                // "menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=100,width=200");
                IDE.fireEvent(new SendMailEvent(factoryURLEscaped));
            }
        });

        display.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    private native void writeToPreviewFrame(Element ifrm, String content) /*-{
        //var ifrm = document.getElementById('myIframe');
        ifrm = (ifrm.contentWindow) ? ifrm.contentWindow : (ifrm.contentDocument.document) ? ifrm.contentDocument.document : ifrm.contentDocument;
        ifrm.document.open();
        ifrm.document.write(content);
        ifrm.document.close();        
    }-*/;
    
    private void openView() {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }

//        display.getShowCounterField().setValue(true);
//        display.getPreviewFrame().setUrl(
//                                         UriUtils.fromString(SpinnetGenerator.getCodeNowButtonJavascriptURL() + "?counter=true")
//            );
        
        writeToPreviewFrame(display.getPreviewFrame().getElement(), "" +
        		"<html>" +
        		"<head></head>" +
        		"<body style=\"margin: 0px; padding: 0px;\"><center>" + websitesPreviewSnippet + "</center></body>" +
        		"</html>" +
        		"");
        
//        display.getHorizontalStyleField().setValue(true);
        display.getWebsitesURLField().setValue(websitesSnippet);
        display.getGitHubURLField().setValue(gitHubPagesSnippet);
        display.getDirectSharingURLField().setValue(factoryURL);
    }


    /** {@inheritDoc} */
    @Override
    public void onGetCodeNowButton(GetCodeNowButtonEvent event) {
        getLatestCommitIdAndOpenView();
    }


    /** {@inheritDoc} */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
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

    private void getLatestCommitIdAndOpenView() {
        try {
            GitClientService.getInstance().log(vfs.getId(), openedProject.getId(), false,
                     new AsyncRequestCallback<LogResponse>(new LogResponseUnmarshaller(new LogResponse(), false)) {
                         @Override
                         protected void onSuccess(LogResponse result) {
                             if (result.getCommits().size() > 0) {
                                 latestCommitId = result.getCommits().get(0).getId();
                             }
                             getRepoUrlAndOpenView();
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

    private void getRepoUrlAndOpenView() {
        try {
            GitClientService.getInstance().getGitReadOnlyUrl(vfs.getId(), openedProject.getId(),
                   new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                       @Override
                       protected void onSuccess(StringBuilder result) {
                           vcsURL = result.toString();
                           generateSnippetsAndOpenView();
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

    private void generateSnippetsAndOpenView() {
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
        generateSnippetForWebsites(false, false);
        gitHubPagesSnippet = "[![alt](" + SpinnetGenerator.getCodeNowGitHubImageURL() + ")](" + factoryURL + ")";
        openView();
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

    private void generateSnippetForWebsites(boolean showCounter, boolean verticalStyle) {
        String jsURL = SpinnetGenerator.getCodeNowButtonJavascriptURL();
        
        websitesSnippet = "<script " +
        		"type=\"text/javascript\" " +
        		"language=\"javascript\" " +
        		"src=\"" + jsURL + "\" " +
        		"target=\"" + factoryURL + "\"></script>";
        
        websitesPreviewSnippet = "<script " +
            "type=\"text/javascript\" " +
            "language=\"javascript\" " +
            "src=\"" + jsURL + "\" ></script>";
        
    }

}
