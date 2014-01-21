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

/**
 * Documentation Control Button.
 * @author Vladislav Zhukovskii
 */
public class DocumentationControl extends SimpleControl implements IDEControl {
    private static final String ID = "Help/Documentation";

    private static final String TITLE = "Documentation";

    public static final String SUPPORT_GROUP_ID = "Support";

    public DocumentationControl() {
        super(ID);
        setTitle(TITLE);
        setGroupName(SUPPORT_GROUP_ID);
        setVisible(true);
        setEnabled(true);
        setImages(SamplesClientBundle.INSTANCE.help(), SamplesClientBundle.INSTANCE.helpDisabled());

        getAttributes().put("onClick", "javascript:window.open('http://docs.codenvy.com');");
    }


    @Override
    public void initialize() {
    }
}