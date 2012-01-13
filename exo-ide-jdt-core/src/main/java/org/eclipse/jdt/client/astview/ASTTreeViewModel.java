/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.eclipse.jdt.client.astview;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.google.gwt.cell.client.AbstractCell;

import com.google.gwt.view.client.ListDataProvider;

import com.google.gwt.view.client.TreeViewModel;

import org.eclipse.jdt.client.astview.views.ASTAttribute;
import org.eclipse.jdt.client.astview.views.Binding;
import org.eclipse.jdt.client.astview.views.CommentsProperty;
import org.eclipse.jdt.client.astview.views.GeneralAttribute;
import org.eclipse.jdt.client.astview.views.NodeProperty;
import org.eclipse.jdt.client.astview.views.ProblemsProperty;
import org.eclipse.jdt.client.astview.views.SettingsProperty;
import org.eclipse.jdt.client.astview.views.WellKnownTypesProperty;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.client.core.dom.Annotation;
import org.eclipse.jdt.client.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.client.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.client.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.ConstructorInvocation;
import org.eclipse.jdt.client.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.client.core.dom.Expression;
import org.eclipse.jdt.client.core.dom.FieldAccess;
import org.eclipse.jdt.client.core.dom.IAnnotationBinding;
import org.eclipse.jdt.client.core.dom.IBinding;
import org.eclipse.jdt.client.core.dom.IMethodBinding;
import org.eclipse.jdt.client.core.dom.ITypeBinding;
import org.eclipse.jdt.client.core.dom.IVariableBinding;
import org.eclipse.jdt.client.core.dom.ImportDeclaration;
import org.eclipse.jdt.client.core.dom.MemberRef;
import org.eclipse.jdt.client.core.dom.MemberValuePair;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.MethodInvocation;
import org.eclipse.jdt.client.core.dom.MethodRef;
import org.eclipse.jdt.client.core.dom.Name;
import org.eclipse.jdt.client.core.dom.PackageDeclaration;
import org.eclipse.jdt.client.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.client.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.client.core.dom.SuperFieldAccess;
import org.eclipse.jdt.client.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.client.core.dom.Type;
import org.eclipse.jdt.client.core.dom.TypeParameter;
import org.eclipse.jdt.client.core.dom.VariableDeclaration;
import org.eclipse.jdt.client.core.Signature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Jan 13, 2012 10:30:27 AM evgen $
 *
 */
public class ASTTreeViewModel implements TreeViewModel
{

   private static class AstCell extends AbstractCell<Object>
   {

      /**
       * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
       */
      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb)
      {
         if (value instanceof ASTNode)
         {
            getNodeType((ASTNode)value, sb);
         }
         else if (value instanceof ASTAttribute)
         {
            sb.appendEscaped(((ASTAttribute)value).getLabel());
         }
      }

      private void getNodeType(ASTNode node, SafeHtmlBuilder buf)
      {
         buf.appendEscaped(Signature.getSimpleName(node.getClass().getName()));
         buf.appendEscaped(" ["); //$NON-NLS-1$
         buf.append(node.getStartPosition());
         buf.appendEscaped(", "); //$NON-NLS-1$
         buf.append(node.getLength());
         buf.append(']');
         if ((node.getFlags() & ASTNode.MALFORMED) != 0)
         {
            buf.appendEscaped(" (malformed)"); //$NON-NLS-1$
         }
         if ((node.getFlags() & ASTNode.RECOVERED) != 0)
         {
            buf.appendEscaped(" (recovered)"); //$NON-NLS-1$
         }
      }
   }

   private CompilationUnit unit;

   private ListDataProvider<Object> astListDataProvider;

   /**
    * @param unit
    */
   public ASTTreeViewModel(CompilationUnit unit)
   {
      this.unit = unit;
      astListDataProvider = new ListDataProvider<Object>();
      List<Object> list = astListDataProvider.getList();
      list.addAll(getNodeChildren(unit));
   }

   /**
    * @see com.google.gwt.view.client.TreeViewModel#getNodeInfo(java.lang.Object)
    */
   @Override
   public <T> NodeInfo<?> getNodeInfo(T value)
   {
      //root
      if (value == null)
      {
         return new DefaultNodeInfo<Object>(astListDataProvider, new AstCell());
      }

      Object[] objects = getChildren(value);
      return new DefaultNodeInfo<Object>(new ListDataProvider<Object>(Arrays.asList(objects)), new AstCell());
   }

   private ArrayList<ASTAttribute> getNodeChildren(ASTNode node)
   {
      ArrayList<ASTAttribute> res = new ArrayList<ASTAttribute>();

      if (node instanceof Expression)
      {
         Expression expression = (Expression)node;
         ITypeBinding expressionTypeBinding = expression.resolveTypeBinding();
         res.add(createExpressionTypeBinding(node, expressionTypeBinding));

         // expressions:
         if (expression instanceof Name)
         {
            IBinding binding = ((Name)expression).resolveBinding();
            if (binding != expressionTypeBinding)
               res.add(createBinding(expression, binding));
         }
         else if (expression instanceof MethodInvocation)
         {
            MethodInvocation methodInvocation = (MethodInvocation)expression;
            IMethodBinding binding = methodInvocation.resolveMethodBinding();
            res.add(createBinding(expression, binding));
            String inferred = String.valueOf(methodInvocation.isResolvedTypeInferredFromExpectedType());
            res.add(new GeneralAttribute(expression, "ResolvedTypeInferredFromExpectedType", inferred)); //$NON-NLS-1$
         }
         else if (expression instanceof SuperMethodInvocation)
         {
            SuperMethodInvocation superMethodInvocation = (SuperMethodInvocation)expression;
            IMethodBinding binding = superMethodInvocation.resolveMethodBinding();
            res.add(createBinding(expression, binding));
            String inferred = String.valueOf(superMethodInvocation.isResolvedTypeInferredFromExpectedType());
            res.add(new GeneralAttribute(expression, "ResolvedTypeInferredFromExpectedType", inferred)); //$NON-NLS-1$
         }
         else if (expression instanceof ClassInstanceCreation)
         {
            ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation)expression;
            IMethodBinding binding = classInstanceCreation.resolveConstructorBinding();
            res.add(createBinding(expression, binding));
            String inferred = String.valueOf(classInstanceCreation.isResolvedTypeInferredFromExpectedType());
            res.add(new GeneralAttribute(expression, "ResolvedTypeInferredFromExpectedType", inferred)); //$NON-NLS-1$
         }
         else if (expression instanceof FieldAccess)
         {
            IVariableBinding binding = ((FieldAccess)expression).resolveFieldBinding();
            res.add(createBinding(expression, binding));
         }
         else if (expression instanceof SuperFieldAccess)
         {
            IVariableBinding binding = ((SuperFieldAccess)expression).resolveFieldBinding();
            res.add(createBinding(expression, binding));
         }
         else if (expression instanceof Annotation)
         {
            IAnnotationBinding binding = ((Annotation)expression).resolveAnnotationBinding();
            res.add(createBinding(expression, binding));
         }
         // Expression attributes:
         res.add(new GeneralAttribute(expression,
            "Boxing: " + expression.resolveBoxing() + "; Unboxing: " + expression.resolveUnboxing())); //$NON-NLS-1$ //$NON-NLS-2$
         res.add(new GeneralAttribute(expression,
            "ConstantExpressionValue", expression.resolveConstantExpressionValue())); //$NON-NLS-1$

         // references:
      }
      else if (node instanceof ConstructorInvocation)
      {
         IMethodBinding binding = ((ConstructorInvocation)node).resolveConstructorBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof SuperConstructorInvocation)
      {
         IMethodBinding binding = ((SuperConstructorInvocation)node).resolveConstructorBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof MethodRef)
      {
         IBinding binding = ((MethodRef)node).resolveBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof MemberRef)
      {
         IBinding binding = ((MemberRef)node).resolveBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof Type)
      {
         IBinding binding = ((Type)node).resolveBinding();
         res.add(createBinding(node, binding));

         // declarations:
      }
      else if (node instanceof AbstractTypeDeclaration)
      {
         IBinding binding = ((AbstractTypeDeclaration)node).resolveBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof AnnotationTypeMemberDeclaration)
      {
         IBinding binding = ((AnnotationTypeMemberDeclaration)node).resolveBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof EnumConstantDeclaration)
      {
         IBinding binding = ((EnumConstantDeclaration)node).resolveVariable();
         res.add(createBinding(node, binding));
         IBinding binding2 = ((EnumConstantDeclaration)node).resolveConstructorBinding();
         res.add(createBinding(node, binding2));
      }
      else if (node instanceof MethodDeclaration)
      {
         IBinding binding = ((MethodDeclaration)node).resolveBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof VariableDeclaration)
      {
         IBinding binding = ((VariableDeclaration)node).resolveBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof AnonymousClassDeclaration)
      {
         IBinding binding = ((AnonymousClassDeclaration)node).resolveBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof ImportDeclaration)
      {
         IBinding binding = ((ImportDeclaration)node).resolveBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof PackageDeclaration)
      {
         IBinding binding = ((PackageDeclaration)node).resolveBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof TypeParameter)
      {
         IBinding binding = ((TypeParameter)node).resolveBinding();
         res.add(createBinding(node, binding));
      }
      else if (node instanceof MemberValuePair)
      {
         IBinding binding = ((MemberValuePair)node).resolveMemberValuePairBinding();
         res.add(createBinding(node, binding));
      }

      List list = node.structuralPropertiesForType();
      for (int i = 0; i < list.size(); i++)
      {
         StructuralPropertyDescriptor curr = (StructuralPropertyDescriptor)list.get(i);
         res.add(new NodeProperty(node, curr));
      }

      if (node instanceof CompilationUnit)
      {
         CompilationUnit root = (CompilationUnit)node;
         //         res.add(new JavaElement(root, root.getJavaElement()));
         res.add(new CommentsProperty(root));
         res.add(new ProblemsProperty(root));
         res.add(new SettingsProperty(root));
         res.add(new WellKnownTypesProperty(root));
      }

      return res;
   }

   private Binding createBinding(ASTNode parent, IBinding binding)
   {
      String label = Binding.getBindingLabel(binding);
      return new Binding(parent, label, binding, true);
   }

   private Binding createExpressionTypeBinding(ASTNode parent, ITypeBinding binding)
   {
      String label = "> (Expression) type binding"; //$NON-NLS-1$
      return new Binding(parent, label, binding, true);
   }

   public Object getParent(Object child)
   {
      if (child instanceof ASTNode)
      {
         ASTNode node = (ASTNode)child;
         ASTNode parent = node.getParent();
         if (parent != null)
         {
            StructuralPropertyDescriptor prop = node.getLocationInParent();
            return new NodeProperty(parent, prop);
         }
      }
      else if (child instanceof ASTAttribute)
      {
         return ((ASTAttribute)child).getParent();
      }
      return null;
   }

   public Object[] getChildren(Object parent)
   {
      if (parent instanceof ASTAttribute)
      {
         return ((ASTAttribute)parent).getChildren();
      }
      else if (parent instanceof ASTNode)
      {
         return getNodeChildren((ASTNode)parent).toArray();
      }
      return new Object[0];
   }

   /**
    * @see com.google.gwt.view.client.TreeViewModel#isLeaf(java.lang.Object)
    */
   @Override
   public boolean isLeaf(Object parent)
   {
      //root
      if(parent == null)
         return false;
      
      return getChildren(parent).length == 0;
   }

}
