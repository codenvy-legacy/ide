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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.ide.api.resources.ModelProvider;
import com.codenvy.ide.resources.model.Project;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Model provider for Java projects.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaProjectModelProvider implements ModelProvider {

    private final EventBus eventBus;

    /** @param eventBus */
    public JavaProjectModelProvider(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /** {@inheritDoc} */
    @Override
    public Project createProjectInstance() {
        return new JavaProject(eventBus);
    }

}
