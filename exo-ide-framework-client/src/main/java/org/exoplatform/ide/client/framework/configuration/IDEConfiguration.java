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
package org.exoplatform.ide.client.framework.configuration;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class IDEConfiguration {

    private String context;

    private String loopbackServiceContext;

    private String gadgetURL = GWT.getModuleBaseURL();

    private String vfsId;

    private String vfsBaseUrl;

    private String hiddenFiles;

    public IDEConfiguration() {
    }


    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getLoopbackServiceContext() {
        return loopbackServiceContext;
    }

    public void setLoopbackServiceContext(String loopbackServiceContext) {
        this.loopbackServiceContext = loopbackServiceContext;
    }

    public String getGadgetURL() {
        return gadgetURL;
    }

    public void setGadgetURL(String gadgetURL) {
        this.gadgetURL = gadgetURL;
    }

    public String getVfsBaseUrl() {
        return vfsBaseUrl;
    }

    public void setVfsBaseUrl(String vfsBaseUrl) {
        this.vfsBaseUrl = vfsBaseUrl;
    }

    public String getVfsId() {
        return vfsId;
    }

    public void setVfsId(String vfsId) {
        this.vfsId = vfsId;
    }

    public String getHiddenFiles() {
        return hiddenFiles;
    }

    public void setHiddenFiles(String hiddenFiles) {
        this.hiddenFiles = hiddenFiles;
    }

}
