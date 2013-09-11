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
package org.exoplatform.ide.editor.jsp.client.codemirror;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.CodeLine.CodeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.codemirror.CodeValidator;
import org.exoplatform.ide.editor.java.client.codemirror.JavaCodeValidator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class JspCodeValidator extends CodeValidator {

    List<? extends Token> javaCode;

    JavaCodeValidator javaCodeValidator = new JavaCodeValidator();

    /**
     * Updates list of code errors and error marks. Also updates the fqn of tokens within the tokenList
     *
     * @param tokenList
     */
    public List<CodeLine> getCodeErrorList(List<? extends Token> tokenList) {
        if (tokenList == null || tokenList.isEmpty()) {
            return new ArrayList<CodeLine>();
        }

        javaCode =
                extractCode((List<TokenBeenImpl>)tokenList, new LinkedList<TokenBeenImpl>(), MimeType.APPLICATION_JAVA);

        return javaCodeValidator.getCodeErrorList(javaCode);
    }

    @Override
    public CodeLine getImportStatement(List<? extends Token> tokenList, String fqn) {
        if (this.javaCode == null) {
            this.javaCode =
                    extractCode((List<TokenBeenImpl>)tokenList, new LinkedList<TokenBeenImpl>(), MimeType.APPLICATION_JAVA);
        }

        if (javaCodeValidator.shouldImportStatementBeInsterted((List<TokenBeenImpl>)javaCode, fqn)) {
            int appropriateLineNumber =
                    JavaCodeValidator.getAppropriateLineNumberToInsertImportStatement((List<TokenBeenImpl>)tokenList);

            if (appropriateLineNumber > 1) {
                return new CodeLine(CodeType.IMPORT_STATEMENT, "import " + fqn + ";\n", appropriateLineNumber);
            } else {
                return new CodeLine(CodeType.IMPORT_STATEMENT, "<%\n  import " + fqn + ";\n%>\n", 1);
            }
        }

        return null;
    }
}
