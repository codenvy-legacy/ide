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
package com.codenvy.ide.ext.web.js.editor;

import com.codenvy.ide.ext.web.DefaultChainedCodeAssistProcessor;
import com.codenvy.ide.ext.web.html.editor.HTMLCodeAssistProcessor;
import com.codenvy.ide.texteditor.api.CodeAssistCallback;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.codeassistant.CompletionProposal;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Allows to chain code assist processor for a given content type.
 * It will delegate to sub processors.
 * @author Florent Benoit
 */
@Singleton
public class DefaultCodeAssistProcessor extends DefaultChainedCodeAssistProcessor {

    /**
     * Javascript code assist processors.(as it's optional it can't be in constructor)
     */
    @Inject(optional = true)
    protected void injectProcessors(Set<JsCodeAssistProcessor> jsCodeAssistProcessors) {
        setProcessors(jsCodeAssistProcessors);
    }


}
