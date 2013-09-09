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
package org.exoplatform.ide.editor.api.codeassitant;

/**
 * Properties, that can be applied to {@link Token}
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:31:49 PM evgen $
 */
public interface TokenProperties {

    String CLASS = "CLASS";

    String FQN = "FQN";

    String PACKAGE = "PACKAGE";

    String MODIFIERS = "MODIFIERS";

    String DECLARING_CLASS = "DECLARINGCLASS";

    String GENERIC_PARAMETER_TYPES = "GENERICPARAMETERTYPES";

    String GENERIC_RETURN_TYPE = "GENERICRETURNTYPE";

    String PARAMETER_TYPES = "PARAMETERTYPES";

    String RETURN_TYPE = "RETURNTYPE";

    /** Code, that will be inserted if token selected. Used only for template tokens. */
    String CODE = "CODE";

    /**
     * Used for template tokens. Short hint, that will be displayed near name in autocomplete form. Can help user quickly to
     * identify the purpose of template.
     */
    String SHORT_HINT = "SHORT-HINT";

    /** Used for template tokens. Full text of template, that will be inserted to code editor. Also, can be shown in javadoc window. */
    String FULL_TEXT = "FULL-TEXT";

    String LINE_NUMBER = "lineNumber";

    String MIME_TYPE = "mimeType";

    String SHORT_DESCRIPTION = "shortDescription";

    String FULL_DESCRIPTION = "fullDescription";

    /** Used for sub token list property. Contains {@link ArrayProperty} */
    String SUB_TOKEN_LIST = "subTokenList";

    String ELEMENT_TYPE = "elementType";

    String LAST_LINE_NUMBER = "lastLineNumber";

    String ANNOTATIONS = "annotations";

    String PARENT_TOKEN = "parentToken";

    String PARAMETERS = "parameters";

    String INITIALIZATION_STATEMENT = "initializationStatement";

    String GENERIC_EXCEPTIONTYPES = "genericExceptionTypes";

}
