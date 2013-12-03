/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.util;

import com.codenvy.ide.ext.java.jdt.core.ToolFactory;
import com.codenvy.ide.ext.java.jdt.core.compiler.IScanner;
import com.codenvy.ide.ext.java.jdt.core.compiler.ITerminalSymbols;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.TargetSourceRangeComputer;
import com.codenvy.ide.ext.java.jdt.core.formatter.IndentManipulation;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.TokenScanner;

import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;

import java.util.HashMap;
import java.util.Map;

public class SelectionAwareSourceRangeComputer extends TargetSourceRangeComputer {

    private ASTNode[] fSelectedNodes;

    private int fSelectionStart;

    private int fSelectionLength;

    private Map<ASTNode, SourceRange> fRanges;

    private String fDocumentPortionToScan;

    public SelectionAwareSourceRangeComputer(ASTNode[] selectedNodes, Document document, int selectionStart,
                                             int selectionLength) {
        fSelectedNodes = selectedNodes;
        fSelectionStart = selectionStart;
        fSelectionLength = selectionLength;
        try {
            fDocumentPortionToScan = document.get(fSelectionStart, fSelectionLength);
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public SourceRange computeSourceRange(ASTNode node) {
        try {
            if (fRanges == null)
                initializeRanges();
            SourceRange result = fRanges.get(node);
            if (result != null)
                return result;
            return super.computeSourceRange(node);
        } catch (CoreException e) {
            // fall back to standard implementation
            fRanges = new HashMap<ASTNode, SourceRange>();
        }
        return super.computeSourceRange(node);
    }

    private void initializeRanges() throws CoreException {
        fRanges = new HashMap<ASTNode, SourceRange>();
        if (fSelectedNodes.length == 0)
            return;

        fRanges.put(fSelectedNodes[0], super.computeSourceRange(fSelectedNodes[0]));
        int last = fSelectedNodes.length - 1;
        fRanges.put(fSelectedNodes[last], super.computeSourceRange(fSelectedNodes[last]));

        IScanner scanner = ToolFactory.createScanner(true, false, false, false);
        char[] source = fDocumentPortionToScan.toCharArray();
        scanner.setSource(source);
        fDocumentPortionToScan = null; // initializeRanges() is only called once

        TokenScanner tokenizer = new TokenScanner(scanner);
        int pos = tokenizer.getNextStartOffset(0, false);

        ASTNode currentNode = fSelectedNodes[0];
        int newStart = Math.min(fSelectionStart + pos, currentNode.getStartPosition());
        SourceRange range = fRanges.get(currentNode);
        fRanges.put(currentNode, new SourceRange(newStart, range.getLength() + range.getStartPosition() - newStart));

        currentNode = fSelectedNodes[last];
        int scannerStart = currentNode.getStartPosition() + currentNode.getLength() - fSelectionStart;
        tokenizer.setOffset(scannerStart);
        pos = scannerStart;
        int token = -1;
        try {
            while (true) {
                token = tokenizer.readNext(false);
                pos = tokenizer.getCurrentEndOffset();
            }
        } catch (CoreException e) {
        }
        if (token == ITerminalSymbols.TokenNameCOMMENT_LINE) {
            int index = pos - 1;
            while (index >= 0 && IndentManipulation.isLineDelimiterChar(source[index])) {
                pos--;
                index--;
            }
        }

        int newEnd = Math.max(fSelectionStart + pos, currentNode.getStartPosition() + currentNode.getLength());
        range = fRanges.get(currentNode);
        fRanges.put(currentNode, new SourceRange(range.getStartPosition(), newEnd - range.getStartPosition()));
    }
}
