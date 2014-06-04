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
package com.codenvy.ide;

import com.google.gwt.i18n.client.Constants;


/**
 * Represents application's build information.
 * 
 * @author Ann Shumilova
 */
public interface BuildInfo extends Constants {

    @Key("revision")
    @DefaultStringValue("xxx")
    String revision();

    @Key("buildTime")
    @DefaultStringValue("just now")
    String buildTime();

    @Key("version")
    @DefaultStringValue("zzz")
    String version();
}
