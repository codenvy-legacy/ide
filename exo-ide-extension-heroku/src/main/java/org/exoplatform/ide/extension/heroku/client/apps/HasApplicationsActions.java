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
package org.exoplatform.ide.extension.heroku.client.apps;

import com.google.gwt.event.logical.shared.SelectionHandler;

/**
 * Application actions handlers.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 15, 2012 10:43:13 AM anya $
 */
public interface HasApplicationsActions {
    void addDeleteApplicationHandler(SelectionHandler<String> handler);

    void addRenameApplicationHandler(SelectionHandler<String> handler);

    void addChangeEnvironmentHandler(SelectionHandler<String> handler);

    void addApplicationInfoHandler(SelectionHandler<String> handler);

    void addImportApplicationHandler(SelectionHandler<String> handler);
}
