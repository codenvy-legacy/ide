/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.editor.java;

import com.google.collide.client.editor.folding.FoldOccurrencesFinder;
import com.google.collide.client.editor.folding.FoldRange;
import com.google.collide.shared.util.TextUtils;
import com.google.collide.shared.util.UnicodeUtils;

import org.eclipse.jdt.client.core.ISourceRange;
import org.eclipse.jdt.client.core.SourceRange;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.ASTVisitor;
import org.eclipse.jdt.client.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.client.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.client.core.dom.BlockComment;
import org.eclipse.jdt.client.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.client.core.dom.Comment;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.EnumDeclaration;
import org.eclipse.jdt.client.core.dom.ImportDeclaration;
import org.eclipse.jdt.client.core.dom.Javadoc;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.QualifiedName;
import org.eclipse.jdt.client.core.dom.SimpleName;
import org.eclipse.jdt.client.core.dom.Type;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;
import org.eclipse.jdt.client.core.util.CharUtil;
import org.eclipse.jdt.client.internal.corext.dom.ASTNodes;
import org.exoplatform.ide.editor.shared.runtime.Assert;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.editor.shared.text.Position;
import org.exoplatform.ide.editor.shared.text.Region;
import org.exoplatform.ide.editor.shared.text.projection.IProjectionPosition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaFoldOccurrencesFinder implements FoldOccurrencesFinder
{
   /**
    * Projection position that will return two foldable regions: one folding away
    * the region from after the '/**' to the beginning of the content, the other
    * from after the first content line until after the comment.
    */
   private static final class CommentPosition extends FoldRange
   {
      CommentPosition(int offset, int length)
      {
         super(offset, length);
      }

      /*
       * @see org.eclipse.jface.text.source.projection.IProjectionPosition#computeFoldingRegions(org.eclipse.jface.text.IDocument)
       */
      public IRegion[] computeProjectionRegions(IDocument document) throws BadLocationException
      {
         DocumentCharacterIterator sequence = new DocumentCharacterIterator(document, offset, offset + length);
         int prefixEnd = 0;
         int contentStart = findFirstContent(sequence, prefixEnd);

         int firstLine = document.getLineOfOffset(offset + prefixEnd);
         int captionLine = document.getLineOfOffset(offset + contentStart);
         int lastLine = document.getLineOfOffset(offset + length);

         Assert.isTrue(firstLine <= captionLine, "first folded line is greater than the caption line"); //$NON-NLS-1$
         Assert.isTrue(captionLine <= lastLine, "caption line is greater than the last folded line"); //$NON-NLS-1$

         IRegion preRegion;
         if (firstLine < captionLine)
         {
            //				preRegion= new Region(offset + prefixEnd, contentStart - prefixEnd);
            int preOffset = document.getLineOffset(firstLine);
            IRegion preEndLineInfo = document.getLineInformation(captionLine);
            int preEnd = preEndLineInfo.getOffset();
            preRegion = new Region(preOffset, preEnd - preOffset);
         }
         else
         {
            preRegion = null;
         }

         if (captionLine < lastLine)
         {
            int postOffset = document.getLineOffset(captionLine + 1);
            int postLength = offset + length - postOffset;
            if (postLength > 0)
            {
               IRegion postRegion = new Region(postOffset, postLength);
               if (preRegion == null)
               {
                  return new IRegion[]{postRegion};
               }
               return new IRegion[]{preRegion, postRegion};
            }
         }

         if (preRegion != null)
         {
            return new IRegion[]{preRegion};
         }

         return null;
      }

      /**
       * Finds the offset of the first identifier part within <code>content</code>.
       * Returns 0 if none is found.
       *
       * @param content   the content to search
       * @param prefixEnd the end of the prefix
       * @return the first index of a unicode identifier part, or zero if none can
       *         be found
       */
      private int findFirstContent(final CharSequence content, int prefixEnd)
      {
         int lenght = content.length();
         for (int i = prefixEnd; i < lenght; i++)
         {
            if (TextUtils.isValidIdentifierCharacter(content.charAt(i)))
            {
               return i;
            }
         }
         return 0;
      }


      /*
       * @see org.eclipse.jface.text.source.projection.IProjectionPosition#computeCaptionOffset(org.eclipse.jface.text.IDocument)
       */
      public int computeCaptionOffset(IDocument document) throws BadLocationException
      {
         DocumentCharacterIterator sequence = new DocumentCharacterIterator(document, offset, offset + length);
         return findFirstContent(sequence, 0);
      }
   }

   /**
    * Projection position that will return two foldable regions: one folding away
    * the lines before the one containing the simple name of the java element, one
    * folding away any lines after the caption.
    */
   private static final class JavaElementPosition extends FoldRange
   {

      private SimpleName fMember;

      public JavaElementPosition(int offset, int length, SimpleName member)
      {
         super(offset, length);
         Assert.isNotNull(member);
         fMember = member;
      }


      /*
       * @see org.eclipse.jface.text.source.projection.IProjectionPosition#computeFoldingRegions(org.eclipse.jface.text.IDocument)
       */
      public IRegion[] computeProjectionRegions(IDocument document) throws BadLocationException
      {
         int nameStart = offset;

            /* The member's name range may not be correct. However,
             * reconciling would trigger another element delta which would
				 * lead to reentrant situations. Therefore, we optimistically
				 * assume that the name range is correct, but double check the
				 * received lines below. */
         ISourceRange nameRange = new SourceRange(fMember.getStartPosition(), fMember.getLength());

         nameStart = nameRange.getOffset();


         int firstLine = document.getLineOfOffset(offset);
         int captionLine = document.getLineOfOffset(nameStart);
         int lastLine = document.getLineOfOffset(offset + length);

			/* see comment above - adjust the caption line to be inside the
          * entire folded region, and rely on later element deltas to correct
			 * the name range. */
         if (captionLine < firstLine)
         {
            captionLine = firstLine;
         }
         if (captionLine > lastLine)
         {
            captionLine = lastLine;
         }

         IRegion preRegion;
         if (firstLine < captionLine)
         {
            int preOffset = document.getLineOffset(firstLine);
            IRegion preEndLineInfo = document.getLineInformation(captionLine);
            int preEnd = preEndLineInfo.getOffset();
            preRegion = new Region(preOffset, preEnd - preOffset);
         }
         else
         {
            preRegion = null;
         }

         if (captionLine < lastLine)
         {
            int postOffset = document.getLineOffset(captionLine + 1);
            int postLength = offset + length - postOffset;
            if (postLength > 0)
            {
               IRegion postRegion = new Region(postOffset, postLength);
               if (preRegion == null)
               {
                  return new IRegion[]{postRegion};
               }
               return new IRegion[]{preRegion, postRegion};
            }
         }

         if (preRegion != null)
         {
            return new IRegion[]{preRegion};
         }

         return null;
      }


      /**
       * {@inheritDoc}
       */
      public int computeCaptionOffset(IDocument document) throws BadLocationException
      {
         int nameStart = offset;

         // need a reconcile here?

         nameStart = fMember.getStartPosition();
         return nameStart - offset;
      }

   }

   private class FoldFinder extends ASTVisitor
   {

      private List<FoldRange> folds = new ArrayList<FoldRange>();

      private List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();

      @Override
      public boolean visit(EnumDeclaration node)
      {
         if (node.getParent() != unit)
         {
            folds.add(new JavaElementPosition(node.getStartPosition(), node.getLength(), node.getName()));
         }
         return super.visit(node);
      }

      @Override
      public boolean visit(MethodDeclaration node)
      {
         if (node.getJavadoc() != null)
         {
            int offset = node.getJavadoc().getStartPosition() + node.getJavadoc().getLength() + 1;
            folds.add(new FoldRange(offset, node.getLength() - node.getJavadoc().getLength() - 1));
         }
         else
         {
            folds.add(new JavaElementPosition(node.getStartPosition(), node.getLength(), node.getName()));
         }
         return true;
      }

      @Override
      public boolean visit(TypeDeclaration node)
      {
         if (node.getParent() != unit)
         {
            folds.add(new JavaElementPosition(node.getStartPosition(), node.getLength(), node.getName()));
         }
         return super.visit(node);
      }

      @Override
      public boolean visit(AnnotationTypeDeclaration node)
      {
         if (node.getParent() != unit)
         {
            folds.add(new JavaElementPosition(node.getStartPosition(), node.getLength(), node.getName()));
         }
         return super.visit(node);
      }

      @Override
      public boolean visit(Javadoc node)
      {
         folds.add(new CommentPosition(node.getStartPosition(), node.getLength()));
         return false;
      }

      @Override
      public boolean visit(BlockComment node)
      {
         folds.add(new CommentPosition(node.getStartPosition(), node.getLength()));
         return false;
      }

      @Override
      public boolean visit(ImportDeclaration node)
      {
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
   public List<FoldRange> computePositions(IDocument document)
   {
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setUnitName("/P/org/test/CreateJavaClassPresenter.java");
      parser.setSource(document.get());
      parser.setIgnoreMethodBodies(true);
      parser.setBindingsRecovery(false);
      parser.setResolveBindings(false);
      ASTNode ast = parser.createAST(null);
      unit = (CompilationUnit)ast;
      FoldFinder finder = new FoldFinder();
      unit.accept(finder);

      unit.getCommentList();
      for (Iterator iterator = unit.getCommentList().iterator(); iterator.hasNext(); )
      {
         Comment comment = (Comment)iterator.next();
         if (comment.isBlockComment())
         {
            comment.accept(finder);
         }
      }
      List<ImportDeclaration> imports = finder.imports;
      if(imports.size() > 1)
      {
         ImportDeclaration importDeclaration = imports.get(0);


         ImportDeclaration lastImport = imports.get(imports.size() - 1);
         finder.folds.add(new JavaElementPosition(importDeclaration.getStartPosition(),
            lastImport.getStartPosition() + lastImport.getLength() - importDeclaration.getStartPosition() ,
            ((QualifiedName)importDeclaration.getName()).getName()));
      }
      return finder.folds;
   }
}
