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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

import java.util.HashMap;
import java.util.Map;

/**
 * OpenSocial defines a data store that applications can use to read and write user-specific data.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 */
public class AppData {
    // TODO
    private Map<String, String> collection;

    public Map<String, String> getCollection() {
        if (collection == null) {
            collection = new HashMap<String, String>();
        }
        return collection;
    }

    public void setCollection(Map<String, String> collection) {
        this.collection = collection;
    }
}
