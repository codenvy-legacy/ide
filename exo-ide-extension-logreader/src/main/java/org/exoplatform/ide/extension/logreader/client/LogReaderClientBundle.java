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
package org.exoplatform.ide.extension.logreader.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface LogReaderClientBundle extends ClientBundle {

    LogReaderClientBundle INSTANCE = GWT.create(LogReaderClientBundle.class);

    @Source("org/exoplatform/ide/extension/logreader/client/images/log-reader-disabled.png")
    ImageResource logReaderDisabled();

    @Source("org/exoplatform/ide/extension/logreader/client/images/log-reader.png")
    ImageResource logReader();

    @Source("org/exoplatform/ide/extension/logreader/client/images/clearOutput.png")
    ImageResource clearOutput();

    @Source("org/exoplatform/ide/extension/logreader/client/images/log-reder-settings.png")
    ImageResource logRederSettings();

    @Source("org/exoplatform/ide/extension/logreader/client/images/next_Disabled.png")
    ImageResource next_Disabled();

    @Source("org/exoplatform/ide/extension/logreader/client/images/next.png")
    ImageResource next();

    @Source("org/exoplatform/ide/extension/logreader/client/images/prev_Disabled.png")
    ImageResource prev_Disabled();

    @Source("org/exoplatform/ide/extension/logreader/client/images/prev.png")
    ImageResource prev();

    @Source("org/exoplatform/ide/extension/logreader/client/images/refresh_Disabled.png")
    ImageResource refresh_Disabled();

    @Source("org/exoplatform/ide/extension/logreader/client/images/refresh.png")
    ImageResource refresh();

}
