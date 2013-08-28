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
package org.exoplatform.ide.editor.java;

import com.codenvy.ide.commons.shared.TextUtils;
import com.google.collide.client.editor.folding.AbstractFoldRange;
import com.google.collide.client.editor.folding.FoldOccurrencesFinder;

import org.eclipse.jdt.client.core.ISourceRange;
import org.eclipse.jdt.client.core.SourceRange;
import org.eclipse.jdt.client.core.dom.*;
import org.exoplatform.ide.editor.shared.runtime.Assert;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.editor.shared.text.Region;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaFoldOccurrencesFinder implements FoldOccurrencesFinder {
    /**
     * Projection position that will return two foldable regions: one folding away
     * the region from after the '/**' to the beginning of the content, the other
     * from after the first content line until after the comment.
     */
    private static final class CommentPosition extends AbstractFoldRange {
        CommentPosition(int offset, int length) {
            super(offset, length);
        }

        /*
         * @see org.eclipse.jface.text.source.projection.IProjectionPosition#computeFoldingRegions(org.eclipse.jface.text.IDocument)
         */
        public IRegion[] computeProjectionRegions(IDocument document) throws BadLocationException {
            DocumentCharacterIterator sequence = new DocumentCharacterIterator(document, offset, offset + length);
            int prefixEnd = 0;
            int contentStart = findFirstContent(sequence, prefixEnd);

            int firstLine = document.getLineOfOffset(offset + prefixEnd);
            int captionLine = document.getLineOfOffset(offset + contentStart);
            int lastLine = document.getLineOfOffset(offset + length);

            Assert.isTrue(firstLine <= captionLine, "first folded line is greater than the caption line"); //$NON-NLS-1$
            Assert.isTrue(captionLine <= lastLine, "caption line is greater than the last folded line"); //$NON-NLS-1$

            IRegion preRegion;
            if (firstLine < captionLine) {
                //				preRegion= new Region(offset + prefixEnd, contentStart - prefixEnd);
                int preOffset = document.getLineOffset(firstLine);
                IRegion preEndLineInfo = document.getLineInformation(captionLine);
                int preEnd = preEndLineInfo.getOffset();
                preRegion = new Region(preOffset, preEnd - preOffset);
            } else {
                preRegion = null;
            }

            if (captionLine < lastLine) {
                int postOffset = document.getLineOffset(captionLine + 1);
                int postLength = offset + length - postOffset;
                if (postLength > 0) {
                    IRegion postRegion = new Region(postOffset, postLength);
                    if (preRegion == null) {
                        return new IRegion[]{postRegion};
                    }
                    return new IRegion[]{preRegion, postRegion};
                }
            }

            if (preRegion != null) {
                return new IRegion[]{preRegion};
            }

            return null;
        }

        /**
         * Finds the offset of the first identifier part within <code>content</code>.
         * Returns 0 if none is found.
         *
         * @param content
         *         the content to search
         * @param prefixEnd
         *         the end of the prefix
         * @return the first index of a unicode identifier part, or zero if none can
         *         be found
         */
        private int findFirstContent(final CharSequence content, int prefixEnd) {
            int lenght = content.length();
            for (int i = prefixEnd; i < lenght; i++) {
                if (TextUtils.isValidIdentifierCharacter(content.charAt(i))) {
                    return i;
                }
            }
            return 0;
        }


        /*
         * @see org.eclipse.jface.text.source.projection.IProjectionPosition#computeCaptionOffset(org.eclipse.jface.text.IDocument)
         */
        public int computeCaptionOffset(IDocument document) throws BadLocationException {
            DocumentCharacterIterator sequence = new DocumentCharacterIterator(document, offset, offset + length);
            return findFirstContent(sequence, 0);
        }
    }

    /**
     * Projection position that will return two foldable regions: one folding away
     * the lines before the one containing the simple name of the java element, one
     * folding away any lines after the caption.
     */
    private static final class JavaElementPosition extends AbstractFoldRange {

        private SimpleName fMember;

        public JavaElementPosition(int offset, int length, SimpleName member) {
            super(offset, length);
            Assert.isNotNull(member);
            fMember = member;
        }


        /*
         * @see org.eclipse.jface.text.source.projection.IProjectionPosition#computeFoldingRegions(org.eclipse.jface.text.IDocument)
         */
        public IRegion[] computeProjectionRegions(IDocument document) throws BadLocationException {
            int nameStart = offset;

            /*
             * The member's name range may not be correct. However, reconciling would trigger another element delta which would lead to
             * reentrant situations. Therefore, we optimistically assume that the name range is correct, but double check the received lines
             * below.
             */
            ISourceRange nameRange = new SourceRange(fMember.getStartPosition(), fMember.getLength());

            nameStart = nameRange.getOffset();

            int firstLine = document.getLineOfOffset(offset);
            int captionLine = document.getLineOfOffset(nameStart);
            int lastLine = document.getLineOfOffset(offset + length);

            /*
             * See comment above - adjust the caption line to be inside the entire folded region, and rely on later element deltas to
             * correct the name range.
             */
            if (captionLine < firstLine) {
                captionLine = firstLine;
            }
            if (captionLine > lastLine) {
                captionLine = lastLine;
            }

            IRegion preRegion;
            if (firstLine < captionLine) {
                int preOffset = document.getLineOffset(firstLine);
                IRegion preEndLineInfo = document.getLineInformation(captionLine);
                int preEnd = preEndLineInfo.getOffset();
                preRegion = new Region(preOffset, preEnd - preOffset);
            } else {
                preRegion = null;
            }

            if (captionLine < lastLine) {
                int postOffset = document.getLineOffset(captionLine + 1);
                int postLength = offset + length - postOffset;
                if (postLength > 0) {
                    IRegion postRegion = new Region(postOffset, postLength);
                    if (preRegion == null) {
                        return new IRegion[]{postRegion};
                    }
                    return new IRegion[]{preRegion, postRegion};
                }
            }

            if (preRegion != null) {
                return new IRegion[]{preRegion};
            }

            return null;
        }


        /** {@inheritDoc} */
        public int computeCaptionOffset(IDocument document) throws BadLocationException {
            int nameStart = offset;

            // need a reconcile here?

            nameStart = fMember.getStartPosition();
            return nameStart - offset;
        }

    }

    private class FoldFinder extends ASTVisitor {

        private List<AbstractFoldRange> folds = new ArrayList<AbstractFoldRange>();

        private List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();

        private IDocument document;

        private FoldFinder(IDocument document) {
            this.document = document;
        }

        /**
         * Aligns <code>region</code> to start and end at a line offset. The region's start is
         * decreased to the next line offset, and the end offset increased to the next line start or the
         * end of the document. <code>null</code> is returned if <code>region</code> is
         * <code>null</code> itself or does not comprise at least one line delimiter, as a single line
         * cannot be folded.
         *
         * @param region
         *         the region to align, may be <code>null</code>
         * @return a region equal or greater than <code>region</code> that is aligned with line
         *         offsets, <code>null</code> if the region is too small to be foldable (e.g. covers
         *         only one line)
         */
        protected final IRegion alignRegion(IRegion region) {
            if (region == null) {
                return null;
            }

            try {

                int start = document.getLineOfOffset(region.getOffset());
                int end = document.getLineOfOffset(region.getOffset() + region.getLength());
                if (start >= end) {
                    return null;
                }

                int offset = document.getLineOffset(start);
                int endOffset;
                if (document.getNumberOfLines() > end + 1) {
                    endOffset = document.getLineOffset(end + 1);
                } else {
                    endOffset = document.getLineOffset(end) + document.getLineLength(end);
                }

                return new Region(offset, endOffset - offset);

            } catch (BadLocationException x) {
                // concurrent modification
                return null;
            }
        }

        private void addNode(ASTNode node, SimpleName name) {
            IRegion iRegion = alignRegion(new Region(node.getStartPosition(), node.getLength()));
            if (iRegion != null) {
                folds.add(new JavaElementPosition(iRegion.getOffset(), iRegion.getLength(), name));
            }
        }

        @Override
        public boolean visit(EnumDeclaration node) {
            if (node.getParent() != unit) {
                addNode(node, node.getName());
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(MethodDeclaration node) {
            if (node.getJavadoc() != null) {
                int offset = node.getJavadoc().getStartPosition() + node.getJavadoc().getLength() + 1;
                IRegion iRegion = alignRegion(new Region(offset, node.getLength() - node.getJavadoc().getLength() - 1));
                if (iRegion != null) {
                    folds.add(new JavaElementPosition(iRegion.getOffset(), iRegion.getLength(), node.getName()));
                }
            } else {
                addNode(node, node.getName());
            }
            return true;
        }

        @Override
        public boolean visit(TypeDeclaration node) {
            if (node.getParent() != unit) {
                addNode(node, node.getName());
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(AnnotationTypeDeclaration node) {
            if (node.getParent() != unit) {
                addNode(node, node.getName());
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(Javadoc node) {
            addComment(node);
            return false;
        }

        private void addComment(Comment node) {
            IRegion iRegion = alignRegion(new Region(node.getStartPosition(), node.getLength()));
            if (iRegion != null) {
                folds.add(new CommentPosition(iRegion.getOffset(), iRegion.getLength()));
            }
        }

        @Override
        public boolean visit(BlockComment node) {
            addComment(node);
            return false;
        }

        @Override
        public boolean visit(ImportDeclaration node) {
            imports.add(node);
            return false;
        }

        //      @Override
        //      public boolean visit(AnonymousClassDeclaration node)
        //      {
        //         ASTNode parent = node.getParent();
        //         if (parent instanceof ClassInstanceCreation)
        //         {
        //            Type type = ((ClassInstanceCreation)parent).getType().;
        //           folds.add(new JavaElementPosition(node.getStartPosition(), node.getLength(), type.getName()));
        //         }
        //         return super.visit(node);
        //      }

    }

    private CompilationUnit unit;

    @Override
    public List<AbstractFoldRange> findPositions(IDocument document) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setUnitName("/P/org/test/CreateJavaClassPresenter.java");
        parser.setSource(document.get());
        parser.setIgnoreMethodBodies(true);
        parser.setBindingsRecovery(false);
        parser.setResolveBindings(false);
        ASTNode ast = parser.createAST(null);
        unit = (CompilationUnit)ast;
        FoldFinder finder = new FoldFinder(document);
        unit.accept(finder);

        unit.getCommentList();
        for (Iterator iterator = unit.getCommentList().iterator(); iterator.hasNext(); ) {
            Comment comment = (Comment)iterator.next();
            if (comment.isBlockComment()) {
                comment.accept(finder);
            }
        }
        List<ImportDeclaration> imports = finder.imports;
        if (imports.size() > 1) {
            ImportDeclaration importDeclaration = imports.get(0);


            ImportDeclaration lastImport = imports.get(imports.size() - 1);
            finder.folds.add(new JavaElementPosition(importDeclaration.getStartPosition(),
                                                     lastImport.getStartPosition() + lastImport.getLength() -
                                                     importDeclaration.getStartPosition() + 1,
                                                     ((QualifiedName)importDeclaration.getName()).getName()));
        }
        return finder.folds;
    }
}
