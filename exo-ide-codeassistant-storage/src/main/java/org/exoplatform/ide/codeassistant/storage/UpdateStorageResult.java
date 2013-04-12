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
package org.exoplatform.ide.codeassistant.storage;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: UpdateStorageResult.java Oct 29, 2012 vetal $
 */
public class UpdateStorageResult {

    private String erroMessage;

    private int exitCode;

    public UpdateStorageResult() {
        exitCode = 0;
    }

    /**
     * @param erroMessage
     * @param exitCode
     */
    public UpdateStorageResult(String erroMessage, int exitCode) {
        super();
        this.erroMessage = erroMessage;
        this.exitCode = exitCode;
    }

    /** @return the erroMessage */
    public String getErroMessage() {
        return erroMessage;
    }

    /**
     * @param erroMessage
     *         the erroMessage to set
     */
    public void setErroMessage(String erroMessage) {
        this.erroMessage = erroMessage;
    }

    /** @return the exitCode */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * @param exitCode
     *         the exitCode to set
     */
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

}
