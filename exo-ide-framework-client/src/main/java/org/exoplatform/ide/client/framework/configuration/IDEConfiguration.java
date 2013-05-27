/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.client.framework.configuration;

import com.google.gwt.core.client.GWT;

/**
 * Created by The eXo Platform SAS.
 *
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
