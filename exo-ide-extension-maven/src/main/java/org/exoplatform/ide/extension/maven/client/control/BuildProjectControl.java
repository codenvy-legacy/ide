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
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.maven.client.BuilderClientBundle;
import org.exoplatform.ide.extension.maven.client.BuilderExtension;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;

/**
 * Control for build project by maven builder.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectControl.java Feb 17, 2012 3:51:08 PM azatsarynnyy $
 */
@RolesAllowed({"workspace/developer"})
public class BuildProjectControl extends BuildProjectControlAbstract implements IDEControl, ProjectClosedHandler,
                                                                                ProjectOpenedHandler {
    public static final String ID = BuilderExtension.LOCALIZATION_CONSTANT.buildProjectControlId();

    private static final String TITLE = BuilderExtension.LOCALIZATION_CONSTANT.buildProjectControlTitle();

    private static final String PROMPT = BuilderExtension.LOCALIZATION_CONSTANT.buildProjectControlPrompt();

    public BuildProjectControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(BuilderClientBundle.INSTANCE.build(), BuilderClientBundle.INSTANCE.buildDisabled());
        setEvent(new BuildProjectEvent(false, true));
    }
}
