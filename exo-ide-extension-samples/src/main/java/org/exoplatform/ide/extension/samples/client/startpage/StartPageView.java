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
package org.exoplatform.ide.extension.samples.client.startpage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

/**
 * View for Welcome Page.
 * @author Oksana Vereshchaka
 */
public class StartPageView extends ViewImpl implements StartPagePresenter.Display {

    private static final String ID = "WelcomeViewId";

    private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.welcomeTitle();

    private static StartPageViewUiBinder uiBinder = GWT.create(StartPageViewUiBinder.class);

    interface StartPageViewUiBinder extends UiBinder<Widget, StartPageView> {
    }

    @UiField
    Anchor invitationsLink;

    @UiField
    Anchor cloneLink;

    @UiField
    Anchor projectLink;

    @UiField
    Anchor importLink;

    @UiField
    Anchor documentationLink;

    @UiField
    Anchor supportLink;
    
    @UiField
    Frame facebookFrame;
    
    @UiField
    Frame googleFrame;

    @UiField
    Image logo;

    public StartPageView() {
        super(ID, "editor", TITLE, new Image(SamplesClientBundle.INSTANCE.welcome()));
        add(uiBinder.createAndBindUi(this));
        
        if (facebookLikeURL() == null) {
            facebookFrame.setVisible(false);
        } else {
            facebookFrame.setUrl(UriUtils.fromString(facebookLikeURL()));            
        }
        
        if (googleLikeURL() == null) {
            googleFrame.setVisible(false);
        } else {
            googleFrame.setUrl(UriUtils.fromString(googleLikeURL()));
            googleFrame.getElement().setAttribute("scrolling", "no");            
        }
    }
    
    /**
     * Returns URL to
     * 
     * @return
     */
    private static native String facebookLikeURL() /*-{
        return $wnd["facebook_like_url"];
    }-*/;    

    /**
     * Returns URL to
     * 
     * @return
     */
    private static native String googleLikeURL() /*-{
        return $wnd["google_like_url"];
    }-*/;

    /** @see org.exoplatform.ide.client.StartPagePresenter.WelcomePresenter.Display#getCloneLink() */
    @Override
    public HasClickHandlers getCloneLink() {
        return cloneLink;
    }

    /** @see org.exoplatform.ide.extension.samples.client.startpage.StartPagePresenter.Display#getProjectLink() */
    @Override
    public HasClickHandlers getProjectLink() {
        return projectLink;
    }

    /** @see org.exoplatform.ide.extension.samples.client.startpage.StartPagePresenter.Display#getImportLink() */
    @Override
    public HasClickHandlers getImportLink() {
        return importLink;
    }

    @Override
    public HasClickHandlers getInvitationsLink() {
        return invitationsLink;
    }

    @Override
    public Anchor getSupportLink() {
        return supportLink;
    }

    @Override
    public void disableInvitationsLink() {
        invitationsLink.getElement().setAttribute("style", " color: grey;cursor: default; text-decoration: none;");
    }

}
