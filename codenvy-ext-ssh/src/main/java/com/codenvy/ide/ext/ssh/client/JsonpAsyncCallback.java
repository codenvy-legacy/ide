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
package com.codenvy.ide.ext.ssh.client;

import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created by The eXo Platform SAS.
 *
 * @param <T>
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public abstract class JsonpAsyncCallback<T> implements AsyncCallback<T> {
    private Loader loader;

    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    public Loader getLoader() {
        if (loader == null) {
            new EmptyLoader();
        }
        return loader;
    }
}