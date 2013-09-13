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
package org.exoplatform.ide.extension.maven.client.control;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.extension.maven.client.BuilderClientBundle;
import org.exoplatform.ide.extension.maven.client.BuilderExtension;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;


/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: BuildAndPublishProjectControl.java Oct 18, 2012 vetal $
 */
@RolesAllowed("developer")
public class BuildAndPublishProjectControl extends BuildProjectControlAbstract

{
    public static final String ID = BuilderExtension.LOCALIZATION_CONSTANT.buildAndPublishProjectControlId();

    private static final String TITLE = BuilderExtension.LOCALIZATION_CONSTANT.buildAndPublishProjectControlTitle();

    private static final String PROMPT = BuilderExtension.LOCALIZATION_CONSTANT.buildAndPublishProjectControlPrompt();

    public BuildAndPublishProjectControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(BuilderClientBundle.INSTANCE.build(), BuilderClientBundle.INSTANCE.buildDisabled());
        setEvent(new BuildProjectEvent(true));
    }
}
