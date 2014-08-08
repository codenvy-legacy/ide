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
package com.codenvy.ide.api.texteditor.outline;

import com.codenvy.ide.collections.Array;

/**
 * An interface representing a continuous namespace in the source code. It may
 * be a function, a class, an object or just anonymous namespace.
 * <p/>
 * Code blocks have start offset relative to the file start, length,
 * type, children block and parent code block.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface CodeBlock {

    /** Type of root code block. */
    String ROOT_TYPE = "ROOT_TYPE";

    /** @return the type of this code block */
    String getType();

    /** @return the offset of this code block, relative to first char of his Document */
    int getOffset();

    /** @return the length of this code block */
    int getLength();

    /** @return the list of the nested code blocks */
    Array<CodeBlock> getChildren();

    /** @return parent of this code block, if code block has no parent then return <code>null</code> */
    CodeBlock getParent();

    /** @return the id of this block */
    String getId();
}
