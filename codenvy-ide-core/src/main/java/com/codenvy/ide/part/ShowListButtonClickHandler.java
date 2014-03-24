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
package com.codenvy.ide.part;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Click handler of the show tabs list button.
 * 
 * @author Ann Shumilova
 */
public interface ShowListButtonClickHandler {
    
    /**
     * @param x x coordinate of the right bottom corner of the list button
     * @param y y coordinate of the right bottom corner of the list button 
     * @param callback callback is called when list is closed
     */
    void onShowListClicked(int x, int y, AsyncCallback<Void> callback);
}
