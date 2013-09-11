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
package org.exoplatform.ide.extension.cloudbees.client.list;

import com.google.gwt.event.logical.shared.SelectionHandler;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 22, 2011 evgen $
 */
public interface HasApplicationListActions extends ListGridItem<ApplicationInfo> {
    void addDeleteHandler(SelectionHandler<ApplicationInfo> handler);

    void addInfoHandler(SelectionHandler<ApplicationInfo> handler);
}
