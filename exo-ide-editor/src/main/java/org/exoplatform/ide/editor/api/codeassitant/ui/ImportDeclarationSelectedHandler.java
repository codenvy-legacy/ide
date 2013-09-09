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
package org.exoplatform.ide.editor.api.codeassitant.ui;

import org.exoplatform.ide.editor.api.codeassitant.Token;

/**
 * Callback for AssistImportDeclarationForm.
 * <p/>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 22, 2010 9:43:03 AM evgen $
 */
public interface ImportDeclarationSelectedHandler {

    /**
     * Fired if user select token.
     *
     * @param token
     *         that user select
     */
    void onImportDeclarationSelected(Token token);

    /**
     * Fired if AssistImportDeclarationForm closed without selected token. It may be click outside the AssistImportDeclarationForm
     * or "Esc" key pressed. Implementor must return focus in to editor.
     */
    void onImportCanceled();

}
