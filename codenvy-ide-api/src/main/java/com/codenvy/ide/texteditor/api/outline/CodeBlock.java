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
package com.codenvy.ide.texteditor.api.outline;

import com.codenvy.ide.json.JsonArray;

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
    JsonArray<CodeBlock> getChildren();

    /** @return parent of this code block, if code block has no parent then return <code>null</code> */
    CodeBlock getParent();

    /** @return the id of this block */
    String getId();
}
