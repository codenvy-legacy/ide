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
package org.eclipse.jdt.client;

import com.codenvy.ide.client.util.logging.Log;
import com.google.collide.client.CollabEditor;
import com.google.collide.client.code.popup.EditorPopupController.Remover;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.AbstractImagePrototype.ImagePrototypeElement;

import org.eclipse.jdt.client.codeassistant.CompletionProposalLabelProvider;
import org.eclipse.jdt.client.core.BindingKey;
import org.eclipse.jdt.client.core.dom.*;
import org.eclipse.jdt.client.event.ViewJavadocEvent;
import org.eclipse.jdt.client.event.ViewJavadocHandler;
import org.eclipse.jdt.client.internal.text.JavaWordFinder;
import org.eclipse.jdt.client.ui.BindingLabelProvider;
import org.eclipse.jdt.client.ui.JavaElementLabels;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.java.hover.HoverResources;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavadocPresenter implements ViewJavadocHandler, EditorActiveFileChangedHandler, UpdateOutlineHandler {

    private Editor editor;

    private CompilationUnit unit;

    private CompletionProposalLabelProvider labelProvider;

    private BindingLabelProvider prov;

    private final HoverResources resources;

    private Remover remover;

    /**
     *
     */
    public JavadocPresenter(HandlerManager eventBus, HoverResources resources) {
        this.resources = resources;
        eventBus.addHandler(ViewJavadocEvent.TYPE, this);
        eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        eventBus.addHandler(UpdateOutlineEvent.TYPE, this);
        labelProvider = new CompletionProposalLabelProvider();
        prov = new BindingLabelProvider(JavaElementLabels.ALL_DEFAULT, 0);
    }

    /** @see org.eclipse.jdt.client.event.ViewJavadocHandler#onViewJavadoc(org.eclipse.jdt.client.event.ViewJavadocEvent) */
    @Override
    public void onViewJavadoc(ViewJavadocEvent event) {
        if (remover != null) {
            remover.remove();
            remover = null;
        }
        if (editor == null || unit == null || !(editor instanceof CollabEditor))
            return;
        IDocument document = editor.getDocument();
        int offset;
        try {
            offset = document.getLineOffset(editor.getCursorRow() - 1) + editor.getCursorColumn() - 1;
            IRegion region = JavaWordFinder.findWord(document, offset);
            Element info = getHoverInfo(editor, region);
            if (info != null)
                remover = ((CollabEditor)editor).showPopup(region, info);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
    }

    private Element getHoverInfo(Editor editor, IRegion hoverRegion) {
        NodeFinder nf = new NodeFinder(unit, hoverRegion.getOffset(), hoverRegion.getLength());
        ASTNode coveringNode = nf.getCoveredNode();
        if (coveringNode == null)
            return null;
        if (coveringNode.getNodeType() == ASTNode.MODIFIER)
            return null;
        ASTNode parentNode = coveringNode.getParent();
        if (parentNode instanceof SimpleType) {

            SimpleType st = (SimpleType)parentNode;
            Element docElement = getDocForType(st.resolveBinding());
            return docElement;

        }
        if (parentNode instanceof MethodInvocation) {
            MethodInvocation mi = (MethodInvocation)parentNode;
            IMethodBinding methodDeclaration = mi.resolveMethodBinding().getMethodDeclaration();
            String className = methodDeclaration.getDeclaringClass().getQualifiedName();
            BindingKey key = new BindingKey(methodDeclaration.getKey());
            String methodSignature = key.toSignature();
            String url =
                    className + "%23" + methodDeclaration.getName() + "%40" + (methodSignature).replaceAll("\\.", "/");
            Element div = DOM.createDiv();
            addImage(div, labelProvider.createMethodImage(methodDeclaration.getModifiers()));
            addFqn(div,
                   methodDeclaration.getReturnType().getName() + " " + className + "." + prov.getText(methodDeclaration));
            loadJavaDoc(url, false, div);
            return div;
        }
        if (coveringNode instanceof SimpleName) {
            SimpleName nn = (SimpleName)coveringNode;
            IBinding binding = nn.resolveBinding();
            if (binding.getKind() == IBinding.VARIABLE) {
                IVariableBinding var = (IVariableBinding)binding;
                if (var.isField()) {
                    String className = var.getDeclaringClass().getBinaryName();
                    String url = className + "%23" + nn.getFullyQualifiedName();
                    Element div = DOM.createDiv();
                    addImage(div, labelProvider.createMethodImage(var.getModifiers()));
                    addFqn(div, var.getType().getName() + " " + className + "." + prov.getText(var));
                    loadJavaDoc(url, false, div);
                    return div;
                }
            }
            if (binding.getKind() == IBinding.TYPE) {
                return getDocForType((ITypeBinding)binding);
            }
        }
        return null;
    }

    /**
     * @param type
     * @return
     */
    private Element getDocForType(ITypeBinding type) {
        Element docElement = DOM.createDiv();
        ImageResource image = labelProvider.getTypeImage(type.getModifiers());
        addImage(docElement, image);
        addFqn(docElement, type.getQualifiedName());
        loadJavaDoc(type.getBinaryName(), true, docElement);
        return docElement;
    }

    private void addFqn(Element docElement, String fqn) {
        Element fqnDiv = DOM.createDiv();
        fqnDiv.setInnerText(fqn);
        fqnDiv.addClassName(resources.hover().javaFqn());
        docElement.appendChild(fqnDiv);
        docElement.appendChild(DOM.createElement("br"));
    }

    /**
     * @param docElement
     * @param image
     */
    private void addImage(Element docElement, ImageResource image) {
        ImagePrototypeElement imageElement = AbstractImagePrototype.create(image).createElement();
        imageElement.getStyle().setFloat(Float.LEFT);
        docElement.appendChild(imageElement);
    }

    private void addTextToElement(String text, Element element) {
        Element div = DOM.createDiv();
        div.setInnerHTML(text);
        element.appendChild(div);
    }

    private void loadJavaDoc(String requestPart, boolean isClass, final Element docElement) {

        String url =
                JdtExtension.DOC_CONTEXT + requestPart + "&projectid="
                + JavaCodeController.get().getActiveFile().getProject().getId() + "&vfsid="
                + VirtualFileSystem.getInstance().getInfo().getId() + "&isclass=" + isClass;
        try {
            AsyncRequest.build(RequestBuilder.GET, url).send(
                    new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {

                        @Override
                        protected void onSuccess(StringBuilder result) {
                            addTextToElement(result.toString(), docElement);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            addTextToElement("Javadoc not found", docElement);
                        }
                    });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        editor = event.getEditor();
    }

    /** @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent) */
    @Override
    public void onUpdateOutline(UpdateOutlineEvent event) {
        unit = event.getCompilationUnit();
    }

}
