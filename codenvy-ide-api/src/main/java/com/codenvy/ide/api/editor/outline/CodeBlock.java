/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.api.editor.outline;

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
