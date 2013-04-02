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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.ide.editor.java.Breakpoint;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:39:59 AM Mar 28, 2012 evgen $
 */
public class CurrentEditorBreakPoint extends Breakpoint {

    private String filePath;


    /**
     * @param lineNumber
     * @param message
     */
    public CurrentEditorBreakPoint(int line, String message, String filePath) {
        super(Type.CURRENT, line, message);
        this.filePath = filePath;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CurrentEditorBreakPoint)
            return getLineNumber() == ((CurrentEditorBreakPoint)obj).getLineNumber();
        return false;
    }


    /** @param lineNumber */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

}
