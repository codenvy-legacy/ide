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
package org.exoplatform.ide.editor.codeassistant;

import com.google.gwt.resources.client.CssResource;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Codeassistant Feb 22, 2011 5:17:02 PM evgen $
 */
public interface CodeAssistantCss extends CssResource {

    @ClassName("exo-autocomplete-panel")
    String panelStyle();

    @ClassName("exo-autocomplete-description")
    String description();

    @ClassName("exo-autocomplete-list")
    String listStyle();

    @ClassName("exo-autocomplete-edit")
    String edit();
}
