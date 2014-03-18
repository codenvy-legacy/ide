/*
 * CODENVY CONFIDENTIAL
 *  __________________
 *
 *   [2014] Codenvy, S.A.
 *   All Rights Reserved.
 *
 *  NOTICE:  All information contained herein is, and remains
 *  the property of Codenvy S.A. and its suppliers,
 *  if any.  The intellectual and technical concepts contained
 *  herein are proprietary to Codenvy S.A.
 *  and its suppliers and may be covered by U.S. and Foreign Patents,
 *  patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material
 *  is strictly forbidden unless prior written permission is obtained
 *  from Codenvy S.A..
 */

package com.codenvy.ide.ext.web.html.editor;

import com.codenvy.ide.ext.web.DefaultChainedCodeAssistProcessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Set;

/**
 * Allows to chain code assist processor for the default given content type.
 * It will delegate to sub processors.
 *
 * @author Florent Benoit
 */
@Singleton
public class DefaultCodeAssistProcessor extends DefaultChainedCodeAssistProcessor {

    /**
     * HTML code assist processors.
     */
    @Inject(optional = true)
    public DefaultCodeAssistProcessor(Set<HTMLCodeAssistProcessor> htmlCodeAssistProcessors) {
        super(htmlCodeAssistProcessors);
    }

}
