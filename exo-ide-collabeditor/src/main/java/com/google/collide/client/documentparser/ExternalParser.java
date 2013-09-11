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
package com.google.collide.client.documentparser;

import com.codenvy.ide.json.shared.JsonArray;
import org.exoplatform.ide.editor.api.codeassitant.Token;

/**
 * External parser for CollabEditor. It may be used instead of or
 * in addition with internal CodeMirror parser.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExternalParser.java May 29, 2013 11:50:25 AM azatsarynnyy $
 *
 */
public interface ExternalParser {

    /**
     * Returns tokens array for the given <code>content</code>.
     * 
     * @param content text content to extract tokens
     * @return tokens array
     */
    JsonArray<? extends Token> getTokenList(String content);
}
