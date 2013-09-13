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
package org.exoplatform.ide.extension.java.client;

import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.extension.java.client.datasource.ConfigureDatasourcePresenter;

/**
 * Java extension for IDE.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JavaExtension.java Jun 21, 2011 12:29:16 PM vereshchaka $
 */
public class JavaExtension extends Extension {

    @Override
    public void initialize() {
        new ConfigureDatasourcePresenter();
    }

}
