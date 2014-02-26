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
package com.codenvy.ide.ext.java.jdi.client.debug;

import elemental.html.DragEvent;
import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.debug.Breakpoint;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The implementation of {@link DebuggerView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class DebuggerViewImpl extends BaseView<DebuggerView.ActionDelegate> implements DebuggerView {
    private static DebuggerViewImplUiBinder ourUiBinder = GWT.create(DebuggerViewImplUiBinder.class);
    @UiField
    PushButton                      btnResume;
    @UiField
    PushButton                      btnStepInto;
    @UiField
    PushButton                      btnStepOver;
    @UiField
    PushButton                      btnStepReturn;
    @UiField
    PushButton                      btnDisconnect;
    @UiField
    PushButton                      btnRemoveAllBreakpoints;
    @UiField
    PushButton                      btnChangeValue;
    @UiField
    PushButton                      btnEvaluateExpression;
    @UiField
    Label                           vmName;
    @UiField
    ScrollPanel                     variablesPanel;
    @UiField
    ScrollPanel                     breakpointsPanel;
    @UiField(provided = true)
    JavaRuntimeLocalizationConstant locale;
    @UiField(provided = true)
    JavaRuntimeResources            res;
    @UiField(provided = true)
    Resources                       coreRes;
    private final DtoFactory                dtoFactory;
    private       SimpleList<Breakpoint>    breakpoints;
    private       Tree<Variable>            variables;
    private       TreeNodeElement<Variable> selectedVariable;

    /**
     * Create view.
     *
     * @param partStackUIResources
     * @param resources
     * @param locale
     * @param coreRes
     * @param rendererResources
     */
    @Inject
    protected DebuggerViewImpl(PartStackUIResources partStackUIResources, JavaRuntimeResources resources,
                               JavaRuntimeLocalizationConstant locale, Resources coreRes,
                               VariableTreeNodeRenderer.Resources rendererResources, DtoFactory dtoFactory) {
        super(partStackUIResources);

        this.locale = locale;
        this.res = resources;
        this.coreRes = coreRes;
        this.dtoFactory = dtoFactory;

        container.add(ourUiBinder.createAndBindUi(this));

        TableElement breakPointsElement = Elements.createTableElement();
        breakPointsElement.setAttribute("style", "width: 100%");
        SimpleList.ListEventDelegate<Breakpoint> breakpointListEventDelegate = new SimpleList.ListEventDelegate<Breakpoint>() {
            public void onListItemClicked(Element itemElement, Breakpoint itemData) {
                breakpoints.getSelectionModel().setSelectedItem(itemData);
            }

            public void onListItemDoubleClicked(Element listItemBase, Breakpoint itemData) {
                // TODO: implement 'go to breakpoint source' feature
            }
        };

        SimpleList.ListItemRenderer<Breakpoint> breakpointListItemRenderer = new
                SimpleList.ListItemRenderer<Breakpoint>() {
                    @Override
                    public void render(Element itemElement, Breakpoint itemData) {
                        TableCellElement label = Elements.createTDElement();

                        SafeHtmlBuilder sb = new SafeHtmlBuilder();
                        // Add icon
                        sb.appendHtmlConstant("<table><tr><td>");
                        ImageResource icon = res.breakpoint();
                        if (icon != null) {
                            sb.appendHtmlConstant("<img src=\"" + icon.getSafeUri().asString() + "\">");
                        }
                        sb.appendHtmlConstant("</td>");

                        // Add title
                        sb.appendHtmlConstant("<td>");
                        sb.appendEscaped(itemData.getPath() + " - [line: " + String.valueOf(itemData.getLineNumber() + 1) + "]");
                        sb.appendHtmlConstant("</td></tr></table>");

                        label.setInnerHTML(sb.toSafeHtml().asString());

                        itemElement.appendChild(label);
                    }

                    @Override
                    public Element createElement() {
                        return Elements.createTRElement();
                    }
                };

        breakpoints = SimpleList.create((SimpleList.View)breakPointsElement, coreRes.defaultSimpleListCss(), breakpointListItemRenderer,
                                        breakpointListEventDelegate);
        this.breakpointsPanel.add(breakpoints);
        this.variables = Tree.create(rendererResources, new VariableNodeDataAdapter(), new VariableTreeNodeRenderer(rendererResources));
        this.variables.setTreeEventHandler(new Tree.Listener<Variable>() {
            @Override
            public void onNodeAction(TreeNodeElement<Variable> node) {
            }

            @Override
            public void onNodeClosed(TreeNodeElement<Variable> node) {
                //do nothing
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<Variable> node) {
                //do nothing
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<Variable> node, DragEvent event) {
                //do nothing
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Variable> node, DragEvent event) {
                //do nothing
            }

            @Override
            public void onNodeExpanded(final TreeNodeElement<Variable> node) {
                selectedVariable = node;
                delegate.onSelectedVariableElement(selectedVariable.getData());
                delegate.onExpandVariablesTree();
            }

            @Override
            public void onNodeSelected(TreeNodeElement<Variable> node, SignalEvent event) {
                selectedVariable = node;
                delegate.onSelectedVariableElement(selectedVariable.getData());
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
                //do nothing
            }

            @Override
            public void onRootDragDrop(DragEvent event) {
                //do nothing
            }
        });
        Widget widget = variables.asWidget();
        widget.setHeight("100%");
        this.variablesPanel.add(widget);
    }

    /** {@inheritDoc} */
    @Override
    public void setVariables(@NotNull List<Variable> variables) {
        Variable root = this.variables.getModel().getRoot();
        if (root == null) {
            root = dtoFactory.createDto(Variable.class);
            this.variables.getModel().setRoot(root);
        }
        root.setVariables(variables);
        this.variables.renderTree(0);
    }

    /** {@inheritDoc} */
    @Override
    public void setBreakpoints(@NotNull Array<Breakpoint> breakpoints) {
        this.breakpoints.render(breakpoints);
    }

    /** {@inheritDoc} */
    @Override
    public void setVMName(@NotNull String name) {
        vmName.setText(name);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableResumeButton(boolean isEnable) {
        btnResume.setEnabled(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableRemoveAllBreakpointsButton(boolean isEnable) {
        btnRemoveAllBreakpoints.setEnabled(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableDisconnectButton(boolean isEnable) {
        btnDisconnect.setEnabled(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableStepIntoButton(boolean isEnable) {
        btnStepInto.setEnabled(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableStepOverButton(boolean isEnable) {
        btnStepOver.setEnabled(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableStepReturnButton(boolean isEnable) {
        btnStepReturn.setEnabled(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableChangeValueButtonEnable(boolean isEnable) {
        btnChangeValue.setEnabled(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableEvaluateExpressionButtonEnable(boolean isEnable) {
        btnEvaluateExpression.setEnabled(isEnable);
    }

    /** {@inheritDoc} */
    @Override
    public void updateSelectedVariable() {
        variables.closeNode(selectedVariable);
        variables.expandNode(selectedVariable);
    }

    /** {@inheritDoc} */
    @Override
    public void setVariablesIntoSelectedVariable(@NotNull List<Variable> variables) {
        Variable rootVariable = selectedVariable.getData();
        rootVariable.setVariables(variables);
    }

    @UiHandler("btnResume")
    public void onResumeButtonClicked(ClickEvent event) {
        delegate.onResumeButtonClicked();
    }

    @UiHandler("btnStepInto")
    public void onStepIntoButtonClicked(ClickEvent event) {
        delegate.onStepIntoButtonClicked();
    }

    @UiHandler("btnStepOver")
    public void onStepOverButtonClicked(ClickEvent event) {
        delegate.onStepOverButtonClicked();
    }

    @UiHandler("btnStepReturn")
    public void onStepReturnButtonClicked(ClickEvent event) {
        delegate.onStepReturnButtonClicked();
    }

    @UiHandler("btnDisconnect")
    public void onDisconnectButtonClicked(ClickEvent event) {
        delegate.onDisconnectButtonClicked();
    }

    @UiHandler("btnRemoveAllBreakpoints")
    public void onRemoveAllBreakpointsButtonClicked(ClickEvent event) {
        delegate.onRemoveAllBreakpointsButtonClicked();
    }

    @UiHandler("btnChangeValue")
    public void onChangeValueButtonClicked(ClickEvent event) {
        delegate.onChangeValueButtonClicked();
    }

    @UiHandler("btnEvaluateExpression")
    public void onEvaluateExpressionButtonClicked(ClickEvent event) {
        delegate.onEvaluateExpressionButtonClicked();
    }

    interface DebuggerViewImplUiBinder extends UiBinder<Widget, DebuggerViewImpl> {
    }
}