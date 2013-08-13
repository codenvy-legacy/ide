/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.factory.client.generate;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window.Location;

import org.exoplatform.ide.client.framework.util.Utils;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class SpinnetGenerator {

//  /** URL to CodeNow button. */
//  //private static final String CODE_NOW_BUTTON_URL = "/ide/" + Utils.getWorkspaceName() + "/_app/codenow-embed.html";
//  private static final String CODE_NOW_BUTTON_URL = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost()).setPath("factory/codenow-embed.html").buildString();
//
//  /** URL of image which will be used as link for CodeNow button for GitHub Pages. */
//  private static final String CODE_NOW_BUTTON_FOR_GITHUB_IMAGE_URL = "/ide/" + Utils.getWorkspaceName() + "/_app/codenow_gh.png";

    /**
     * Returns URL to CodeNow button template
     * 
     * @return
     */
    public static String getCodeNowButtonJavascriptURL() {
        String jsURL;
        
        if (Location.getHost().indexOf("localhost:8080") >= 0 ||
            Location.getHost().indexOf("127.0.0.1:8080") >= 0 ||
            Location.getHost().indexOf("gavrik.codenvy-dev.com") >= 0) {

            jsURL = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                .setPath("ide/_app/factory/factory.js").buildString();
        } else {
            jsURL = new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                .setPath("factory/factory.js").buildString();            
        }
        
        //jsURL = jsURL.substring(jsURL.indexOf("//"));
        return jsURL;
    }
    
    /**
     * Returns URL of image which will be used as link for CodeNow button for GitHub Pages.
     * 
     * @return
     */
    public static String getCodeNowGitHubImageURL() {
        return new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
            .setPath("/ide/" + Utils.getWorkspaceName() + "/_app/codenow_gh.png").buildString();
    }
    
    /**
     * Returns base URL for Codenvy Factory.
     * 
     * @return
     */
    public static String getBaseFactoryURL() {
        return new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
            .setPath("factory").buildString();
    }
    
}
