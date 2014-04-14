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
package com.codenvy.ide.about;

import com.codenvy.ide.ui.window.Window;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * UI for {@link AboutView}.
 * 
 * @author Ann Shumilova
 */
@Singleton
public class AboutViewImpl extends Window implements AboutView {
    interface AboutViewImplUiBinder extends UiBinder<Widget, AboutViewImpl> {
    }

    @UiField
    Button                 btnOk;
    @UiField
    Label                  version;
    @UiField
    Label                  revision;
    @UiField
    Label                  buildTime;
    @UiField(provided = true)
    AboutLocalizationConstant   locale;
    
    private ActionDelegate delegate;
    

    @Inject
    public AboutViewImpl(AboutViewImplUiBinder uiBinder, AboutLocalizationConstant locale) {
        this.locale = locale;
        this.setTitle(locale.aboutViewTitle());
        this.setWidget(uiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
        btnOk.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setVersion(String version) {
        this.version.setText(version);
    }

    /** {@inheritDoc} */
    @Override
    public void setRevision(String revision) {
        this.revision.setText(revision);
    }

    /** {@inheritDoc} */
    @Override
    public void setTime(String time) {
        this.buildTime.setText(time);
    }

    @UiHandler("btnOk")
    void onBtnOkClick(ClickEvent event) {
        delegate.onOkClicked();
    }

    @Override
    protected void onClose() {
        //do nothing 
    }
}
