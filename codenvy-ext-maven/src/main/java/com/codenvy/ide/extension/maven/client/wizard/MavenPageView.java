/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.maven.client.wizard;

import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

/**
 * @author Evgen Vidolob
 */
@ImplementedBy(MavenPageViewImpl.class)
public interface MavenPageView extends View<MavenPageView.ActionDelegate> {
    void setArtifactId(String artifact);

    void setGroupId(String group);

    void setVersion(String value);

    String getPackaging();

    public interface ActionDelegate{

        void onTextsChange();
    }

    String getGroupId();

    String getArtifactId();

    String getVersion();
}
