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
package com.codenvy.ide.outline;

import com.codenvy.ide.api.texteditor.outline.CodeBlock;
import com.codenvy.ide.ui.tree.Tree.Listener;
import com.google.gwt.user.client.ui.IsWidget;

public interface OutlineView extends IsWidget {
    void renderTree();

    void rootChanged(CodeBlock newRoot);

    void setTreeEventHandler(Listener<CodeBlock> listener);

    void selectAndExpand(CodeBlock block);
}