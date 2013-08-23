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
package org.exoplatform.ide.editor.client.api;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditoCapability Feb 9, 2011 4:30:38 PM evgen $
 */
public enum EditorCapability {

    /** Editor supports code indentation */
    FORMAT_SOURCE,

    /** Editor supports line numbering that is displaying line numbers at the left field of code */
    SHOW_LINE_NUMBERS,

    /** you can use method goToPosition() to set cursor in the any position in the current editor */
    SET_CURSOR_POSITION,

    /** Editor can delete current line */
    DELETE_LINES,

    /** Editor support find and replace feature */
    FIND_AND_REPLACE,

    /** Editor and opened file type support autocompletion feature */
    AUTOCOMPLETION,

    /** Editor and opened file type support outline feature */
    OUTLINE,

    /** Editor and opened file type support validation feature */
    VALIDATION,

    /** Editor supports the comment text */
    COMMENT_SOURCE,

    /** Editor supports code folding */
    CODE_FOLDING

}
