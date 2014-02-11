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
package org.exoplatform.ide.extension.samples.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.extension.samples.client.startpage.PremiumAccountInfoReceivedEvent;
import org.exoplatform.ide.extension.samples.client.startpage.PremiumAccountInfoReceivedHandler;

/** Contact Support control */
public class SupportControl extends SimpleControl implements IDEControl, PremiumAccountInfoReceivedHandler {
    private static final String ID = "Help/Contact Support";

    private static final String TITLE = "Contact Support";

    public SupportControl() {
        super(ID);
        setTitle(TITLE);
        setGroupName(DocumentationControl.SUPPORT_GROUP_ID);
        setVisible(true);
        setEnabled(true);
        setImages(SamplesClientBundle.INSTANCE.contact(), SamplesClientBundle.INSTANCE.contactDisabled());

        //getAttributes().put("onClick", "javascript:UserVoice.showPopupWidget();");
    }


    @Override
    public void initialize() {
        IDE.addHandler(PremiumAccountInfoReceivedEvent.TYPE, this);
    }

    @Override
    public void onPremiumAccountInfoReceived(PremiumAccountInfoReceivedEvent event) {
        getAttributes().put("onClick", event.isUserHasPremiumAccount() ? "javascript:UserVoice.showPopupWidget();"
                                                                       : "javascript:window.open('http://helpdesk.codenvy.com');");
    }
}