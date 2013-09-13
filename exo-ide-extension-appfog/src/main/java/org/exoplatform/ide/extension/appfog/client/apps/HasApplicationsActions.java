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
package org.exoplatform.ide.extension.appfog.client.apps;

import com.google.gwt.event.logical.shared.SelectionHandler;

import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface HasApplicationsActions {
    void addStartApplicationHandler(SelectionHandler<AppfogApplication> handler);

    void addStopApplicationHandler(SelectionHandler<AppfogApplication> handler);

    void addRestartApplicationHandler(SelectionHandler<AppfogApplication> handler);

    void addDeleteApplicationHandler(SelectionHandler<AppfogApplication> handler);
}
