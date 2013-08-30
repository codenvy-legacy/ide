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

import com.codenvy.ide.json.shared.JsonStringSet;
import com.codenvy.ide.json.shared.JsonStringSet.IterationCallback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import org.eclipse.jdt.client.core.Flags;
import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.eclipse.jdt.client.core.search.IJavaSearchConstants;
import org.eclipse.jdt.client.env.BinaryTypeImpl;
import org.eclipse.jdt.client.internal.codeassist.ISearchRequestor;
import org.eclipse.jdt.client.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.client.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.client.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.client.runtime.IProgressMonitor;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.codeassistant.jvm.shared.*;
import org.exoplatform.ide.editor.java.client.JavaEditorExtension;
import org.exoplatform.ide.editor.java.client.codeassistant.services.JavaCodeAssistantService;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link INameEnvironment} interface, use JavaCodeAssistantService for receiving data and SessionStorage for
 * cache Java type data in browser
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 13, 2012 3:10:43 PM evgen $
 */
public class NameEnvironment implements INameEnvironment {

    private String projectId;

    private static Set<String> blackSet = new HashSet<String>();

    private static Set<String> packages = new HashSet<String>();


    public static void clearFQNBlackList() {
        blackSet.clear();
    }

    /**
     *
     */
    public NameEnvironment(String projectId) {
        this.projectId = projectId;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#findType(char[][]) */
    @Override
    public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
        StringBuilder b = new StringBuilder();
        for (char[] c : compoundTypeName) {
            b.append(c).append('.');
        }
        b.deleteCharAt(b.length() - 1);

        final String key = validateFqn(b);
        if (TypeInfoStorage.get().containsKey(key)) {
            return new NameEnvironmentAnswer(new BinaryTypeImpl(JSONParser
                                                                        .parseLenient(TypeInfoStorage.get().getType(key)).isObject()),
                                             null);
        }

        if (projectId != null) {

            loadTypeInfo(key, projectId);
        }
        return null;
    }

    private String validateFqn(StringBuilder builder) {
        if (builder.indexOf("<") != -1) {
            builder.setLength(builder.indexOf("<"));
        }
        return builder.toString();
    }

    /**
     * Load and store in TypeInfoStorage type info
     *
     * @param fqn
     *         of the type
     * @param projectId
     *         project
     */
    public static void loadTypeInfo(final String fqn, String projectId) {
        final JSONTypeInfoUnmarshaller jsonTypeInfoUnmarshaller = new JSONTypeInfoUnmarshaller();
        JavaCodeAssistantService.get().getClassDescription(fqn, projectId,
                                                           new AsyncRequestCallback<TypeInfo>(jsonTypeInfoUnmarshaller)

                                                           {

                                                               @Override
                                                               protected void onSuccess(TypeInfo result) {
                                                                   if (jsonTypeInfoUnmarshaller.typeInfo != null) {
                                                                       TypeInfoStorage.get().putType(fqn, jsonTypeInfoUnmarshaller.typeInfo















































































































                                                                                                                                  .toString());
                                                                   } else {
                                                                       blackSet.add(fqn);
                                                                   }
                                                               }

                                                               @Override
                                                               protected void onFailure(Throwable exception) {
                                                                   IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                               }
                                                           });
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#findType(char[], char[][]) */
    @Override
    public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
        if ("package-info".equals(new String(typeName)))
            return null;

        StringBuilder b = new StringBuilder();
        for (char[] c : packageName) {
            b.append(c).append('.');
        }
        b.append(typeName);
        final String key = validateFqn(b);
        if (TypeInfoStorage.get().getPackages(projectId).contains(key))
            return null;
        if (TypeInfoStorage.get().containsKey(key)) {
            return new NameEnvironmentAnswer(new BinaryTypeImpl(JSONParser
                                                                        .parseLenient(TypeInfoStorage.get().getType(key)).isObject()),
                                             null);
        }
        if (projectId != null) {
            if (!blackSet.contains(key))
                loadTypeInfo(key, projectId);
        }
        return null;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#isPackage(char[][], char[]) */
    @Override
    public boolean isPackage(char[][] parentPackageName, char[] packageName) {
        StringBuilder p = new StringBuilder();
        if (parentPackageName != null) {
            for (char[] seg : parentPackageName) {
                p.append(seg).append('.');
            }
        }
        p.append(packageName);
        JsonStringSet packages = TypeInfoStorage.get().getPackages(projectId);
        //TODO maybe need more actions on this
        if (packages == null)
            return false;

        return packages.contains(p.toString());
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#cleanup() */
    @Override
    public void cleanup() {
    }

    /**
     * Must be used only by CompletionEngine. The progress monitor is used to be able to cancel completion operations
     * <p/>
     * Find constructor declarations that are defined in the current environment and whose name starts with the given prefix. The
     * prefix is a qualified name separated by periods or a simple name (ex. java.util.V or V).
     * <p/>
     * The constructors found are passed to one of the following methods: ISearchRequestor.acceptConstructor(...)
     */
    @Override
    public void findConstructorDeclarations(char[] prefix, boolean camelCaseMatch, final ISearchRequestor requestor,
                                            IProgressMonitor monitor) {
        int lastDotIndex = CharOperation.lastIndexOf('.', prefix);
        char[] qualification, simpleName;
        if (lastDotIndex < 0) {
            qualification = null;
            if (camelCaseMatch) {
                simpleName = prefix;
            } else {
                simpleName = CharOperation.toLowerCase(prefix);
            }
        } else {
            qualification = CharOperation.subarray(prefix, 0, lastDotIndex);
            if (camelCaseMatch) {
                simpleName = CharOperation.subarray(prefix, lastDotIndex + 1, prefix.length);
            } else {
                simpleName = CharOperation.toLowerCase(CharOperation.subarray(prefix, lastDotIndex + 1, prefix.length));
            }
        }
        String url =
                Utils.getRestContext() + Utils.getWorkspaceName() + "/code-assistant/java/classes-by-prefix" + "?prefix=" + new String(simpleName)
                + "&projectid=" + projectId + "&vfsid=" + VirtualFileSystem.getInstance().getInfo().getId();
        try {
            List<JSONObject> typesByNamePrefix =
                    TypeInfoStorage.get().getTypesByNamePrefix(new String(prefix), qualification != null);
            for (JSONObject object : typesByNamePrefix) {
                BinaryTypeImpl type = new BinaryTypeImpl(object);
                addConstructor(type, requestor);
            }
            String typesJson = runSyncReques(url);
            JSONArray typesFromServer = null;
            if (typesJson != null) {
                typesFromServer = JSONParser.parseLenient(typesJson).isArray();
                for (int i = 0; i < typesFromServer.size(); i++) {
                    JSONObject object = typesFromServer.get(i).isObject();
                    BinaryTypeImpl type = new BinaryTypeImpl(object);
                    if (TypeInfoStorage.get().containsKey(String.valueOf(type.getFqn())))
                        continue;
                    TypeInfoStorage.get().putType(new String(type.getFqn()), type.toJsonString());
                    addConstructor(type, requestor);
                }
            }

        } catch (Exception e) {
            IDE.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
        }
    }

    private void addConstructor(BinaryTypeImpl type, final ISearchRequestor requestor) {
        IBinaryMethod[] methods = type.getMethods();
        //      if((type.getModifiers() & IModifierConstants.ACC_INTERFACE) != 0 )
        //      {
        //         requestor.acceptConstructor(0, type.getSourceName(), 0, "()V;".toCharArray(), null, null, type.getModifiers(), Signature.getQualifier(type.getFqn()), ExtraFlags.HasNonPrivateStaticMemberTypes, new String(type.getSourceName()), null);
        //      }
        boolean hasConstructor = false;
        if (methods != null) {
            for (IBinaryMethod method : methods) {
                if (!method.isConstructor())
                    continue;
                int parameterCount = Signature.getParameterCount(method.getMethodDescriptor());
                char[][] parameterTypes = Signature.getParameterTypes(method.getMethodDescriptor());
                requestor.acceptConstructor(method.getModifiers(), type.getSourceName(), parameterCount,
                                            method.getMethodDescriptor(), parameterTypes, method.getArgumentNames(), type.getModifiers(),
                                            Signature.getQualifier(type.getFqn()), 0, new String(type.getSourceName()), null);
                hasConstructor = true;
            }
        }
        if (!hasConstructor)
            requestor.acceptConstructor(Flags.AccPublic, type.getSourceName(), -1,
                                        null, // signature is not used for source type
                                        CharOperation.NO_CHAR_CHAR, CharOperation.NO_CHAR_CHAR, type.getModifiers(),
                                        Signature.getQualifier(type.getFqn()), 0, new String(type.getSourceName()), null);

    }

    /**
     * Find the packages that start with the given prefix. A valid prefix is a qualified name separated by periods (ex. java.util).
     * The packages found are passed to: ISearchRequestor.acceptPackage(char[][] packageName)
     */
    @Override
    public void findPackages(char[] qualifiedName, final ISearchRequestor requestor) {
        JsonStringSet packages = TypeInfoStorage.get().getPackages(projectId);
        final String pack = new String(qualifiedName);
        packages.iterate(new IterationCallback() {

            @Override
            public void onIteration(String key) {
                if (key.startsWith(pack))
                    requestor.acceptPackage(key.toCharArray());
            }
        });
    }

    private String runSyncReques(String url) {
        XmlHttpWraper xmlhttp = nativeRunSyncReques(url);
        int status = xmlhttp.getStatusCode();
        if (status == 200)
            return xmlhttp.getResponseText();
        else {
            String message = null;
            if (status == 204) {
                message = "no content";
            } else
                message = xmlhttp.getResponseText();
            throw new RuntimeException("Server return " + message);
        }
    }

    private native XmlHttpWraper nativeRunSyncReques(String url)/*-{
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.open("GET", url, false);
        xmlhttp.send();
        return xmlhttp;
    }-*/;

    /**
     * Must be used only by CompletionEngine. The progress monitor is used to be able to cancel completion operations
     * <p/>
     * Find the top-level types that are defined in the current environment and whose name starts with the given prefix. The prefix
     * is a qualified name separated by periods or a simple name (ex. java.util.V or V).
     * <p/>
     * The types found are passed to one of the following methods (if additional information is known about the types):
     * ISearchRequestor.acceptType(char[][] packageName, char[] typeName) ISearchRequestor.acceptClass(char[][] packageName, char[]
     * typeName, int modifiers) ISearchRequestor.acceptInterface(char[][] packageName, char[] typeName, int modifiers)
     * <p/>
     * This method can not be used to find member types... member types are found relative to their enclosing type.
     */
    @Override
    public void findTypes(char[] qualifiedName, boolean b, boolean camelCaseMatch, int searchFor,
                          final ISearchRequestor requestor, IProgressMonitor monitor) {
        if (qualifiedName.length == 0)
            return;
        AutoBean<TypesList> autoBean = JavaEditorExtension.AUTO_BEAN_FACTORY.types();
        String searchType = convertSearchFilterToModelFilter(searchFor);
        String url = null;
        if (searchType == null) {
            int lastDotIndex = CharOperation.lastIndexOf('.', qualifiedName);
            String typeSearch;
            if (lastDotIndex < 0) {
                typeSearch = "className";
            } else {
                typeSearch = "fqn";
            }
            url =
                    Utils.getRestContext() + Utils.getWorkspaceName() + "/code-assistant/java/find-by-prefix/" + new String(qualifiedName)
                    + "?where=" + typeSearch + "&projectid=" + projectId + "&vfsid="
                    + VirtualFileSystem.getInstance().getInfo().getId();
        } else
            url =
                    Utils.getRestContext() + Utils.getWorkspaceName() + "/code-assistant/java/find-by-type/" + searchType + "?prefix="
                    + new String(qualifiedName) + "&projectid=" + projectId + "&vfsid="
                    + VirtualFileSystem.getInstance().getInfo().getId();
        try {

            String typesJson = runSyncReques(url);
            Splittable data = StringQuoter.split(typesJson);
            AutoBeanCodex.decodeInto(data, autoBean);

            for (ShortTypeInfo info : autoBean.as().getTypes()) {

                requestor
                        .acceptType(info.getName().substring(0, info.getName().lastIndexOf(".")).toCharArray(),
                                    info.getName().substring(info.getName().lastIndexOf(".") + 1).toCharArray(), null,
                                    info.getModifiers(), null);
            }
            TypeInfoStorage.get().setShortTypesInfo(typesJson);
        } catch (Throwable e) {
            IDE.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
        }
    }

    private static String convertSearchFilterToModelFilter(int searchFilter) {
        switch (searchFilter) {
            case IJavaSearchConstants.CLASS:
                return JavaType.CLASS.name();
            case IJavaSearchConstants.INTERFACE:
                return JavaType.INTERFACE.name();
            case IJavaSearchConstants.ENUM:
                return JavaType.ENUM.name();
            case IJavaSearchConstants.ANNOTATION_TYPE:
                return JavaType.ANNOTATION.name();
            // TODO
            // case IJavaSearchConstants.CLASS_AND_ENUM:
            // return NameLookup.ACCEPT_CLASSES | NameLookup.ACCEPT_ENUMS;
            // case IJavaSearchConstants.CLASS_AND_INTERFACE:
            // return NameLookup.ACCEPT_CLASSES | NameLookup.ACCEPT_INTERFACES;
            default:
                return null;
        }
    }

    /**
     * Find the top-level types that are defined
     * in the current environment and whose simple name matches the given name.
     * <p/>
     * The types found are passed to one of the following methods (if additional
     * information is known about the types):
     * ISearchRequestor.acceptType(char[][] packageName, char[] typeName)
     * ISearchRequestor.acceptClass(char[][] packageName, char[] typeName, int modifiers)
     * ISearchRequestor.acceptInterface(char[][] packageName, char[] typeName, int modifiers)
     * <p/>
     * This method can not be used to find member types... member
     * types are found relative to their enclosing type.
     */
    @Override
    public void findExactTypes(final char[] missingSimpleName, boolean b, int type, final ISearchRequestor storage) {
        findTypes(missingSimpleName, b, false, type, new ISearchRequestor() {

            @Override
            public void acceptType(char[] packageName, char[] typeName, char[][] enclosingTypeNames, int modifiers,
                                   AccessRestriction accessRestriction) {
                if (CharOperation.equals(missingSimpleName, typeName))
                    storage.acceptType(packageName, typeName, enclosingTypeNames, modifiers, accessRestriction);
            }

            @Override
            public void acceptPackage(char[] packageName) {
            }

            @Override
            public void acceptConstructor(int modifiers, char[] simpleTypeName, int parameterCount, char[] signature,
                                          char[][] parameterTypes, char[][] parameterNames, int typeModifiers, char[] packageName,
                                          int extraFlags,
                                          String path, AccessRestriction access) {
            }
        }, null);
    }

    public static class JSONTypesInfoUnmarshaller implements Unmarshallable<TypesInfoList> {

        public JSONArray typesInfo;

        @Override
        public void unmarshal(Response response) throws UnmarshallerException {
            if (response.getStatusCode() != 204) {
                typesInfo = JSONParser.parseLenient(response.getText()).isArray();
            }
        }

        @Override
        public TypesInfoList getPayload() {
            return null;
        }

    }

    public static class JSONTypeInfoUnmarshaller implements Unmarshallable<TypeInfo> {

        private JSONObject typeInfo;

        @Override
        public void unmarshal(Response response) throws UnmarshallerException {
            if (response.getStatusCode() != 204) {
                typeInfo = JSONParser.parseLenient(response.getText()).isObject();
            }
        }

        @Override
        public TypeInfo getPayload() {
            return null;
        }
    }

    private static final class XmlHttpWraper extends JavaScriptObject {
        /**
         *
         */
        protected XmlHttpWraper() {
        }

        public native int getStatusCode()/*-{
            return this.status;
        }-*/;

        public native String getResponseText()/*-{
            return this.responseText;
        }-*/;

    }
}
