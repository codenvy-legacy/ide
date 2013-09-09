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

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */

public interface LogReaderMessages extends Messages {

    @Key("log.reader.next")
    String getNextLogButton();

    @Key("log.reader.prev")
    String getPrevLogButton();

    @Key("log.reader.refresh")
    String getRefreshLogButton();

    @Key("log.reader.offset")
    String getOffset();

    @Key("log.reader.limit")
    String getLimit();

    @Key("log.reder.settings.ok")
    String getOkButton();

    @Key("log.reder.settings.cancel")
    String getCancelButton();

    @Key("log.reder.settings.data")
    String getDate();

}
