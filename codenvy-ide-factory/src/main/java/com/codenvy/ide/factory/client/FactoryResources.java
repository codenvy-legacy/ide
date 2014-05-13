/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.factory.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Factory extension resources (css styles, images).
 * 
 * @author Ann Shumilova
 */
public interface FactoryResources extends ClientBundle {
    public interface FactoryCSS extends CssResource {
        String titleLabel();

        String label();
        
        String link();
        
        String generateButton();
        
        String factoryPanel();
        
        String smallButton();
        
        String input();
        
        String tooltip();
        
        String radiobutton();
        
        String previewFrame();
        
        String snippetImage();
        
        String social();
        
        String socialDelimeter();
    }

    @Source({"Factory.css", "com/codenvy/ide/api/ui/style.css"})
    FactoryCSS factoryCSS();
    
    
    @Source("factory/share-factory.svg")
    SVGResource shareFactory();
    
    @Source("factory/copy.svg")
    SVGResource copyButton();
    
    @Source("factory/help.svg")
    SVGResource helpButton();
    
    @Source("factory/html.svg")
    SVGResource html();
    
    @Source("factory/github.svg")
    SVGResource github();
    
    @Source("factory/facebook.svg")
    SVGResource facebook();
    
    @Source("factory/google-plus.svg")
    SVGResource googlePlus();
    
    @Source("factory/twitter.svg")
    SVGResource twitter();
    
    @Source("factory/email.svg")
    SVGResource email();
}
