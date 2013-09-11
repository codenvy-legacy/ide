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
package org.eclipse.jdt.client.outline;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;

import org.eclipse.jdt.client.core.dom.*;
import org.eclipse.jdt.client.internal.corext.dom.ASTNodes;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

import java.util.Iterator;

/**
 * Visitor is used for creating HTML code of widgets, that represent the AST nodes of different types.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 7, 2012 12:10:16 PM anya $
 */
public class CreateWidgetVisitor {
    /** HTML code of the widget. */
    private SafeHtmlBuilder html;

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.PackageDeclaration) */
    public boolean visit(PackageDeclaration node) {
        html = new SafeHtmlBuilder();
        html.appendHtmlConstant(getMainImage(JavaClientBundle.INSTANCE.packageItem()).toString());
        html.appendHtmlConstant(getTitleElement(node.getName().getFullyQualifiedName()).getString());
        return true;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.TypeDeclaration) */
    public boolean visit(TypeDeclaration node) {
        html = new SafeHtmlBuilder();
        int modifiers = node.getModifiers();

        // Check type is Class or Interface:
        Image image =
                node.isInterface() ? getMainImage(JavaClientBundle.INSTANCE.interfaceItem())
                                   : getMainImage(JavaClientBundle.INSTANCE.classItem());

        html.appendHtmlConstant(image.toString());
        html.appendHtmlConstant(getModifiersContainer(modifiers));

        Image modifImage = null;

        // Get the access modifier icon:
        if (Modifier.isPrivate(modifiers)) {
            modifImage = new Image(JavaClientBundle.INSTANCE.classPrivateItem());
        } else if (Modifier.isProtected(modifiers)) {
            modifImage = new Image(JavaClientBundle.INSTANCE.classProtectedItem());
        } else if (Modifier.isPublic(modifiers)) {
        } else {
            modifImage = new Image(JavaClientBundle.INSTANCE.classDefaultItem());
        }

        if (modifImage != null) {
            // TODO
            DOM.setStyleAttribute(modifImage.getElement(), "position", "absolute");
            DOM.setStyleAttribute(modifImage.getElement(), "marginLeft", "-10px");
            DOM.setStyleAttribute(modifImage.getElement(), "marginTop", "10px");
            modifImage.getElement().setAttribute("border", "0");
            html.appendHtmlConstant(modifImage.toString());
        }

        html.appendHtmlConstant(getTitleElement(node.getName().getIdentifier()).getString());
        return true;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.EnumDeclaration) */
    public boolean visit(EnumDeclaration node) {
        html = new SafeHtmlBuilder();
        int modifiers = node.getModifiers();
        Image image = getMainImage(JavaClientBundle.INSTANCE.enumItem());
        html.appendHtmlConstant(image.toString());
        html.appendHtmlConstant(getModifiersContainer(modifiers));

        Image modifImage = null;

        // Get the access modifier icon:
        if (Modifier.isPrivate(modifiers)) {
            modifImage = new Image(JavaClientBundle.INSTANCE.classPrivateItem());
        } else if (Modifier.isProtected(modifiers)) {
            modifImage = new Image(JavaClientBundle.INSTANCE.classProtectedItem());
        } else if (Modifier.isPublic(modifiers)) {
        } else {
            modifImage = new Image(JavaClientBundle.INSTANCE.classDefaultItem());
        }

        if (modifImage != null) {
            DOM.setStyleAttribute(modifImage.getElement(), "position", "absolute");
            DOM.setStyleAttribute(modifImage.getElement(), "marginLeft", "-10px");
            DOM.setStyleAttribute(modifImage.getElement(), "marginTop", "8px");
            modifImage.getElement().setAttribute("border", "0");
            html.appendHtmlConstant(modifImage.toString());
        }

        html.appendHtmlConstant(getTitleElement(node.getName().getIdentifier()).getString());
        return true;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.ImportDeclaration) */
    public boolean visit(ImportDeclaration node) {
        html = new SafeHtmlBuilder();
        html.appendHtmlConstant(getMainImage(JavaClientBundle.INSTANCE.importItem()).toString());
        html.appendHtmlConstant(getTitleElement(node.getName().getFullyQualifiedName()).getString());
        return true;
    }

    /**
     * @param node
     *         import group node
     * @return {@link Boolean}
     */
    public boolean visit(ImportGroupNode node) {
        html = new SafeHtmlBuilder();
        html.appendHtmlConstant(getMainImage(JavaClientBundle.INSTANCE.imports()).toString());
        html.appendHtmlConstant(getTitleElement(node.getName()).getString());
        return true;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.MethodDeclaration) */
    public boolean visit(MethodDeclaration node) {
        html = new SafeHtmlBuilder();
        int modifiers = node.getModifiers();
        Image image = null;

        // Get the access modifier icon:
        if (Modifier.isPrivate(modifiers)) {
            image = getMainImage(JavaClientBundle.INSTANCE.privateMethod());
        } else if (Modifier.isProtected(modifiers)) {
            image = getMainImage(JavaClientBundle.INSTANCE.protectedMethod());
        } else if (Modifier.isPublic(modifiers)) {
            image = getMainImage(JavaClientBundle.INSTANCE.publicMethod());
        } else {
            image = getMainImage(JavaClientBundle.INSTANCE.defaultMethod());
        }
        html.appendHtmlConstant(image.toString());

        // Add all modifiers container:
        html.appendHtmlConstant(getModifiersContainer(modifiers, node.isConstructor()));

        // Method title consists of method's name and the list of its parameters:
        String method = node.getName().getFullyQualifiedName();
        method += getMethodParams(node);
        html.appendHtmlConstant(getTitleElement(method).getString());

        // Constructor return type is null
        if (!node.isConstructor()) {
            // Append method's return type:
            html.appendHtmlConstant(getTypeElement(node.getReturnType2().toString()));
        }

        return true;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.FieldDeclaration) */
    public boolean visit(FieldDeclaration node) {
        html = new SafeHtmlBuilder();
        int modifiers = node.getModifiers();
        Image image = null;

        // Get the access modifier icon:
        if (Modifier.isPrivate(modifiers)) {
            image = getMainImage(JavaClientBundle.INSTANCE.privateField());
        } else if (Modifier.isProtected(modifiers)) {
            image = getMainImage(JavaClientBundle.INSTANCE.protectedField());
        } else if (Modifier.isPublic(modifiers)) {
            image = getMainImage(JavaClientBundle.INSTANCE.publicField());
        } else {
            image = getMainImage(JavaClientBundle.INSTANCE.defaultField());
        }

        html.appendHtmlConstant(image.toString());
        // Add all modifiers container:
        html.appendHtmlConstant(getModifiersContainer(modifiers));

        // Get field's name and type:
        if (node.fragments().iterator().hasNext()) {
            String name = ((VariableDeclarationFragment)node.fragments().iterator().next()).getName().getIdentifier();
            html.appendHtmlConstant(getTitleElement(name).getString());
            html.appendHtmlConstant(getTypeElement(node.getType().toString()));
        }

        return true;
    }

    public void visit(VariableDeclarationFragment node) {
        html = new SafeHtmlBuilder();
        if (node.getParent() instanceof FieldDeclaration) {
            FieldDeclaration parent = (FieldDeclaration)node.getParent();
            int modifiers = parent.getModifiers();
            Image image = null;

            // Get the access modifier icon:
            if (Modifier.isPrivate(modifiers)) {
                image = getMainImage(JavaClientBundle.INSTANCE.privateField());
            } else if (Modifier.isProtected(modifiers)) {
                image = getMainImage(JavaClientBundle.INSTANCE.protectedField());
            } else if (Modifier.isPublic(modifiers)) {
                image = getMainImage(JavaClientBundle.INSTANCE.publicField());
            } else {
                image = getMainImage(JavaClientBundle.INSTANCE.defaultField());
            }

            html.appendHtmlConstant(image.toString());
            // Add all modifiers container:
            html.appendHtmlConstant(getModifiersContainer(modifiers));

            html.appendHtmlConstant(getTitleElement(node.getName().getIdentifier()).getString());
            html.appendHtmlConstant(getTypeElement(parent.getType().toString()));
        }
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.AnnotationTypeDeclaration) */
    public boolean visit(AnnotationTypeDeclaration node) {
        html = new SafeHtmlBuilder();
        int modifiers = node.getModifiers();
        html.appendHtmlConstant(getMainImage(JavaClientBundle.INSTANCE.annotationItem()).toString());
        html.appendHtmlConstant(getModifiersContainer(modifiers));

        Image modifImage = null;

        // Get the access modifier icon:
        if (Modifier.isPrivate(modifiers)) {
            modifImage = new Image(JavaClientBundle.INSTANCE.classPrivateItem());
        } else if (Modifier.isProtected(modifiers)) {
            modifImage = new Image(JavaClientBundle.INSTANCE.classProtectedItem());
        } else if (Modifier.isPublic(modifiers)) {
        } else {
            modifImage = new Image(JavaClientBundle.INSTANCE.classDefaultItem());
        }

        if (modifImage != null) {
            // TODO
            DOM.setStyleAttribute(modifImage.getElement(), "position", "absolute");
            DOM.setStyleAttribute(modifImage.getElement(), "marginLeft", "-10px");
            DOM.setStyleAttribute(modifImage.getElement(), "marginTop", "10px");
            modifImage.getElement().setAttribute("border", "0");
            html.appendHtmlConstant(modifImage.toString());
        }

        html.appendHtmlConstant(getTitleElement(node.getName().getIdentifier()).getString());
        return true;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.AnnotationTypeMemberDeclaration) */
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        html = new SafeHtmlBuilder();
        html.appendHtmlConstant(getMainImage(JavaClientBundle.INSTANCE.publicMethod()).toString());
        // Add all modifiers container:
        html.appendHtmlConstant(getModifiersContainer(node.getModifiers(), false));

        // Method title consists of method's name and the list of its parameters:
        String method = node.getName().getFullyQualifiedName();
        method += "()";
        html.appendHtmlConstant(getTitleElement(method).getString());

        // Append method's return type:
        html.appendHtmlConstant(getTypeElement(node.getType().toString()));

        return false;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.AnonymousClassDeclaration) */
    public boolean visit(AnonymousClassDeclaration node) {
        html = new SafeHtmlBuilder();
        html.appendHtmlConstant(getMainImage(JavaClientBundle.INSTANCE.classItem()).toString());
        html.appendHtmlConstant(getModifiersContainer(Modifier.NONE));

        String name = "";
        ASTNode parent = node.getParent();
        if (parent instanceof ClassInstanceCreation) {
            Type type = ((ClassInstanceCreation)parent).getType();
            name = ASTNodes.getTypeName(type);
        }

        Image modifImage = new Image(JavaClientBundle.INSTANCE.classDefaultItem());

        DOM.setStyleAttribute(modifImage.getElement(), "position", "absolute");
        DOM.setStyleAttribute(modifImage.getElement(), "marginLeft", "-10px");
        DOM.setStyleAttribute(modifImage.getElement(), "marginTop", "10px");
        modifImage.getElement().setAttribute("border", "0");
        html.appendHtmlConstant(modifImage.toString());

        html.appendHtmlConstant(getTitleElement("new " + name + "() {...}").getString());
        return true;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.EnumConstantDeclaration) */
    public boolean visit(EnumConstantDeclaration node) {
        html = new SafeHtmlBuilder();

        //Enum constants are static and final by default:
        int modifiers = 24;
        Image image = getMainImage(JavaClientBundle.INSTANCE.publicField());

        html.appendHtmlConstant(image.toString());

        // Add all modifiers container:
        html.appendHtmlConstant(getModifiersContainer(modifiers));
        html.appendHtmlConstant(getTitleElement(node.getName().getFullyQualifiedName()).getString());
        return true;
    }

    /** @return {@link SafeHtmlBuilder} HTML code of the widget */
    public SafeHtmlBuilder getHTML() {
        return html;
    }

    /**
     * Get the HTML element for displaying all modifiers.
     *
     * @param modifiers
     *         modifiers
     * @param isConstructor
     *         <code>true</code> if method is constructor
     * @return {@link String} HTML code of the element
     */
    protected String getModifiersContainer(int modifiers, boolean isConstructor) {
        if (Modifier.isTransient(modifiers) || Modifier.isVolatile(modifiers) || Modifier.isStatic(modifiers)
            || Modifier.isFinal(modifiers) || Modifier.isAbstract(modifiers) || isConstructor) {

            String span =
                    "<span style = \"position: absolute; top: -4px; margin-left: -14px; font-family: Verdana,Bitstream Vera Sans," +
                    "sans-serif; font-size: 9px; text-align: right;' \">";
            span += isConstructor ? "<span class='item-modifier' style='color: #317441; font-weight:bold;'>c</span>" : "";
            span += (Modifier.isTransient(modifiers)) ? "<span class='item-modifier' style='color:#6d0000;'>t</span>" : "";
            span += (Modifier.isVolatile(modifiers)) ? "<span class='item-modifier' style='color:#6d0000'>v</span>" : "";
            span += (Modifier.isStatic(modifiers)) ? "<span class='item-modifier' style='color:#6d0000'>s</span>" : "";
            span += (Modifier.isFinal(modifiers)) ? "<span class='item-modifier' style='color:#174c83'>f</span>" : "";
            span += (Modifier.isAbstract(modifiers)) ? "<span class='item-modifier' style='color:#004e00'>A</span>" : "";
            span += "</span>";
            return span;
        }
        return "";
    }

    /**
     * Get the HTML element for displaying all modifiers.
     *
     * @param modifiers
     * @return {@link String} HTML code of the element
     */
    protected String getModifiersContainer(int modifiers) {
        return getModifiersContainer(modifiers, false);
    }

    /**
     * Returns mail image of the node.
     *
     * @param imageResource
     * @return {@link Image}
     */
    protected Image getMainImage(ImageResource imageResource) {
        Image image = new Image(imageResource);
        DOM.setStyleAttribute(image.getElement(), "cssFloat", "left");
        DOM.setStyleAttribute(image.getElement(), "marginRight", "5px");
        return image;
    }

    /**
     * Returns the HTML element with the title of the node.
     *
     * @param title
     * @return {@link Element}
     */
    protected Element getTitleElement(String title) {
        Element span = DOM.createSpan();
        span.setInnerHTML(title);
        return span;
    }

    /**
     * Returns HTML code of the element with node's type.
     *
     * @param type
     *         type's string representation
     * @return {@link String} HTML code of the element with node's type
     */
    protected String getTypeElement(String type) {
        Element span = DOM.createSpan();
        span.setInnerText(" : " + type);
        span.getStyle().setColor("#644A17");
        return span.getString();
    }

    /**
     * Returns the string presentation of method's parameters.
     *
     * @param method
     * @return {@link String} method's parameters comma separated
     */
    @SuppressWarnings("unchecked")
    protected String getMethodParams(MethodDeclaration method) {
        if (method.parameters().isEmpty()) {
            return "()";
        } else {
            Iterator<SingleVariableDeclaration> paramsIterator = method.parameters().iterator();
            StringBuffer params = new StringBuffer("(");
            while (paramsIterator.hasNext()) {
                SingleVariableDeclaration variable = paramsIterator.next();
                params.append(variable.getType().toString());
                if (paramsIterator.hasNext()) {
                    params.append(", ");
                }
            }
            params.append(")");
            return params.toString();
        }
    }
}
