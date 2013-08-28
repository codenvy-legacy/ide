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
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: WelcomeView.java Aug 25, 2011 12:33:32 PM vereshchaka $
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
    Frame fbFrame;
    
    @UiField
    Frame googleFrame;

    @UiField
    Image logo;

    public StartPageView() {
        super(ID, "editor", TITLE, new Image(SamplesClientBundle.INSTANCE.welcome()));
        add(uiBinder.createAndBindUi(this));
        fbFrame.setUrl(UriUtils.fromString("/ide/" + Utils.getWorkspaceName() +"/_app/fblike.html"));
        googleFrame.setUrl(UriUtils.fromString("/ide/" + Utils.getWorkspaceName() +"/_app/googleone.html"));
        googleFrame.getElement().setAttribute("scrolling", "no");
    }

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
    public void disableInvitationsLink() {
        invitationsLink.getElement().setAttribute("style", " color: grey;cursor: default; text-decoration: none;");
    }

}
