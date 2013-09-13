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
package com.google.collide.client.code.autocomplete.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;

import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: AutocompleteProposalWidget.java Sep 11, 2012 3:17:38 PM azatsarynnyy $
 */
public class ContentAssistProposalWidget extends Composite {
    private CompletionProposal completionProposal;

    private Grid grid;

    public ContentAssistProposalWidget(CompletionProposal completionProposal) {
        this.completionProposal = completionProposal;
        grid = new Grid(1, 2);
        grid.setWidget(0, 0, completionProposal.getImage());
        initWidget(grid);
    }

}
