var Element = {
	"name" : "Element",
	"type" : "CLASS",
	"code" : "Element",
	"fqn" : "UWA",
	"fullDescription" : "<div><p>Document Object Model extensions.</p></div>",
	"subTokenList" : [
			{
				"name" : "setAttributes",
				"type" : "METHOD",
				"code" : "setAttributes(attributes)",
				"shortDescription" : "(attributes) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Sets the attributes values of the Element.&nbsp; Attributes are passed as either a hash or a name/value pair.</p><h4>Parameters</h4><ul><li>Object attributes: attributes hash or a name/value pair.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul><h4>Example</h4><blockquote><pre>element.setAttributes({href: \"http://...\", title: \"MyLink\"});</pre></blockquote></div>"
			},
			{
				"name" : "setText",
				"type" : "METHOD",
				"code" : "setText(text)",
				"shortDescription" : "(text) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Sets the inner text of the Element.</p><h4>Parameters</h4><ul><li>String text: the text value.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul><h4>Example</h4><blockquote><pre>element.setText(\"Loading...\");</pre></blockquote></div>"
			},
			{
				"name" : "appendText",
				"type" : "METHOD",
				"code" : "appendText(text)",
				"shortDescription" : "(text) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Adds a new text node at the end of the element’s existing content.</p><h4>Parameters</h4><ul><li>String text: the text value.</li></ul><h4>Returns</h4><ul><li>Object: new text node.</li></ul></div>"
			},
			{
				"name" : "setContent",
				"type" : "METHOD",
				"code" : "setContent(content)",
				"shortDescription" : "(content) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Set content of Element by providing another Element or String with HTML.</p><h4>Parameters</h4><ul><li>Void content: the content value.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "addContent",
				"type" : "METHOD",
				"code" : "addContent(content)",
				"shortDescription" : "(content) : Node",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Add Element content to current Element.</p><h4>Parameters</h4><ul><li>Void content: Can be an Element of an String.</li></ul><h4>Returns</h4><ul><li>New Node added to Element childs.</li></ul><h4>Status</h4><p>to be deprecated</p></div>"
			},
			{
				"name" : "inject",
				"type" : "METHOD",
				"code" : "inject(el, where)",
				"shortDescription" : "(el, where) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Insert the Element inside the passed element.</p><h4>Parameters</h4><ul><li>Object el: the Element to inject.</li><li>String where: the position where the element will be injected, default is “bottom”.</li></ul><h4>Returns</h4><ul><li>Object: injected Element instance.</li></ul></div>"
			},
			{
				"name" : "setHTML",
				"type" : "METHOD",
				"code" : "setHTML(html)",
				"shortDescription" : "(html) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Sets the innerHTML of the Element.</p><h4>Parameters</h4><ul><li>String html: the html value.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "empty",
				"type" : "METHOD",
				"code" : "empty()",
				"shortDescription" : "() : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Empty an element of all its children.</p><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "remove",
				"type" : "METHOD",
				"code" : "remove()",
				"shortDescription" : "() : Object ",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Completely removes element from the document and returns it.</p><h4>Returns</h4><ul><li>Object: the current UWA.Element instance of parent node.</li></ul></div>"
			},
			{
				"name" : "getParent",
				"type" : "METHOD",
				"code" : "getParent()",
				"shortDescription" : "() : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Return a reference to the element’s parent node.</p><h4>Returns</h4><ul><li>Object: the current UWA.Element instance of parent node.</li></ul></div>"
			},
			{
				"name" : "getChildren",
				"type" : "METHOD",
				"code" : "getChildren()",
				"shortDescription" : "() : Objects",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Get collection of the element’s child nodes.</p><h4>Returns</h4><ul><li>Objects: collection of the element’s child nodes.</li></ul></div>"
			},
			{
				"name" : "getDimensions",
				"type" : "METHOD",
				"code" : "getDimensions()",
				"shortDescription" : "() : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Finds the computed width and height of element and returns them as key/value pairs of an object.</p><h4>Returns</h4><ul><li>Object: Object with \"width\" and \"height\" property.</li></ul></div>"
			},
			{
				"name" : "getPosition",
				"type" : "METHOD",
				"code" : "getPosition()",
				"shortDescription" : "() : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Gets the cumulative offset of the x and y position (or the cumulative top and left) of an object.&nbsp; This function is helpful if you want to position things around a certain element.</p><h4>Returns</h4><ul><li>Object: Object with \"y\" and \"x\" property.</li></ul></div>"
			},
			{
				"name" : "hasClassName",
				"type" : "METHOD",
				"code" : "hasClassName(className)",
				"shortDescription" : "(className) : Bool",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Checks whether element has the given CSS className.</p><h4>Parameters</h4><ul><li>String className: the className to check.</li></ul><h4>Returns</h4><ul><li>Bool: true if Element has className else false.</li></ul></div>"
			},
			{
				"name" : "addClassName",
				"type" : "METHOD",
				"code" : "addClassName(className)",
				"shortDescription" : "(className) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Adds a CSS class to element.</p><h4>Parameters</h4><ul><li>String className: the className to add.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "removeClassName",
				"type" : "METHOD",
				"code" : "removeClassName(className)",
				"shortDescription" : "(className) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Removes element’s CSS className and returns element.</p><h4>Parameters</h4><ul><li>String className: the className to remove.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "hide",
				"type" : "METHOD",
				"code" : "hide()",
				"shortDescription" : "() : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Hides and returns element, by setting \"display\" style property to \"none\".</p><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "show",
				"type" : "METHOD",
				"code" : "show()",
				"shortDescription" : "() : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Displays and returns element, by setting \"display\" style property to \"\".</p><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "toggle",
				"type" : "METHOD",
				"code" : "toggle()",
				"shortDescription" : "() : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Toggles the visibility of element.</p><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "setStyle",
				"type" : "METHOD",
				"code" : "setStyle(property, value)",
				"shortDescription" : "(property, value) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Modifies element’s CSS style properties.&nbsp; Styles are passed as either a hash or a name/value pair.</p><h4>Parameters</h4><ul><li>Object/String property: style property name or styles hash or a name/value pair.</li><li>String value: sstyle property value.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "setStyles",
				"type" : "METHOD",
				"code" : "setStyles(styles)",
				"shortDescription" : "(styles) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Modifies element’s CSS style properties.&nbsp; Styles are passed as either a hash or a name/value pair.</p><h4>Parameters</h4><ul><li>Object attributes: styles hash or a name/value pair.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "getStyle",
				"type" : "METHOD",
				"code" : "getStyle(property)",
				"shortDescription" : "(property) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : ""
			},
			{
				"name" : "setOpacity",
				"type" : "METHOD",
				"code" : "setOpacity(value)",
				"shortDescription" : "(value) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Set element’s CSS style opacity value.</p><h4>Parameters</h4><ul><li>String value: style opacity value.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "addEvent",
				"type" : "METHOD",
				"code" : "addEvent(event, fn)",
				"shortDescription" : "(event, fn) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : ""
			},
			{
				"name" : "addEvents",
				"type" : "METHOD",
				"code" : "addEvents(events)",
				"shortDescription" : "(events) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Add multiple Events on Element.</p><h4>Parameters</h4><ul><li>Object events: events hash or a name/callback pair.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "removeEvent",
				"type" : "METHOD",
				"code" : "removeEvent(event, fn)",
				"shortDescription" : "(event, fn) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Remove Event on Element.</p><h4>Parameters</h4><ul><li>String event: the event name.</li><li>Function fn: the event callback function.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			},
			{
				"name" : "removeEvents",
				"type" : "METHOD",
				"code" : "removeEvents(events)",
				"shortDescription" : "(events) : Object",
				"fqn" : "UWA.Element",
				"fullDescription" : "<div><p>Remove Events on Element.</p><h4>Parameters</h4><ul><li>Object events: events hash or a name/callback pair.</li></ul><h4>Returns</h4><ul><li>Object: the current UWA.Element instance.</li></ul></div>"
			} ]
}