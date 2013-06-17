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
package com.codenvy.ide.ext.java.jdi.client.debug;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.debug.Breakpoint;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeLocalizationConstant;
import com.codenvy.ide.ext.java.jdi.client.JavaRuntimeResources;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.part.PartStackUIResources;
import com.codenvy.ide.part.base.BaseView;
import com.codenvy.ide.ui.Button;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link DebuggerView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class DebuggerViewImpl extends BaseView<DebuggerView.ActionDelegate> implements DebuggerView {
    interface DebuggerViewImplUiBinder extends UiBinder<Widget, DebuggerViewImpl> {
    }

    private static DebuggerViewImplUiBinder ourUiBinder = GWT.create(DebuggerViewImplUiBinder.class);

    private SimpleList<Breakpoint> breakPoints;
    private SimpleList<Variable>   variables;
    @UiField
    Button                          btnResume;
    @UiField
    Button                          btnStepInto;
    @UiField
    Button                          btnStepOver;
    @UiField
    Button                          btnStepReturn;
    @UiField
    Button                          btnDisconnect;
    @UiField
    Button                          btnRemoveAllBreakpoints;
    @UiField
    Button                          btnChangeValue;
    @UiField
    Button                          btnEvaluateExpression;
    @UiField
    Label                           vmName;
    @UiField
    ScrollPanel                     variablesPanel;
    @UiField
    ScrollPanel                     breakPointsPanel;
    @UiField(provided = true)
    JavaRuntimeLocalizationConstant locale;
    @UiField(provided = true)
    JavaRuntimeResources            res;
    @UiField(provided = true)
    Resources                       coreRes;

    /**
     * Create view.
     *
     * @param partStackUIResources
     * @param resources
     * @param locale
     * @param coreRes
     */
    @Inject
    protected DebuggerViewImpl(PartStackUIResources partStackUIResources, JavaRuntimeResources resources,
                               JavaRuntimeLocalizationConstant locale, Resources coreRes) {
        super(partStackUIResources);

        this.locale = locale;
        this.res = resources;
        this.coreRes = coreRes;

        container.add(ourUiBinder.createAndBindUi(this));

        TableElement breakPointsElement = Elements.createTableElement();
        breakPointsElement.setAttribute("style", "width: 100%");
        SimpleList.ListEventDelegate<Breakpoint> listBreakPointsDelegate = new SimpleList.ListEventDelegate<Breakpoint>() {
            public void onListItemClicked(Element itemElement, Breakpoint itemData) {
                breakPoints.getSelectionModel().setSelectedItem(itemData);
            }

            public void onListItemDoubleClicked(Element listItemBase, Breakpoint itemData) {
            }
        };
        SimpleList.ListItemRenderer<Breakpoint> listBreakPointsRenderer = new SimpleList.ListItemRenderer<Breakpoint>() {
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
        breakPoints = SimpleList.create((SimpleList.View)breakPointsElement, coreRes.defaultSimpleListCss(),
                                        listBreakPointsRenderer,
                                        listBreakPointsDelegate);
        this.breakPointsPanel.add(breakPoints);

        TableElement variablesElement = Elements.createTableElement();
        variablesElement.setAttribute("style", "width: 100%");
        SimpleList.ListEventDelegate<Variable> variablesDelegate = new SimpleList.ListEventDelegate<Variable>() {
            public void onListItemClicked(Element itemElement, Variable itemData) {
                variables.getSelectionModel().setSelectedItem(itemData);
                delegate.onSelectedVariable(itemData);
            }

            public void onListItemDoubleClicked(Element listItemBase, Variable itemData) {
            }
        };
        SimpleList.ListItemRenderer<Variable> variablesItemRenderer = new SimpleList.ListItemRenderer<Variable>() {
            @Override
            public void render(Element itemElement, Variable itemData) {
                TableCellElement label = Elements.createTDElement();

                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                // Add icon
                sb.appendHtmlConstant("<table><tr><td>");
                ImageResource icon = res.local();
                if (icon != null) {
                    sb.appendHtmlConstant("<img src=\"" + icon.getSafeUri().asString() + "\">");
                }
                sb.appendHtmlConstant("</td>");

                // Add title
                sb.appendHtmlConstant("<td>");
                sb.appendEscaped(itemData.getName() + ": " + itemData.getValue());
                sb.appendHtmlConstant("</td></tr></table>");

                label.setInnerHTML(sb.toSafeHtml().asString());

                itemElement.appendChild(label);
            }

            @Override
            public Element createElement() {
                return Elements.createTRElement();
            }
        };
        variables = SimpleList.create((SimpleList.View)variablesElement, coreRes.defaultSimpleListCss(), variablesItemRenderer,
                                      variablesDelegate);
        this.variablesPanel.add(variables);
    }

    /** {@inheritDoc} */
    @Override
    public void setVariables(@NotNull JsonArray<Variable> variables) {
        // TODO change to tree
        this.variables.render(variables);
    }

    /** {@inheritDoc} */
    @Override
    public void setBreakPoints(@NotNull JsonArray<Breakpoint> breakPoints) {
        this.breakPoints.render(breakPoints);
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
}