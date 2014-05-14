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
        
        void onParametersChanged();
        
        void onFacebookClicked(boolean isEncoded);
        
        void onTwitterClicked(boolean isEncoded);
        
        void onGooglePlusClicked(boolean isEncoded);
        
        void onMailClicked(boolean isEncoded);
        
    }

    void setNonEncodedLink(String link);

    void setEncodedLink(String link);

    boolean getVerticalAlign();

    boolean getHorizontalAlign();

    boolean getWhiteTheme();

    boolean getDarkTheme();

    boolean getShowCounter();

    void previewFactoryButton(String content);

    String getDescription();

    String getAuthor();

    String getOpenFile();

    String getFindReplace();

    String getExpirationDate();

    void showEncodedPanel(boolean isVisible);

    void showGenerateButton(boolean isVisible);
    
    void setStyleInitialState();
    
    void showSocialEncoded(boolean isVisible);
    
    void showSocialNonEncoded(boolean isVisible);
    
    void submitCreateFactoryForm(String content, AsyncCallback<String> callback);
}
