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
package org.eclipse.jdt.client.create;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.event.CreateJavaClassEvent;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jan 10, 2012 2:05:22 PM anya $
 */
@RolesAllowed({"developer"})
public class CreateJavaClassControl extends JavaControl {

    public CreateJavaClassControl() {
        super("File/New/New Java Class");
        setTitle("Java Class");
        setPrompt("Create Java Class");
        setNormalImage(JdtClientBundle.INSTANCE.newClassWizz());
        setDisabledImage(JdtClientBundle.INSTANCE.newClassWizzDisabled());
        setEvent(new CreateJavaClassEvent());
        setGroupName(GroupNames.NEW_SCRIPT);
    }

}
