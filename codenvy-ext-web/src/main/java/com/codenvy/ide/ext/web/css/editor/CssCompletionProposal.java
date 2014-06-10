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
package com.codenvy.ide.ext.web.css.editor;

import com.codenvy.ide.api.ui.Icon;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.RegionImpl;
import com.codenvy.ide.text.edits.MalformedTreeException;
import com.codenvy.ide.text.edits.ReplaceEdit;
import com.codenvy.ide.texteditor.api.TextEditorOperations;
import com.codenvy.ide.texteditor.api.codeassistant.Completion;
import com.codenvy.ide.texteditor.api.codeassistant.CompletionProposal;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;


/**
 * {@link CompletionProposal} implementation for Css code assistant.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CssCompletionProposal implements CompletionProposal {

    private static final String PROPERTY_TERMINATOR = ";";
    private static final String PROPERTY_SEPARATOR  = ": ";
    private final String            name;
    private final CompletionType    type;
    private       InvocationContext context;
    private       int               jumpLength;
    private       int               selectLength;

    /** @param name */
    public CssCompletionProposal(String name, CompletionType type) {
        this.name = name;
        this.type = type;
    }

    /** @return  */
    private String computateInsertString() {
        selectLength = 0;
        if (type == CompletionType.CLASS) {
            // In this case implicit autocompletion workflow should trigger,
            // and so execution should never reach this point.
            Log.warn(getClass(), "Invocation of this method in not allowed for type CLASS");
        } else if (CompletionType.PROPERTY == type) {
            String addend = name + PROPERTY_SEPARATOR + PROPERTY_TERMINATOR;
            jumpLength = addend.length() - PROPERTY_TERMINATOR.length();
            return addend;
        } else if (CompletionType.VALUE == type) {
            int start = name.indexOf('<');
            int end = name.indexOf('>');
            if ((start >= 0) && (start < end)) {
                jumpLength = end + 1;
                selectLength = -1 * ((end + 1) - start);
            } else {
                jumpLength = name.length();
            }
            return name;
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Widget getAdditionalProposalInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayString() {
        return new SafeHtmlBuilder().appendEscaped(name).toSafeHtml().asString();
    }

    /** {@inheritDoc} */
    @Override
    public Icon getIcon() {
        if (type == CompletionType.PROPERTY)
            return new Icon("css.property", context.getResources().propertyCss());
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public char[] getTriggerCharacters() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAutoInsertable() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void getCompletion(CompletionCallback callback) {
        callback.onCompletion(new Completion() {
            /** {@inheritDoc} */
            @Override
            public void apply(Document document) {
                String insert = computateInsertString();
                ReplaceEdit e =
                        new ReplaceEdit(context.getOffset() - context.getPrefix().length(), context.getPrefix().length(), insert);
                try {
                    e.apply(document);
                    if (type == CompletionType.PROPERTY)
                        context.getEditor().doOperation(TextEditorOperations.CODEASSIST_PROPOSALS);
                } catch (MalformedTreeException e1) {
                    Log.error(getClass(), e1);
                } catch (BadLocationException e1) {
                    Log.error(getClass(), e1);
                }
            }

            /** {@inheritDoc} */
            @Override
            public Region getSelection(Document document) {
                return new RegionImpl(context.getOffset() + jumpLength - context.getPrefix().length(), selectLength);
            }
        });
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /**
     * @param context
     *         the context to set
     */
    public void setContext(InvocationContext context) {
        this.context = context;
    }
}
