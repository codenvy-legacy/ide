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
package org.eclipse.jdt.client;

import com.google.gwt.user.client.ui.ScrollPanel;

import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.user.client.ui.DockLayoutPanel;

import com.google.gwt.user.cellview.client.CellTree;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.google.gwt.user.client.ui.Button;

import com.google.gwt.user.client.ui.FlexTable;

import com.google.gwt.user.client.ui.Label;

import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.google.gwt.core.client.GWT;

import com.google.gwt.resources.client.TextResource;

import com.google.gwt.resources.client.ClientBundle;

import org.eclipse.jdt.client.astview.ASTTreeViewModel;
import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.core.CompletionRequestor;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;
import org.eclipse.jdt.client.internal.codeassist.CompletionEngine;
import org.eclipse.jdt.client.internal.codeassist.InternalCompletionProposal;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 5, 2012 5:15:04 PM evgen $
 */
public class JdtTest
{

   private DockLayoutPanel rootPanel;

   private Data d;

   public interface Data extends ClientBundle
   {

      @Source("org/eclipse/jdt/client/TypeDeclaration.txt")
      TextResource content();

      @Source("org/eclipse/jdt/client/TestClass.txt")
      TextResource testClass();
   }

   public static class CARequestor extends CompletionRequestor
   {

      private List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();

      /** @see org.eclipse.jdt.client.core.CompletionRequestor#accept(org.eclipse.jdt.client.core.CompletionProposal) */
      @Override
      public void accept(CompletionProposal proposal)
      {
         proposals.add(proposal);
      }

      /** @see org.eclipse.jdt.client.core.CompletionRequestor#completionFailure(org.eclipse.jdt.client.core.compiler.IProblem) */
      @Override
      public void completionFailure(IProblem problem)
      {
         super.completionFailure(problem);
         System.out.println(problem.getMessage());
      }
   }

   /** @see com.google.gwt.core.client.EntryPoint#onModuleLoad() */
   // @Override
   public void onModuleLoad()
   {
      // rootPanel = new DockLayoutPanel(Unit.PX);
      // RootLayoutPanel.get().add(rootPanel);
      // d = GWT.create(Data.class);
      // rootPanel.addNorth(new Button(new SafeHtmlBuilder().appendEscaped("Create AST").toSafeHtml(), new ClickHandler()
      // {
      //
      // @Override
      // public void onClick(ClickEvent event)
      // {
      // buildAst();
      // }
      // }), 30);
      //
      // rootPanel.addNorth(new Button("CodeAssistant", new ClickHandler()
      // {
      //
      // @Override
      // public void onClick(ClickEvent event)
      // {
      // codeAssistant();
      // }
      // }), 30);
      //
   }

   /**
    * 
    */
   protected void codeAssistant()
   {
      CARequestor requestor = new CARequestor();
      char[] content = d.testClass().getText().toCharArray();
      CompletionEngine e = new CompletionEngine(new DummyNameEnvirement(null), requestor, JavaCore.getOptions(), null);
      e.complete(new org.eclipse.jdt.client.compiler.batch.CompilationUnit(content, "TestClass", "UTF-8"),
         getCompletionPosition(content, 33, 39), 0);
      FlexTable table = new FlexTable();

      rootPanel.add(table);
      int i = 0;
      for (CompletionProposal p : requestor.proposals)
      {
         table.setWidget(++i, 0, new Label(getStringForProposal(p)));
      }
   }

   /**
    * @param p
    * @return
    */
   private String getStringForProposal(CompletionProposal p)
   {
      StringBuilder str = new StringBuilder();
      InternalCompletionProposal ip = (InternalCompletionProposal)p;
      str.append(p.getFlags()).append(" ");
      str.append(p.getName()).append('(').append("par").append(')');
      str.append(" : ").append(ip.getTypeName()).append(" - ").append(ip.getDeclarationTypeName());

      return str.toString();
   }

   private int getCompletionPosition(char[] content, int row, int col)
   {
      String s = new String(content);
      String[] strings = s.split("\n");
      if (strings.length < row)
         return 0;

      int pos = 0;

      for (int i = 0; i < row - 1; i++)
      {
         pos += strings[i].length() + 1;
      }
      return pos + col - 1;
   }

   /**
    * 
    */
   protected void buildAst()
   {
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setSource(d.testClass().getText().toCharArray());
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setUnitName("TestClass");
      parser.setEnvironment(new String[]{"fersf"}, new String[]{"wfer"}, new String[]{"UTF-8"}, true);
      parser.setResolveBindings(true);
      ASTNode ast = parser.createAST(null);
      CompilationUnit unit = (CompilationUnit)ast;
      CellTree.Resources res = GWT.create(CellTree.BasicResources.class);
      CellTree cellTree = new CellTree(new ASTTreeViewModel(unit), null, res);
      rootPanel.add(new ScrollPanel(cellTree));
   }

}
