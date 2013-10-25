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

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class SpinnetGenerator {
    
    /**
     * Returns URL to CodeNow button template
     * 
     * @return
     */
    public static String getFactoryButtonEmbedJSURL() {
        return new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
            .setPath("factory/resources/embed.js").buildString();        
    }

    /**
     * Returns URL to CodeNow button template
     * 
     * @return
     */
    public static String getCodeNowButtonJavascriptURL() {
        return new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
            .setPath("factory/resources/factory.js").buildString();        
    }
    
    
    /**
     * Returns URL of image which will be used as link for CodeNow button for GitHub Pages.
     * 
     * @return
     */
    public static String getCodeNowGitHubImageURL(boolean darkStyle) {
        String fileName = darkStyle ? "factory.png" : "factory-white.png";
        
//        //TODO Below block is needed only for development. Remove it when Factory 1.1 is done.
//        if (Location.getHost().indexOf("gavrik.codenvy-dev.com") >= 0 || Location.getHost().indexOf("127.0.0.1:8080") >= 0) {
//            return new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
//                .setPath("ide/" + Utils.getWorkspaceName() + "/_app/factory/resources/" + fileName).buildString();
//        }
        
        return new UrlBuilder().setProtocol(Location.getProtocol()).setHost(Location.getHost())
            .setPath("factory/resources/" + fileName).buildString();
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
