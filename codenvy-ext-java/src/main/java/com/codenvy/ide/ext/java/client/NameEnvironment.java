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
package com.codenvy.ide.ext.java.client;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.java.client.core.Flags;
import com.codenvy.ide.ext.java.client.core.Signature;
import com.codenvy.ide.ext.java.client.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.client.core.search.IJavaSearchConstants;
import com.codenvy.ide.ext.java.client.env.BinaryTypeImpl;
import com.codenvy.ide.ext.java.client.internal.codeassist.ISearchRequestor;
import com.codenvy.ide.ext.java.client.internal.compiler.env.AccessRestriction;
import com.codenvy.ide.ext.java.client.internal.compiler.env.IBinaryMethod;
import com.codenvy.ide.ext.java.client.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.ext.java.client.internal.compiler.env.NameEnvironmentAnswer;
import com.codenvy.ide.ext.java.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.shared.*;
import com.codenvy.ide.json.JsonStringSet;
import com.codenvy.ide.json.JsonStringSet.IterationCallback;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPStatus;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

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

    private static final String GET_CLASS_URL = "/code-assistant/java/class-description?fqn=";

    private static final String FIND_CLASS_BY_PREFIX = "/code-assistant/java/find-by-prefix/";

    protected Loader loader;

    protected String restServiceContext;

    private String projectId;

    private String wsName;

    private static Set<String> blackSet = new HashSet<String>();

    private static Set<String> packages = new HashSet<String>();

    public static void clearFQNBlackList() {
        blackSet.clear();
    }

    /**
     *
     */
    public NameEnvironment(String projectId, String restContenxt) {
        this.projectId = projectId;
        //this.autoBeanFactory = autoBeanFactory;
        restServiceContext = restContenxt;
        wsName = '/' + Utils.getWorkspaceName();
    }

    /**
     * Get Class description (methods, fields etc.) by class FQN
     *
     * @param fqn
     * @param fileHref
     *         for who autocompletion called (Need for find classpath)
     * @param callback
     *         - the callback which client has to implement
     */
    public void getClassDescription(String fqn, String projectId, AsyncRequestCallback<TypeInfo> callback) {
        String url =
                restServiceContext + wsName + GET_CLASS_URL + fqn + "&projectid=" + projectId + "&vfsid="
                + "dev-monit";
        int status[] = {HTTPStatus.NO_CONTENT, HTTPStatus.OK};
        callback.setSuccessCodes(status);
        try {
            AsyncRequest.build(RequestBuilder.GET, url).send(callback);
        } catch (RequestException e) {
            Log.error(NameEnvironment.class, e);
        }
    }

    /**
     * Find classes by prefix
     *
     * @param prefix
     *         the first letters of class name
     * @param projectId
     *         for who autocompletion called (Need for find classpath)
     * @param callback
     *         - the callback which client has to implement
     */
    public void findClassesByPrefix(String prefix, String projectId, AsyncRequestCallback<TypesList> callback) {
        String url =
                restServiceContext + wsName + FIND_CLASS_BY_PREFIX + prefix + "?where=className" + "&projectid=" + projectId
                //TODO configure vfs id
                + "&vfsid=" + "dev-monit";
        try {
            AsyncRequest.build(RequestBuilder.GET, url).send(callback);
        } catch (RequestException e) {
            Log.error(getClass(), e);
        }
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
    public void loadTypeInfo(final String fqn, String projectId) {
        final JSONTypeInfoUnmarshaller jsonTypeInfoUnmarshaller = new JSONTypeInfoUnmarshaller();
        getClassDescription(fqn, projectId, new AsyncRequestCallback<TypeInfo>(jsonTypeInfoUnmarshaller)

        {

            @Override
            protected void onSuccess(TypeInfo result) {
                if (jsonTypeInfoUnmarshaller.typeInfo != null) {
                    TypeInfoStorage.get().putType(fqn, jsonTypeInfoUnmarshaller.typeInfo.toString());
                } else {
                    blackSet.add(fqn);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                //IDE.fireEvent(new ExceptionThrownEvent(exception));
                //TODO
                Log.error(getClass(), exception);
            }
        });
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#findType(char[], char[][]) */
    @Override
    public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
        if ("package-info".equals(new String(typeName))) {
            return null;
        }

        StringBuilder b = new StringBuilder();
        for (char[] c : packageName) {
            b.append(c).append('.');
        }
        b.append(typeName);
        final String key = validateFqn(b);
        //TODO
//      if (TypeInfoStorage.get().getPackages(projectId).contains(key))
//         return null;
        if (TypeInfoStorage.get().containsKey(key)) {
            return new NameEnvironmentAnswer(new BinaryTypeImpl(JSONParser
                                                                        .parseLenient(TypeInfoStorage.get().getType(key)).isObject()),
                                             null);
        }
        if (projectId != null) {
            if (!blackSet.contains(key)) {
                loadTypeInfo(key, projectId);
            }
        }
        return null;
    }

    /** @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#isPackage(char[][], char[]) */
    @Override
    public boolean isPackage(char[][] parentPackageName, char[] packageName) {
//      StringBuilder p = new StringBuilder();
//      if (parentPackageName != null)
//      {
//         for (char[] seg : parentPackageName)
//         {
//            p.append(seg).append('.');
//         }
//      }
//      p.append(packageName);
//      JsonStringSet packages = TypeInfoStorage.get().getPackages(projectId);
//      //TODO maybe need more actions on this
//      if (packages == null)
//         return false;
        return true;
//      return packages.contains(p.toString());
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
    public void findConstructorDeclarations(char[] prefix, boolean camelCaseMatch, final ISearchRequestor requestor) {
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
                restServiceContext + wsName + "/code-assistant/java/classes-by-prefix" + "?prefix=" + new String(simpleName)
                + "&projectid=" + projectId + "&vfsid=" + "dev-monit";
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
                    if (TypeInfoStorage.get().containsKey(String.valueOf(type.getFqn()))) {
                        continue;
                    }
                    TypeInfoStorage.get().putType(new String(type.getFqn()), type.toJsonString());
                    addConstructor(type, requestor);
                }
            }

        } catch (Exception e) {
            Log.error(getClass(), e);
        }
    }

    private void addConstructor(BinaryTypeImpl type, final ISearchRequestor requestor) {
        IBinaryMethod[] methods = type.getMethods();
        boolean hasConstructor = false;
        if (methods != null) {
            for (IBinaryMethod method : methods) {
                if (!method.isConstructor()) {
                    continue;
                }
                int parameterCount = Signature.getParameterCount(method.getMethodDescriptor());
                char[][] parameterTypes = Signature.getParameterTypes(method.getMethodDescriptor());
                requestor.acceptConstructor(method.getModifiers(), type.getSourceName(), parameterCount,
                                            method.getMethodDescriptor(), parameterTypes, method.getArgumentNames(), type.getModifiers(),
                                            Signature.getQualifier(type.getFqn()), 0, new String(type.getSourceName()), null);
                hasConstructor = true;
            }
        }
        if (!hasConstructor) {
            requestor.acceptConstructor(Flags.AccPublic, type.getSourceName(), -1,
                                        null, // signature is not used for source type
                                        CharOperation.NO_CHAR_CHAR, CharOperation.NO_CHAR_CHAR, type.getModifiers(),
                                        Signature.getQualifier(type.getFqn()), 0, new String(type.getSourceName()), null);
        }

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
                if (key.startsWith(pack)) {
                    requestor.acceptPackage(key.toCharArray());
                }
            }
        });
    }

    private String runSyncReques(String url) {
        XmlHttpWraper xmlhttp = nativeRunSyncReques(url);
        int status = xmlhttp.getStatusCode();
        if (status == 200) {
            return xmlhttp.getResponseText();
        } else {
            String message = null;
            if (status == 204) {
                message = "no content";
            } else {
                message = xmlhttp.getResponseText();
            }
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
                          final ISearchRequestor requestor) {
        if (qualifiedName.length == 0) {
            return;
        }
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
                    restServiceContext + wsName + "/code-assistant/java/find-by-prefix/" + new String(qualifiedName) + "?where="
                    + typeSearch + "&projectid=" + projectId + "&vfsid=" + "dev-monit";
        } else {
            url =
                    restServiceContext + wsName + "/code-assistant/java/find-by-type/" + searchType + "?prefix="
                    + new String(qualifiedName) + "&projectid=" + projectId + "&vfsid="
                    + "dev-monit";
        }
        try {

            String typesJson = runSyncReques(url);
            DtoClientImpls.TypesListImpl autoBean = DtoClientImpls.TypesListImpl.deserialize(typesJson);

            for (ShortTypeInfo info : autoBean.getTypes().asIterable()) {
                requestor.acceptType(info.getName().substring(0, info.getName().lastIndexOf(".")).toCharArray(),
                                     info.getName().substring(info.getName().lastIndexOf(".") + 1).toCharArray(),
                                     null,
                                     info.getModifiers(),
                                     null);
            }
            TypeInfoStorage.get().setShortTypesInfo(typesJson);
        } catch (Throwable e) {
            Log.error(getClass(), e);
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
                if (CharOperation.equals(missingSimpleName, typeName)) {
                    storage.acceptType(packageName, typeName, enclosingTypeNames, modifiers, accessRestriction);
                }
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
        });
    }

    public static class JSONTypesInfoUnmarshaller implements Unmarshallable<TypesInfoList> {

        public JSONArray typesInfo;

        /** @see com.codenvy.gwtframework.commons.rest.copy.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
        @Override
        public void unmarshal(Response response) throws UnmarshallerException {
            if (response.getStatusCode() != 204) {
                typesInfo = JSONParser.parseLenient(response.getText()).isArray();
            }
        }

        /** @see com.codenvy.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
        @Override
        public TypesInfoList getPayload() {
            return null;
        }

    }

    public static class JSONTypeInfoUnmarshaller implements Unmarshallable<TypeInfo> {

        private JSONObject typeInfo;

        /** @see com.codenvy.gwtframework.commons.rest.copy.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
        @Override
        public void unmarshal(Response response) throws UnmarshallerException {
            if (response.getStatusCode() != 204) {
                typeInfo = JSONParser.parseLenient(response.getText()).isObject();
            }
        }

        /** @see com.codenvy.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
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
