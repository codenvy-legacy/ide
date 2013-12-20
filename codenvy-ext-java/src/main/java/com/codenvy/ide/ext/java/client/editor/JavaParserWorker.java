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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.messages.ProposalAppliedMessage;
import com.codenvy.ide.ext.java.messages.WorkerProposal;
import com.codenvy.ide.ext.java.messages.impl.WorkerCodeBlock;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface JavaParserWorker {

    public interface WorkerCallback<T> {
        void onResult(Array<T> problems);
    }

    public interface ApplyCallback {
        void onApply(ProposalAppliedMessage message);
    }

    void parse(String content, String fileName, String fileId, String packageName, WorkerCallback<IProblem> callback);

    void computeCAProposals(String content, int offset, String fileName, WorkerCallback<WorkerProposal> callback);

    void applyCAProposal(String id, ApplyCallback callback);

    void addOutlineUpdateHandler(String fileId, WorkerCallback<WorkerCodeBlock> callback);
}
