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

    public OutlineUpdater(String filePath, OutlineModel outlineModel, JavaParserWorker worker) {
        this.outlineModel = outlineModel;
        worker.addOutlineUpdateHandler(filePath, this);
        root = JavaCodeBlock.make();
        root.setType(CodeBlock.ROOT_TYPE);
        root.setOffset(0);
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
