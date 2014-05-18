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
package com.codenvy.ide.factory.client.share;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.codenvy.ide.collections.Array;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * View for creating and sharing the Factory.
 * 
 * @author Ann Shumilova
 */
public interface ShareFactoryView extends View<ShareFactoryView.ActionDelegate> {

    public interface ActionDelegate extends BaseActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Get non encoded Social button. */
        void onNonEncodedSocialClicked(boolean isDown);

        /** Performs any actions appropriate in response to the user having pressed the Get encoded HTML button. */
        void onHtmlSnippetClicked(boolean isEncoded);

        /** Performs any actions appropriate in response to the user having pressed the Get encoded GitHub button. */
        void onGitHubSnippetClicked(boolean isEncoded);

        /** Performs any actions appropriate in response to the user having pressed the Get encoded iFrame button. */
        void onIFrameSnippetClicked(boolean isEncoded);

        /** Performs any actions appropriate in response to the user having pressed the Get encoded Social button. */
        void onEncodedSocialClicked(boolean isDown);

        /** Performs any actions appropriate in response to the user having pressed the generate encoded url button. */
        void onGenerateEncodedUrlClicked();

        /** Performs any actions appropriate in response to the user having changed the style of the Factory button. */
        void onFactoryButtonStyleChanged();

        /**
         * Called when files suggestions are requested.
         * 
         * @param query query string
         * @param callback callback
         */
        void onRequestFileSuggestions(String query, AsyncCallback<Array<String>> callback);
        
        /** Performs any actions appropriate in response to the user having changed the advanced parameters of the Factory button. */
        void onParametersChanged();
        
        /**
         * Performs any actions appropriate in response to the user having clicked share on Facebook button.
         * 
         * @param isEncoded is URL encoded or nonencoded
         */
        void onFacebookClicked(boolean isEncoded);
        
        /**
         * Performs any actions appropriate in response to the user having clicked share on Twitter button.
         * 
         * @param isEncoded is URL encoded or nonencoded
         */
        void onTwitterClicked(boolean isEncoded);
        
        /**
         * Performs any actions appropriate in response to the user having clicked share on Google Plus button.
         * 
         * @param isEncoded is URL encoded or nonencoded
         */
        void onGooglePlusClicked(boolean isEncoded);
        
        void onMailClicked(boolean isEncoded);
        
    }

    /**
     * Set the value of non encoded Factory url.
     * 
     * @param link
     */
    void setNonEncodedLink(String link);
    
    /**
     * @return {@link String} Factory's non encoded url value
     */
    String getNonEncodedLink();
    
    /**
     * Set the value of encoded Factory url.
     * 
     * @param link
     */
    void setEncodedLink(String link);

    /**
     * @return is vertical align of the Factory button
     */
    boolean getVerticalAlign();

    /**
     * @return is horizontal align of the Factory button
     */
    boolean getHorizontalAlign();

    /**
     * @return is white theme of the Factory button
     */
    boolean getWhiteTheme();

    /**
     * @return is dark theme of the Factory button
     */
    boolean getDarkTheme();

    /**
     * @return is show counter near factory button or not
     */
    boolean getShowCounter();

    /**
     * @param content factory button preview content
     */
    void previewFactoryButton(String content);

    /**
     * @return {@link String} Factory's description
     */
    String getDescription();

    /**
     * @return {@link String} Factory's author information
     */
    String getAuthor();

    /**
     * @return {@link String} the path of the file, that will be opened on Factory opened
     */
    String getOpenFile();

    /**
     * @return {@link String} find replace
     */
    String getFindReplace();

    /**
     * @return {@link String} Factory's expiration date
     */
    String getExpirationDate();

    /**
     * @param isVisible is show panel with encoded url
     */
    void showEncodedPanel(boolean isVisible);

    /**
     * @param isVisible is show Generate encoded url button
     */
    void showGenerateButton(boolean isVisible);
    
    /** Set the initial state of the Factory button's style. */
    void setStyleInitialState();
    
    /**
     * @param isVisible is show social buttons with encoded url
     */
    void showSocialEncoded(boolean isVisible);
    
    /**
     * @param isVisible is show social buttons with non encoded url
     */
    void showSocialNonEncoded(boolean isVisible);
    
    /**
     * Submit the create Factory form.
     * 
     * @param content content of the factory
     * @param callback on Factory creation callback
     */
    void submitCreateFactoryForm(String content, AsyncCallback<String> callback);
}
