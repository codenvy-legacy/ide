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
package org.exoplatform.ide.extension.maven.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 3, 2012 3:03:35 PM anya $
 */
public interface BuilderClientBundle extends ClientBundle {
    BuilderClientBundle INSTANCE = GWT.<BuilderClientBundle>create(BuilderClientBundle.class);

    @Source("org/exoplatform/ide/extension/maven/images/controls/build.png")
    ImageResource build();

    @Source("org/exoplatform/ide/extension/maven/images/controls/build_Disabled.png")
    ImageResource buildDisabled();

    @Source("org/exoplatform/ide/extension/maven/images/controls/clearOutput.png")
    ImageResource clearOutput();

    @Source("org/exoplatform/ide/extension/maven/images/controls/clearOutput_Disabled.png")
    ImageResource clearOutputDisabled();

    @Source("org/exoplatform/ide/extension/maven/images/controls/build_Stop.png")
    ImageResource buildStop();
}
