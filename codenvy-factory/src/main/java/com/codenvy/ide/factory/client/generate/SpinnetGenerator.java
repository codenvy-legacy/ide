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
    public static String getCodeNowGitHubImageURL(boolean darkStyle) {
        String fileName = darkStyle ? "factory.png" : "factory-white.png";
        
        if (Location.getHost().indexOf("gavrik.codenvy-dev.com") >= 0) {
            return new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                .setPath("ide/" + Utils.getWorkspaceName() + "/_app/images/factory/" + fileName).buildString();
        } else {
            return new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                .setPath("images/factory/" + fileName).buildString();
        }
    }
    
    /**
     * Returns base URL for Codenvy Factory.
     * 
     * @return
     */
    public static String getBaseFactoryURL() {
        if (Location.getHost().indexOf("gavrik.codenvy-dev.com") >= 0) {
            return new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                .setPath("ide/tmp-dev-monit").buildString();
        } else {
            return new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
                .setPath("factory").buildString();
        }
    }
    
}
