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
package com.codenvy.ide.texteditor.renderer;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
class DocumentPosition {

    private int line;

    private int column;

    /**
     * @param line
     * @param column
     */
    public DocumentPosition(int line, int column) {
        super();
        this.line = line;
        this.column = column;
    }

    /** @return line number starting from 0 */
    public int getLineNumber() {
        return line;
    }

    /** @return column number starting from 0 */
    public int getColumn() {
        return column;
    }

}
