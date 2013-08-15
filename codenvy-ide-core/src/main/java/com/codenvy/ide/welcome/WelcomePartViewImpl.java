/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
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
package com.codenvy.ide.welcome;

import com.codenvy.ide.util.Utils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class WelcomePartViewImpl extends Composite implements WelcomePartView {
    interface WelcomePartViewImplUiBinder extends UiBinder<Widget, WelcomePartViewImpl> {
    }

    private static WelcomePartViewImplUiBinder ourUiBinder = GWT.create(WelcomePartViewImplUiBinder.class);

    @UiField
    Frame fbFrame;
    @UiField
    Frame googleFrame;
    @UiField(provided = true)
    final   WelcomePageResources        res;
    @UiField(provided = true)
    final   WelcomeLocalizationConstant locale;
    private ActionDelegate              delegate;

    @Inject
    protected WelcomePartViewImpl(WelcomePageResources res, WelcomeLocalizationConstant locale) {
        this.res = res;
        this.locale = locale;

        initWidget(ourUiBinder.createAndBindUi(this));

        fbFrame.setUrl(UriUtils.fromString("/ide/" + Utils.getWorkspaceName() + "/_app/fblike.html"));
        googleFrame.setUrl(UriUtils.fromString("/ide/" + Utils.getWorkspaceName() + "/_app/googleone.html"));
        googleFrame.getElement().setAttribute("scrolling", "no");
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }
}