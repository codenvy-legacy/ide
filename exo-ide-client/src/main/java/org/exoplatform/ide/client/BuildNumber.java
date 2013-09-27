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
package org.exoplatform.ide.client;

/**
 * Interface to represent the messages contained in resource bundle: /home/vetal/eXo/eXoRpojects/exo-int/web-tools/trunk/
 * applications/gwt/devtool/src/main/resources/org/exoplatform/gadgets/devtool/client/BuildNumber.properties'.
 */
public interface BuildNumber extends com.google.gwt.i18n.client.Messages {

    /**
     * Translated "4417".
     *
     * @return translated "4417"
     */
    @DefaultMessage("")
    @Key("buildNumber")
    String buildNumber();

    /**
     * Translated "2009-12-21 15:05:49".
     *
     * @return translated "2009-12-21 15:05:49"
     */
    @DefaultMessage("")
    @Key("buildTime")
    String buildTime();

    /**
     * Translated "1.0-SNAPSHOT".
     *
     * @return translated "1.0-SNAPSHOT"
     */
    @DefaultMessage("1.0-SNAPSHOT")
    @Key("version")
    String version();

}
