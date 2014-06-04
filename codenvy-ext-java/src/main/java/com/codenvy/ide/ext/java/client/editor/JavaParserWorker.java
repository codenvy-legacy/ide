/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.messages.ProblemLocationMessage;
import com.codenvy.ide.ext.java.messages.ProposalAppliedMessage;
import com.codenvy.ide.ext.java.messages.WorkerProposal;
import com.codenvy.ide.ext.java.messages.impl.WorkerCodeBlock;
import com.codenvy.ide.text.edits.TextEdit;

/**
 * @author Evgen Vidolob
 */
public interface JavaParserWorker {

    void dependenciesUpdated();

    void parse(String content, String fileName, String fileId, String packageName, String projectPath, WorkerCallback<IProblem> callback);

    void computeCAProposals(String content, int offset, String fileName, String projectPath, WorkerCallback<WorkerProposal> callback);

    void applyCAProposal(String id, ApplyCallback callback);

    void addOutlineUpdateHandler(String fileId, WorkerCallback<WorkerCodeBlock> callback);

    void computeQAProposals(String content, int offset, int selectionLength, boolean updatedContent,
                            JsoArray<ProblemLocationMessage> problems,
                            WorkerCallback<WorkerProposal> callback);

    void removeFanFromCache(String fqn);

    void format(int offset, int length, String content, FormatResultCallback callback);

    void preferenceFormatsettings(JsoStringMap<String> settings);

    public interface WorkerCallback<T> {
        void onResult(Array<T> problems);
    }

    public interface ApplyCallback {
        void onApply(ProposalAppliedMessage message);
    }

    public interface FormatResultCallback {
        void onApplyFormat(TextEdit edit);
    }

}
