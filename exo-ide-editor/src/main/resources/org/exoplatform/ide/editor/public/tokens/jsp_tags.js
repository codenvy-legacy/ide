[
		{
			"name" : "jsp:useBean",
			"type" : "TAG",
			"code" : "<jsp:useBean id=\"\"></jsp:useBean>",
			"fullDescription" : "A jsp:useBean action associates an instance of a Java programming language object defined within a given scope and available with a given id with a newly declared scripting variable of the same id. When a &lt;jsp:useBean> action is used in an scriptless page, or in an scriptless context (as in the body of an action so indicated), there are no Java scripting variables created but instead an EL variable is created.",
			"subTokenList" : [
					{
						"name" : "id",
						"type" : "ATTRIBUTE",
						"code" : "id=\"\"",
						"fullDescription" : "The name used to identify the object instance in the speciﬁed scope’s namespace, and also the scripting variable name declared and initialized with that object reference. The name speciﬁed is case sensitive and shall conform to the current scripting language variable-naming conventions."
					},
					{
						"name" : "scope",
						"type" : "ATTRIBUTE",
						"code" : "scope=\"\"",
						"fullDescription" : "The scope within which the reference is available. The default value is page. See the description of the scope attribute deﬁned earlier herein. A translation error must occur if scope is not one of “page”, “request”, “session” or “application”."
					},
					{
						"name" : "class",
						"type" : "ATTRIBUTE",
						"code" : "class=\"\"",
						"fullDescription" : "The fully qualiﬁed name of the class that deﬁnes the implementation of the object. The class name is case sensitive."
					},
					{
						"name" : "beanName",
						"type" : "ATTRIBUTE",
						"code" : "beanName=\"\"",
						"fullDescription" : "The name of a bean, as expected by the instantiate method of the java.beans.Beans class."
					},
					{
						"name" : "type",
						"type" : "ATTRIBUTE",
						"code" : "type=\"\"",
						"fullDescription" : "This allows the type of the scripting variable to be distinct from, but related to, the type of the implementation class speciﬁed. The type is required to be either the class itself, a superclass of the class, or an interface implemented by the class speciﬁed."
					} ]
		},
		{
			"name" : "jsp:setProperty",
			"type" : "TAG",
			"code" : "<jsp:setProperty name=\"\" property=\"\" />",
			"fullDescription" : "The jsp:setProperty action sets the values of properties in a bean. The name attribute that denotes the bean must be defined before this action appears.",
			"subTokenList" : [
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "The name of a bean instance deﬁned by a &lt;jsp:useBean> action or some other action. The bean instance must contain the property to be set."
					},
					{
						"name" : "property",
						"type" : "ATTRIBUTE",
						"code" : "property=\"\"",
						"fullDescription" : "The name of the property whose value will be set."
					},
					{
						"name" : "param",
						"type" : "ATTRIBUTE",
						"code" : "param=\"\"",
						"fullDescription" : "The name of the request parameter whose value is given to a bean property. The name of the request parameter usually comes from a web form."
					},
					{
						"name" : "value",
						"type" : "ATTRIBUTE",
						"code" : "value=\"\"",
						"fullDescription" : "The value to assign to the given property"
					} ]
		},
		{
			"name" : "jsp:getProperty",
			"type" : "TAG",
			"code" : "<jsp:getProperty name=\"\" property=\"\" />",
			"fullDescription" : "The <jsp:getProperty> action places the value of a bean instance property, converted to a String, into the implicit out object, from which the value can be displayed as output.",
			"subTokenList" : [
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "The name of the object instance from which the property is obtained."
					}, {
						"name" : "property",
						"type" : "ATTRIBUTE",
						"code" : "property=\"\"",
						"fullDescription" : "Names the property to get."
					} ]
		},
		{
			"name" : "jsp:include",
			"type" : "TAG",
			"code" : "<jsp:include page=\"\" />",
			"fullDescription" : "A <jsp:include .../> action provides for the inclusion of static and dynamic resources in the same context as the current page",
			"subTokenList" : [
					{
						"name" : "page",
						"type" : "ATTRIBUTE",
						"code" : "page=\"\"",
						"fullDescription" : "Relative URL of the resource."
					},
					{
						"name" : "flush",
						"type" : "ATTRIBUTE",
						"code" : "flush=\"\"",
						"fullDescription" : " Optional boolean attribute. If the value is true, the buffer is ﬂushed now. The default value is false"
					} ]
		},
		{
			"name" : "jsp:forward",
			"type" : "TAG",
			"code" : "<jsp:forward page=\"\" />",
			"fullDescription" : "A <jsp:include .../> action provides for the inclusion of static and dynamic resources in the same context as the current page",
			"subTokenList" : [ {
				"name" : "page",
				"type" : "ATTRIBUTE",
				"code" : "page=\"\"",
				"fullDescription" : "Relative URL of the resource."
			} ]
		},
		{
			"name" : "jsp:param",
			"type" : "TAG",
			"code" : "<jsp:param name=\"\" value=\"\" />",
			"fullDescription" : "The jsp:param element is used to provide key/value information.",
			"subTokenList" : [ {
				"name" : "name",
				"type" : "ATTRIBUTE",
				"code" : "name=\"\"",
				"fullDescription" : "Key"
			}, {
				"name" : "value",
				"type" : "ATTRIBUTE",
				"code" : "value=\"\"",
				"fullDescription" : "Value"
			} ]
		},
		{
			"name" : "jsp:plugin",
			"type" : "TAG",
			"code" : "<jsp:plugin code=\"\" codebase=\"\" type=\"bean\"></jsp:plugin>",
			"fullDescription" : "The plugin action enables a JSP page author to generate HTML that contains the appropriate client browser dependent constructs (OBJECT or EMBED) that will result in the download of the Java Plugin software (if required) and subsequent execution",
			"subTokenList" : [
					{
						"name" : "type",
						"type" : "ATTRIBUTE",
						"code" : "type=\"\"",
						"fullDescription" : "Identiﬁes the type of the component; a bean, or an Applet"
					},
					{
						"name" : "code",
						"type" : "ATTRIBUTE",
						"code" : "code=\"\"",
						"fullDescription" : ""
					},
					{
						"name" : "codebase",
						"type" : "ATTRIBUTE",
						"code" : "codebase=\"\"",
						"fullDescription" : ""
					},
					{
						"name" : "align",
						"type" : "ATTRIBUTE",
						"code" : "align=\"\"",
						"fullDescription" : ""
					},
					{
						"name" : "archive",
						"type" : "ATTRIBUTE",
						"code" : "archive=\"\"",
						"fullDescription" : ""
					},
					{
						"name" : "height",
						"type" : "ATTRIBUTE",
						"code" : "height=\"\"",
						"fullDescription" : ""
					},
					{
						"name" : "hspace",
						"type" : "ATTRIBUTE",
						"code" : "hspace=\"\"",
						"fullDescription" : ""
					},
					{
						"name" : "jreversion",
						"type" : "ATTRIBUTE",
						"code" : "jreversion=\"\"",
						"fullDescription" : "Identiﬁes the spec version number of the JRE the component requires in order to operate; the default is: 1.2"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : ""
					},
					{
						"name" : "vspace",
						"type" : "ATTRIBUTE",
						"code" : "vspace=\"\"",
						"fullDescription" : ""
					},
					{
						"name" : "title",
						"type" : "ATTRIBUTE",
						"code" : "title=\"\"",
						"fullDescription" : ""
					},
					{
						"name" : "width",
						"type" : "ATTRIBUTE",
						"code" : "width=\"\"",
						"fullDescription" : ""
					},
					{
						"name" : "nspluginurl",
						"type" : "ATTRIBUTE",
						"code" : "nspluginurl=\"\"",
						"fullDescription" : "URL where JRE plugin can be downloaded for Netscape Navigator, default is implementation deﬁned."
					},
					{
						"name" : "iepluginurl",
						"type" : "ATTRIBUTE",
						"code" : "iepluginurl=\"\"",
						"fullDescription" : "URL where JRE plugin can be downloaded for IE, default is implementation deﬁned."
					}, {
						"name" : "mayscript",
						"type" : "ATTRIBUTE",
						"code" : "mayscript=\"\"",
						"fullDescription" : ""
					} ]
		},
		{
			"name" : "jsp:params",
			"type" : "TAG",
			"code" : "<jsp:params></jsp:params>",
			"fullDescription" : "The jsp:params action is part of the jsp:plugin action and can only occur as a direct child of a &lt;jsp:plugin> action. Using the jsp:params element in any other context shall result in a translation-time error."
		},
		{
			"name" : "jsp:fallback",
			"type" : "TAG",
			"code" : "<jsp:fallback></jsp:fallback>",
			"fullDescription" : "The jsp:fallback action is part of the jsp:plugin action and can only occur as a direct child of a &lt;jsp:plugin> element. Using the jsp:fallback element in any other context shall result in a translation-time error."
		},
		{
			"name" : "jsp:attribute",
			"type" : "TAG",
			"code" : "<jsp:attribute name=\"\"></jsp:attribute>",
			"fullDescription" : "The &lt;jsp:attribute> standard action has two uses. It allows the page author to deﬁne the value of an action attribute in the body of an XML element instead of in the value of an XML attribute. It also allows the page author to specify the attributes of the element being output, when used inside a &lt;jsp:element> action.",
			"subTokenList" : [
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "If not being used with &lt;jsp:element>, then if the action does not accept dynamic attributes, the name must match the name of an attribute for the action being invoked, as declared in the Tag Library Descriptor for a custom action, or as speciﬁed for a standard action, or a translation error will result. Except for when used with &lt;jsp:element>, a translation error will result if both an XML element attribute and a &lt;jsp:attribute> element are used to specify the value for the same attribute."
					},
					{
						"name" : "trim",
						"type" : "ATTRIBUTE",
						"code" : "trim=\"\"",
						"fullDescription" : "Valid values are true and false. If true, the whitespace, including spaces, carriage returns, line feeds, and tabs, that appears at the beginning and at the end of thebody of the &lt;jsp:attribute> action will be ignored by the JSP compiler. If false the whitespace is not ignored. Defaults to true."
					} ]
		},
		{
			"name" : "jsp:body",
			"type" : "TAG",
			"code" : "<jsp:body></jsp:body>",
			"fullDescription" : "Normally, the body of a standard or custom action invocation is defined implicitly as the body of the XML element used to represent the invocation. The body of a standard or custom action can also be defined explicitly using the &lt;jsp:body> standard action. This is required if one or more &lt;jsp:attribute> elements appear in the body of the tag."
		},
		{
			"name" : "jsp:invoke",
			"type" : "TAG",
			"code" : "<jsp:invoke fragment=\"\" />",
			"fullDescription" : "The most basic usage of this standard action will invoke a fragment with the given name with no parameters. The fragment will be invoked using the JspFragment.invoke method, passing in null for the Writer parameter so that the results will be sent to the JspWriter of the JspContext associated with the JspFragment.",
			"subTokenList" : [
					{
						"name" : "fragment",
						"type" : "ATTRIBUTE",
						"code" : "fragment=\"\"",
						"fullDescription" : "The name used to identify this fragment during this tag invocation."
					},
					{
						"name" : "var",
						"type" : "ATTRIBUTE",
						"code" : "var=\"\"",
						"fullDescription" : "The name of a scoped attribute to store the result of the fragment invocation in, as a java.lang.String object."
					},
					{
						"name" : "varReader",
						"type" : "ATTRIBUTE",
						"code" : "varReader=\"\"",
						"fullDescription" : "The name of a scoped attribute to store the result of the fragment invocation in, as a java.io.Reader object."
					},
					{
						"name" : "scope",
						"type" : "ATTRIBUTE",
						"code" : "scope=\"\"",
						"fullDescription" : "The scope in which to store the resulting variable"
					} ]
		},
		{
			"name" : "jsp:doBody",
			"type" : "TAG",
			"code" : "<jsp:doBody></jsp:doBody>",
			"fullDescription" : "The &lt;jsp:doBody> standard action behaves exactly like &lt;jsp:invoke>, except that it operates on the body of the tag instead of on a speciﬁc fragment passed as an attribute.",
			"subTokenList" : [
					{
						"name" : "var",
						"type" : "ATTRIBUTE",
						"code" : "var=\"\"",
						"fullDescription" : "The name of a scoped attribute to store the result of the fragment invocation in, as a java.lang.String object."
					},
					{
						"name" : "varReader",
						"type" : "ATTRIBUTE",
						"code" : "varReader=\"\"",
						"fullDescription" : "The name of a scoped attribute to store the result of the fragment invocation in, as a java.io.Reader object."
					},
					{
						"name" : "scope",
						"type" : "ATTRIBUTE",
						"code" : "scope=\"\"",
						"fullDescription" : "The scope in which to store the resulting variable"
					} ]
		},
		{
			"name" : "jsp:element",
			"type" : "TAG",
			"code" : "<jsp:element name=\"\"></jsp:element>",
			"fullDescription" : "The jsp:element action is used to dynamically define the value of the tag of an XML element.",
			"subTokenList" : [ {
				"name" : "name",
				"type" : "ATTRIBUTE",
				"code" : "name=\"\"",
				"fullDescription" : "The value of name is that of the element genreated. The name can be a QName."
			} ]
		},
		{
			"name" : "jsp:text",
			"type" : "TAG",
			"code" : "<jsp:text></jsp:text>",
			"fullDescription" : "A jsp:text action can be used to enclose template data in a JSP page, a JSP document, or a tag file. A jsp:text action has no attributes and can appear anywhere that template data can. "
		},
		{
			"name" : "jsp:output",
			"type" : "TAG",
			"code" : "<jsp:output></jsp:output>",
			"fullDescription" : "The jsp:output action can only be used in JSP documents and in tag files in XML syntax",
			"subTokenList" : [
					{
						"name" : "omit-xml-declaration",
						"type" : "ATTRIBUTE",
						"code" : "omit-xml-declaration=\"\"",
						"fullDescription" : "Indicates whether to omit the generation of an XML declaration. Acceptable values are “true”, “yes”, “false” and “no”."
					},
					{
						"name" : "doctype-root-element",
						"type" : "ATTRIBUTE",
						"code" : "doctype-root-element=\"\"",
						"fullDescription" : "Must be speciﬁed if and only if doctype-system is speciﬁed or a translation error must occur. Indicates the name that is to be output in the generated DOCTYPE declaration."
					},
					{
						"name" : "doctype-system",
						"type" : "ATTRIBUTE",
						"code" : "doctype-system=\"\"",
						"fullDescription" : "Speciﬁes that a DOCTYPE declaration is to be generated and gives the value for the System Literal."
					},
					{
						"name" : "doctype-public",
						"type" : "ATTRIBUTE",
						"code" : "doctype-public=\"\"",
						"fullDescription" : "Must not be speciﬁed unless doctype-system is speciﬁed. Gives the value for the Public ID for the generated DOCTYPE."
					} ]
		}

]