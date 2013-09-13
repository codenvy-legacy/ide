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
package org.eclipse.jdt.client;

import com.google.gwt.user.client.ui.IsWidget;

import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;

public interface AssistDisplay extends IsWidget {

    void moveSelectionUp();

    void moveSelectionDown();

    void moveSelectionToPreviousPage();

    void moveSelectionToNextPage();

    void proposalSelected();

    void cancelCodeAssistant();

    void setNewProposals(CompletionProposal[] proposals);

}