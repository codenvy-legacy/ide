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
package org.exoplatform.ide.editor.api.codeassitant.ui;

import com.google.gwt.user.client.ui.FlowPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 4:41:26 PM evgen $
 */
public class AssistantImportDeclarationPanel extends FlowPanel {

    private List<TokenWidget> entries = new ArrayList<TokenWidget>();

    /**
     * Add a new child widget to the panel.
     *
     * @param w
     *         the widget to be added
     */
    public void addWidget(TokenWidget w) {
        entries.add(w);
        super.add(w);
    }

    /** @see com.google.gwt.user.client.ui.FlowPanel#clear() */
    @Override
    public void clear() {
        entries.clear();
        super.clear();
    }

    public List<TokenWidget> getEntries() {
        return entries;
    }
}
