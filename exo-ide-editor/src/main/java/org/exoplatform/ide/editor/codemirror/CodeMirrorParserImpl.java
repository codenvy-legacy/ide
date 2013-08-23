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
package org.exoplatform.ide.editor.codemirror;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

import org.exoplatform.ide.editor.api.EditorTokenListPreparedEvent;
import org.exoplatform.ide.editor.api.EditorTokenListPreparedHandler;
import org.exoplatform.ide.editor.api.Parser;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class CodeMirrorParserImpl extends Parser {

    GetTokenListInBackgroundCommand buildCommand = new GetTokenListInBackgroundCommand(this);

    public void init() {
    }

    /**
     * @param node
     * @param lineNumber
     * @param tokenList
     * @param hasParentParser
     *         indicates is parser calles by another parser, e.g. JavaScriptParser is called by HtmlParser
     * @return token list with tokens gathered from node chains from start node to <br>
     *         node
     */
    public TokenBeenImpl parseLine(JavaScriptObject node, int lineNumber, TokenBeenImpl currentToken,
                                   boolean hasParentParser) {
        return currentToken;
    }

    @Override
    public List<TokenBeenImpl> getTokenList(String editorId, JavaScriptObject editor) {
        if (editor == null) {
            return new ArrayList<TokenBeenImpl>();
        }

        if (buildCommand.isBusy()) {
            buildCommand.stop();
        }

        TokenBeenImpl rootToken = new TokenBeenImpl();
        rootToken.setSubTokenList(new ArrayList<TokenBeenImpl>());

        TokenBeenImpl currentToken = rootToken;

        init();

        // fix error when editor.nthLine(1) = null
        // parse first line
        if (Node.getFirstLine(editor) != null) {
            currentToken = parseLine(Node.getFirstLine(editor), 1, currentToken, false);
        }

        // parse lines from second
        if (Node.getLastLineNumber(editor) > 1) {
            for (int lineNumber = 2; lineNumber <= Node.getLastLineNumber(editor); lineNumber++) {
                JavaScriptObject node = Node.get(editor, lineNumber);

                // fix error when editor.nthLine(1) = null
                if (node == null) {
                    continue;
                }

                currentToken = parseLine(Node.getNext(node), lineNumber, currentToken, false);
            }
        }

        if (buildCommand.isBusy() && tokenListPreparedHandler != null) {
            tokenListPreparedHandler.onEditorTokenListPrepared(new EditorTokenListPreparedEvent(editorId, rootToken.getSubTokenList()));
            //eventBus.fireEvent(new EditorTokenListPreparedEvent(editorId, rootToken.getSubTokenList()));
        }

        return rootToken.getSubTokenList();
    }

    ;

    /**
     * Recognize break line node with name "BR" and type "whitespace"
     *
     * @param node
     * @return
     */
    public boolean isLineBreak(Node node) {
        return "whitespace".equals(node.getType()) && "BR".equals(node.getContent());
    }

    private static String possibleMimeType;

    /**
     * Recognize mimeType of line with lineNumber.
     *
     * @param targetLineNumber
     * @param tokenList
     * @return
     */
    public static String getLineMimeType(int targetLineNumber, List<TokenBeenImpl> tokenList) {
        if (tokenList == null || tokenList.size() == 0)
            return null;

        possibleMimeType = null;

        for (TokenBeenImpl token : tokenList) {
            if (token.getLineNumber() > targetLineNumber)
                break;

            searchLineMimeType(targetLineNumber, token);
        }

        return possibleMimeType;
    }

    private static void searchLineMimeType(int targetLineNumber, TokenBeenImpl currentToken) {
        if (targetLineNumber == currentToken.getLineNumber()) {
            possibleMimeType = currentToken.getMimeType();
        }

        // taking in mind the last token among them in the line
        else if (currentToken.getLastLineNumber() != 0 && targetLineNumber <= currentToken.getLastLineNumber()) {
            possibleMimeType = currentToken.getMimeType();
        }

        // search appropriate token among the sub token
        List<TokenBeenImpl> subTokenList = currentToken.getSubTokenList();

        if (subTokenList != null && subTokenList.size() != 0) {
            for (TokenBeenImpl token : subTokenList) {
                if (targetLineNumber < token.getLineNumber())
                    break;

                searchLineMimeType(targetLineNumber, token);
            }
        }
    }

    class GetTokenListInBackgroundCommand implements RepeatingCommand {
        boolean isBusy = false;

        boolean shouldStop = false;

        LinkedList<JavaScriptObject> lineNodeList;

        TokenBeenImpl currentToken;

        TokenBeenImpl rootToken;

        int lineNumber;

        JavaScriptObject cloneEditorContainer;

        List<TokenBeenImpl> preparedTokenList;

        boolean shouldRebuild = false;

        LinkedList<JavaScriptObject> lineNodeListToRebuild;

        int firstLineNumberToRebuild;

        private JavaScriptObject cloneEditorContainerToRebuild;

        private CodeMirrorParserImpl parser;

        private String editorId;

        public GetTokenListInBackgroundCommand(CodeMirrorParserImpl parser) {
            this.parser = parser;
        }

        private void init() {
            isBusy = true;
            shouldStop = false;

            preparedTokenList = new ArrayList<TokenBeenImpl>();

            rootToken = new TokenBeenImpl();
            rootToken.setSubTokenList(preparedTokenList);

            currentToken = rootToken;

            parser.init();
        }

        /**
         * Start building token list
         *
         * @param editor
         */
        public void start(JavaScriptObject editor) {
            // container substitution is needed to get container clone and the links on the started node of each line of this clone
            // without getting clone of entire editor (this is almost impossible)
            JavaScriptObject initialEditorContainer = Node.getContainer(editor);
            if (initialEditorContainer == null) {
                return;
            }

            cloneEditorContainer = Node.getClone(initialEditorContainer);
            Node.setContainer(editor, cloneEditorContainer);

            lineNodeList = getLineNodeList(editor);

            // restore initial container node of editor
            Node.setContainer(editor, initialEditorContainer);

            // parse first line to fix error when editor.nthLine(1) = null
            lineNumber = 1;
            if (Node.getFirstLine(editor) == null) {
                lineNumber = 2;
            }

            // initialize
            init();

            // start parsing of content in background
            Scheduler.get().scheduleIncremental(this);
        }

        private LinkedList<JavaScriptObject> getLineNodeList(JavaScriptObject editor) {
            LinkedList<JavaScriptObject> lineNodeList = new LinkedList<JavaScriptObject>();

            // parse first line to fix error when editor.nthLine(1) = null
            if (Node.getFirstLine(editor) != null) {
                lineNodeList.addLast(Node.getFirstLine(editor));
            }

            int lastLineNumber = Node.getLastLineNumber(editor);

            if (lastLineNumber > 1) {
                for (int lineNumber = 2; lineNumber <= lastLineNumber; lineNumber++) {
                    JavaScriptObject node = Node.get(editor, lineNumber);

                    if (node == null) {
                        return null;
                    }

                    lineNodeList.addLast(Node.getNext(node));
                }
            }

            return lineNodeList;
        }

        /** @see com.google.gwt.core.client.Scheduler.RepeatingCommand#execute() */
        public boolean execute() {
            if (shouldStop == true) {
                interrupt();
                return false;
            }

            if (!lineNodeList.isEmpty()) {
                JavaScriptObject currentNode = lineNodeList.poll();

                // fix error when editor.nthLine(1) = null
                if (currentNode == null) {
                    return true;
                }

                currentToken = parseLine(currentNode, lineNumber, currentToken, false);
                lineNumber++;
                return true;
            }

            finish();

            if (shouldRebuild) {
                shouldRebuild = false;
                lineNodeList = lineNodeListToRebuild;
                lineNumber = firstLineNumberToRebuild;
                init();
                return true;
            }

            return false;
        }

        /** Interrupt building token list */
        private void interrupt() {
            isBusy = false;
            shouldStop = false;
        }

        /** Finalize building token list */
        private void finish() {
            preparedTokenList = rootToken.getSubTokenList();
            interrupt();
            if (tokenListPreparedHandler != null) {
                tokenListPreparedHandler.onEditorTokenListPrepared(new EditorTokenListPreparedEvent(editorId, preparedTokenList));
            }
        }

        /** Stop building token list */
        public void stop() {
            shouldStop = true;
        }

        public boolean isBusy() {
            return isBusy;
        }

        public void shouldRebuild(JavaScriptObject editor) {
            // container substitution is needed to get container clone and the links on the started node of each line of this clone
            // without getting clone of entire editor (this is almost impossible)
            JavaScriptObject initialEditorContainer = Node.getContainer(editor);
            if (initialEditorContainer == null) {
                this.shouldRebuild = false;
                return;
            }

            this.shouldRebuild = true;

            cloneEditorContainerToRebuild = Node.getClone(initialEditorContainer);
            Node.setContainer(editor, cloneEditorContainerToRebuild);

            lineNodeListToRebuild = getLineNodeList(editor);

            // restore initial container node of editor
            Node.setContainer(editor, initialEditorContainer);

            // parse first line to fix error when editor.nthLine(1) = null
            firstLineNumberToRebuild = 1;
            if (Node.getFirstLine(editor) == null) {
                firstLineNumberToRebuild = 2;
            }
        }

        public void setEditorId(String editorId) {
            this.editorId = editorId;
        }
    }

    private EditorTokenListPreparedHandler tokenListPreparedHandler;

    public void getTokenListInBackground(String editorId, JavaScriptObject editor,
                                         EditorTokenListPreparedHandler tokenListPreparedHandler) {
        if (editor == null) {
            return;
        }

        buildCommand.setEditorId(editorId);
        this.tokenListPreparedHandler = tokenListPreparedHandler;

        if (!buildCommand.isBusy()) {
            buildCommand.start(editor);
        } else {
            buildCommand.shouldRebuild(editor);
        }
    }

    @Override
    public void stopParsing() {
        if (buildCommand.isBusy()) {
            buildCommand.stop();
        }
    }

}
