/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
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
package com.codenvy.ide.api.parts;

import com.google.gwt.resources.client.ImageResource;

/**
 * The presentation of welcome page item. This class provides general information of item as like title, caption and icon.
 * Also it provides implementation of action what happened when item is clicked.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface WelcomeItemAction {
    /** @return title of item */
    String getTitle();

    /** @return caption of item */
    String getCaption();

    /** @return icon */
    ImageResource getIcon();

    /** Perform some action when item is clicked. */
    void execute();
}