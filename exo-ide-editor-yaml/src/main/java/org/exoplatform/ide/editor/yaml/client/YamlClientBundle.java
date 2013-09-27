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
package org.exoplatform.ide.editor.yaml.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: YamlClientBundle.java May 29, 2012 3:50:10 PM azatsarynnyy $
 */
public interface YamlClientBundle extends ClientBundle {
    YamlClientBundle INSTANCE = GWT.create(YamlClientBundle.class);

    @Source("org/exoplatform/ide/editor/yaml/client/images/yaml.png")
    ImageResource yaml();

    @Source("org/exoplatform/ide/editor/yaml/client/images/yaml-disabled.png")
    ImageResource yamlDisabled();
}
