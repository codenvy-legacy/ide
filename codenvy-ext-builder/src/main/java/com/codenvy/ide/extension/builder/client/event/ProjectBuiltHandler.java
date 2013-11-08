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
package com.codenvy.ide.extension.builder.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Class, that implements this handler, will listen when project has built.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ProjectBuiltHandler.java Apr 3, 2012 12:23:21 PM azatsarynnyy $
 */
public interface ProjectBuiltHandler extends EventHandler {
    /**
     * @param event
     *         event generated when project is built
     */
    void onProjectBuilt(ProjectBuiltEvent event);
}