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
package com.codenvy.ide.ext.java.client.editor.outline;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.java.client.editor.JavaParserWorker;
import com.codenvy.ide.ext.java.messages.impl.WorkerCodeBlock;
import com.codenvy.ide.texteditor.api.outline.CodeBlock;
import com.codenvy.ide.texteditor.api.outline.OutlineModel;

/**
 * OutlineUpdater receive messages from worker and updates OutlineModel
 *
 * @author Evgen Vidolob
 */
public class OutlineUpdater implements JavaParserWorker.WorkerCallback<WorkerCodeBlock> {

    private final OutlineModel        outlineModel;
    private final JavaCodeBlock root;

    public OutlineUpdater(String fileId, OutlineModel outlineModel, JavaParserWorker worker) {
        this.outlineModel = outlineModel;
        worker.addOutlineUpdateHandler(fileId, this);
        root = JavaCodeBlock.make();
        root.setType(CodeBlock.ROOT_TYPE);
        root.setChildren(Collections.<JavaCodeBlock>createArray());
        outlineModel.updateRoot(root);
    }

    /** {@inheritDoc} */
    @Override
    public void onResult(Array<WorkerCodeBlock> problems) {
        Array<CodeBlock> blockArray = Collections.createArray();
        for (WorkerCodeBlock jcb : problems.asIterable()) {
            JavaCodeBlock codeBlock = jcb.cast();
            codeBlock.setParent(root);
            blockArray.add(codeBlock);
            if (codeBlock.getChildren() != null) {
                setParent(codeBlock, codeBlock.getChildren());
            }
        }
        outlineModel.setRootChildren(blockArray);
    }


    private void setParent(JavaCodeBlock parent, Array<CodeBlock> child) {
        for (CodeBlock block : child.asIterable()) {
            JavaCodeBlock b = (JavaCodeBlock)block;
            b.setParent(parent);
            if(b.getChildren() != null){
                setParent(b, b.getChildren());
            }
        }
    }
}
