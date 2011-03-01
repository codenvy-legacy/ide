var Widget = {
	"name" : "Widget",
	"type" : "CLASS",
	"code" : "Widget",
	"fqn" : "UWA",
	"fullDescription" : "<div><p>The Widget class provides abstract methods to create and manipulate UWA widgets.</p><p>The Widget object is typically instanciated as the <b>widget</b> var in a widget execution scope.</p></div>",
	"subTokenList" : [
			{
				"name" : "id",
				"type" : "PROPERTY",
				"code" : "id",
				"shortDescription" : " : String",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>String</b>: Unique identifier of the widget.&nbsp; The value depends on the execution environment: the Environment registration handler sets this property.</p>"
			},
			{
				"name" : "environment",
				"type" : "PROPERTY",
				"code" : "environment",
				"shortDescription" : " : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Object</b>: Reference to the execution environment.&nbsp; The Environment registration handler sets this property.&nbsp; Instance of the Environment class.</p>"
			},
			{
				"name" : "title",
				"type" : "PROPERTY",
				"code" : "title",
				"shortDescription" : " : String",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>String</b>: widget’s title.&nbsp; The title of the widget.&nbsp; It is set by the <i>setTitle</i> method.</p>"
			},
			{
				"name" : "body",
				"type" : "PROPERTY",
				"code" : "body",
				"shortDescription" : " : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Object</b>: widget’s body.&nbsp; The main HTML element of the widget.&nbsp; Value is null until the &lt;widget&gt; is fully registered in the Environment.&nbsp; Should not be used before <i>launch</i> or <i>onLoad</i> are fired.</p>"
			},
			{
				"name" : "data",
				"type" : "PROPERTY",
				"code" : "data",
				"shortDescription" : " : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Object</b>: Stores widget’s data.&nbsp; This property can be modified by the <i>setValue</i> method, and accessed by the <i>getValue</i> method.</p>"
			},
			{
				"name" : "callbacks",
				"type" : "PROPERTY",
				"code" : "callbacks",
				"shortDescription" : " : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Object</b>: Stores widget’s callbacks.</p>"
			},
			{
				"name" : "preferences",
				"type" : "PROPERTY",
				"code" : "preferences",
				"shortDescription" : " : Array",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Array</b>: Stores widget’s preferences.&nbsp; The array is initially empty.&nbsp; It is initialised by the <i>setPreferences</i> method.</p>"
			},
			{
				"name" : "metas",
				"type" : "PROPERTY",
				"code" : "metas",
				"shortDescription" : " : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Object</b>: Stores widget’s metas.&nbsp; The object is initially empty.&nbsp; It is initialised by the <i>setMetas</i> method.</p>"
			},
			{
				"name" : "debugMode",
				"type" : "PROPERTY",
				"code" : "debugMode",
				"shortDescription" : " : Boolean",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Boolean</b>: activates or desactivates the debug mode for the widget.&nbsp; The default value is TRUE.&nbsp; When TRUE, messages written with <i>log</i> method will appear in the console.</p>"
			},
			{
				"name" : "periodicals",
				"type" : "PROPERTY",
				"code" : "periodicals",
				"shortDescription" : " : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Object</b>: Stores widget’s periodical events.&nbsp; The object is initially empty.&nbsp; It is filled by the <i>setPeriodical</i> method.</p>"
			},
			{
				"name" : "searchResultCount",
				"type" : "PROPERTY",
				"code" : "searchResultCount",
				"shortDescription" : " : Integer",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Integer</b>: the search result count when the widget is onSearch.&nbsp; This property is set by the <i>setSearchResultCount</i> method.</p>"
			},
			{
				"name" : "unreadCount",
				"type" : "PROPERTY",
				"code" : "unreadCount",
				"shortDescription" : " : Integer",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Integer</b>: the count of unread items in the widget.&nbsp; The unread count is set by the <i>setUnreadCount</i> method.</p>"
			},
			{
				"name" : "prefsForm",
				"type" : "PROPERTY",
				"code" : "prefsForm",
				"shortDescription" : " : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Object</b>: instance of UWA.Controls.PrefsForm.</p>"
			},
			{
				"name" : "elements",
				"type" : "PROPERTY",
				"code" : "elements",
				"shortDescription" : " : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Object</b>: instances of UI Elements of the widget.</p>"
			},
			{
				"name" : "apiVersion",
				"type" : "PROPERTY",
				"code" : "apiVersion",
				"shortDescription" : " : String",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>String</b>: Current Widget Api version.</p>"
			},
			{
				"name" : "lang",
				"type" : "PROPERTY",
				"code" : "lang",
				"shortDescription" : " : String",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>String</b>: The preferred language as defined by the Environment.</p>"
			},
			{
				"name" : "locale",
				"type" : "PROPERTY",
				"code" : "locale",
				"shortDescription" : " : String",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>String</b>: The preferred locale as defined by the Environment.</p>"
			},
			{
				"name" : "dir",
				"type" : "PROPERTY",
				"code" : "dir",
				"shortDescription" : " : String",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>String</b>: The preferred direction as defined by the Environment.</p>"
			},
			{
				"name" : "readOnly",
				"type" : "PROPERTY",
				"code" : "readOnly",
				"shortDescription" : " : Boolean",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p><b>Boolean</b>: Default to false.&nbsp; True if the widget is currently read only for the viewer.</p>"
			},
			{
				"name" : "launch",
				"type" : "METHOD",
				"code" : "launch()",
				"shortDescription" : "()",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><<p>Launch the widget : call <i>initPreferences</i> then fire widget.onLoad.</p><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Notes</h4><p>Internal or advanced use only</p></div>"
			},
			{
				"name" : "setIcon",
				"type" : "METHOD",
				"code" : "setIcon(url, search)",
				"shortDescription" : "(url, search)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Sets the icon for the Widget.</p><h4>Parameters</h4><ul><li>String url: the url of the icon.&nbsp; The URL should include the protocol (<i>http://</i>).</li><li>Boolean search: If true, try to autodiscover the icon for the given url.&nbsp; Internal use only.</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Example</h4><blockquote><pre>widget.setIcon(\"http://www.netvibes.com/favicon.ico\");</pre></blockquote><p>or</p><blockquote><pre>widget.setIcon(\"http://www.netvibes.com\", true);</pre></blockquote></div>"
			},
			{
				"name" : "setTitle",
				"type" : "METHOD",
				"code" : "setTitle(title, extended)",
				"shortDescription" : "(title, extended)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Sets the title of the Widget.</p><h4>Parameters</h4><ul><li>String title: The title of the widget.&nbsp; Can contain HTML code.</li><li>String extended: An extra string.&nbsp; Internal use only.</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Example</h4><blockquote><pre>widget.setTitle('Netvibes Blog');</pre></blockquote><p>or</p><blockquote><pre>widget.setTitle('&lt;a href=\"http://blog.netvibes.com/\"&gt;Netvibes Blog&lt;/a&gt;');</pre></blockquote><h4>Notes</h4><p>Implementation can differ between environments.</p></div>"
			},
			{
				"name" : "getTitle",
				"type" : "METHOD",
				"code" : "getTitle()",
				"shortDescription" : " : String",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Get the title of the Widget.</p><h4>Parameters</h4><ul><li>Nothing.</li></ul><h4>Returns</h4><ul><li>String: the title of the widget.&nbsp; HTML tags are stripped.</li></ul></div>"
			},
			{
				"name" : "setBody",
				"type" : "METHOD",
				"code" : "setBody(content)",
				"shortDescription" : "(content)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Sets the body of the Widget.&nbsp; Erases the previous body.&nbsp; Use the String setContent function.</p><h4>Parameters</h4><ul><li>Object content: The body of the widget.</li></ul><h4>Returns</h4><ul><li>Nothing, but fire the “onUpdateBody” callback.</li></ul><h4>Example</h4><blockquote><pre>var div = widget.createElement('div');div.addClassName('container');div.setHTML(\"&lt;p&gt;Hello World&lt;/p&gt;\");widget.setBody(div);</pre></blockquote><p>or</p><blockquote><pre>widget.setBody(\"&lt;p&gt;Hello World&lt;/p&gt;\");</pre></blockquote></div>"
			},
			{
				"name" : "addBody",
				"type" : "METHOD",
				"code" : "addBody(content)",
				"shortDescription" : "(content)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Adds contents to the existing body of the Widget.&nbsp; Use the String addContent function.</p><h4>Parameters</h4><ul><li>Object content: The content to add in the body of the widget.</li></ul><h4>Returns</h4><ul><li>Nothing, but calls the methods associated with the “onUpdateBody” callback.</li></ul><h4>Example</h4><blockquote><pre>var div = widget.createElement('div'); div.addClassName('footer'); div.setText(\"Powered by Netvibes UWA.\");widget.addBody(div);</pre></blockquote><p>or</p><blockquote><pre>widget.addBody(\"&lt;p&gt;Powered by Netvibes UWA.&lt;/p&gt;\");</pre></blockquote></div>"
			},
			{
				"name" : "getElements",
				"type" : "METHOD",
				"code" : "getElements(selector)",
				"shortDescription" : "(selector) : Elements",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Alias to UWA.Element.getElements on Widget Body.</p><h4>Returns</h4><ul><li>Elements into an Array or empty Array.</li></ul></div>"
			},
			{
				"name" : "getElement",
				"type" : "METHOD",
				"code" : "getElement(selector)",
				"shortDescription" : "(selector): Element",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Alias to UWA.Element.getElement on Widget Body.</p><h4>Returns</h4><ul><li>Element or undefined.</li></ul></div>"
			},
			{
				"name" : "createElement",
				"type" : "METHOD",
				"code" : "createElement(tagName,options)",
				"shortDescription" : "(tagName,options) : Element",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Creates a new element according to the provided \"tagName\".</p><ul><li>if options is not defined, works like document.createElement(tagName)</li><li>if options is defined, works like JS frameworks DOM builders (mootools/prototype) - new Element(tagName, options)</li></ul><h4>Parameters</h4><ul><li>String tagName: the HTML tag name of the element to create.</li><li>Object options: will be set on the newly-created element using Element#setAttributes.</li></ul><h4>Returns</h4><ul><li>Element: The created element.</li></ul><h4>Example</h4><blockquote><pre>var div = widget.createElement('div');</pre></blockquote><p>or</p><blockquote><pre>var input = widget.createElement('input', {'type': 'submit', 'value': \"Update\");</pre></blockquote></div>"
			},
			{
				"name" : "openURL",
				"type" : "METHOD",
				"code" : "openURL(url)",
				"shortDescription" : "(url)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Open an URL.&nbsp; Behavior differ between execution environments.</p><ul><li>open the page in an iframe on the same screen</li><li>open the page in a new window/tab</li><li>open the page in a new browser window (desktop widgets)</li></ul><h4>Parameters</h4><ul><li>String url: the url to open in a new window.</li></ul></div>"
			},
			{
				"name" : "getUrl",
				"type" : "METHOD",
				"code" : "getUrl()",
				"shortDescription" : ": Url",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><h4>Returns</h4><ul><li>Url of the current widget.</li></ul></div>"
			},
			{
				"name" : "addStar",
				"type" : "METHOD",
				"code" : "addStar(data)",
				"shortDescription" : "(data)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Star Widget into Environment if available.</p><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
			},
			{
				"name" : "initPreferences",
				"type" : "METHOD",
				"code" : "initPreferences()",
				"shortDescription" : "()",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Initializes preferences of the widget.&nbsp; The method gets values from the environnement.&nbsp; If values do not exist in the environment, it sets them to their default values.&nbsp; This method is likely internaly fired by the <i>launch</i> method of the Widget.</p><h4>Parameters</h4><ul><li>None.</li></ul></div>"
			},
			{
				"name" : "getPreference",
				"type" : "METHOD",
				"code" : "getPreference(name)",
				"shortDescription" : "(name) : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Get a preference with its name.</p><h4>Parameters</h4><ul><li>String name : the name of the preference.</li></ul><h4>Returns</h4><ul><li>Object : a preference in its JSON serialization.</li></ul><h4>Example</h4><p>If you have this preference defined in XML</p><blockquote><pre>&lt;preference name=\"limit\" type=\"range\" label=\"Number of items to display\" defaultValue=\"5\" step=\"1\" min=\"1\" max=\"25\" /&gt;</pre></blockquote><p>You can get its javascript representation with the following code</p><blockquote><pre>widget.getPreference(\"limit\")</pre></blockquote></div>"
			},
			{
				"name" : "setPreferences",
				"type" : "METHOD",
				"code" : "setPreferences(schema)",
				"shortDescription" : "(schema)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Sets preferences schema of the widget.&nbsp; Replaces previous preferences.</p><h4>Parameters</h4><ul><li>Array schema: an Array of preferences in their JSON serialization</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Example</h4><blockquote><pre>widget.setPreferences([{\"name\":\"paging\",\"type\":\"boolean\",\"label\":\"Enable pagination\",\"defaultValue\":\"false\"},{\"name\":\"offset\",\"type\":\"hidden\",\"defaultValue\":\"0\"}]);</pre></blockquote></div>"
			},
			{
				"name" : "setPreferencesXML",
				"type" : "METHOD",
				"code" : "setPreferencesXML(prefs)",
				"shortDescription" : "(prefs)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Sets preferences schema of the widget from an XML source.&nbsp; Replaces previous preferences.</p><h4>Parameters</h4><ul><li>Object prefs: an XML UWA preferences document</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
			},
			{
				"name" : "getPreferences",
				"type" : "METHOD",
				"code" : "getPreferences()",
				"shortDescription" : " : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Gets preferences schema of the widget.</p><h4>Returns</h4><ul><li>Object: Widget preferences schema with current values for each preference.</li></ul><h4>Example</h4><blockquote><pre>widget.getPreferences();</pre></blockquote></div>"
			},
			{
				"name" : "mergePreferences",
				"type" : "METHOD",
				"code" : "mergePreferences(prefs)",
				"shortDescription" : "(prefs)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Add preferences to the widget if preferences of the same name are not already defined.</p><h4>Parameters</h4><ul><li>Array schema: an Array of preferences in their JSON serialization</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
			},
			{
				"name" : "addPreference",
				"type" : "METHOD",
				"code" : "addPreference(preference)",
				"shortDescription" : "(preference)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Adds a single preference to the existing preferences of the widget.</p><h4>Parameters</h4><ul><li>Object : a preference in its JSON serialization</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
			},
			{
				"name" : "getValue",
				"type" : "METHOD",
				"code" : "getValue(name)",
				"shortDescription" : "(name) : String",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Gets the value of the given preference.</p><h4>Parameters</h4><ul><li>String - name: the name of the preference we want the value of.</li></ul><h4>Returns</h4><ul><li>String : the current value of the preference</li></ul><h4>Example</h4><blockquote><pre>var url = widget.getValue(\"feedUrl\");</pre></blockquote></div>"
			},
			{
				"name" : "getInt",
				"type" : "METHOD",
				"code" : "getInt(name)",
				"shortDescription" : "(name) : Number",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Gets the Integer value of the given preference.</p><p>It is particularly advised to use getInt when a preference is of type range.</p><h4>Parameters</h4><ul><li>String name: the name of the preference we want the value of.</li></ul><h4>Returns</h4><ul><li>Number: the current value of the preference, converted as integer.</li></ul></div>"
			},
			{
				"name" : "getBool",
				"type" : "METHOD",
				"code" : "getBool(name)",
				"shortDescription" : "(name) : Boolean",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Gets the Boolean value of the given preference.</p><p>It is particularly advised to use getBool when a preference is of type boolean.</p><h4>Parameters</h4><ul><li>String name: the name of the preference we want the value of.</li></ul><h4>Returns</h4><ul><li>Boolean: the current value of the preference, converted as boolean.</li></ul></div>"
			},
			{
				"name" : "setValue",
				"type" : "METHOD",
				"code" : "setValue(name, value)",
				"shortDescription" : "(name, value) : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Sets the value of the given preference.</p><h4>Parameters</h4><ul><li>String name: the name of the preference we want to set.</li><li>String value: the value of the preference</li></ul><h4>Returns</h4><ul><li>Object: the value of the preference we set.</li></ul><h4>Example</h4><blockquote><pre>widget.setValue(\"nbItems\", \"5\");</pre></blockquote></div>"
			},
			{
				"name" : "deleteValue",
				"type" : "METHOD",
				"code" : "deleteValue(name)",
				"shortDescription" : "(name)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Delete value of the given preference.</p><h4>Parameters</h4><ul><li>String name: the name of the preference we want the value of.</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
			},
			{
				"name" : "saveValues",
				"type" : "METHOD",
				"code" : "savaValues(callback)",
				"shortDescription" : "(callback)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Request Widget Environment to save preferences values and call callback function.</p><h4>Parameters</h4><ul><li>Function callback: the callback function call after save preferences value.</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
			},
			{
				"name" : "getInfos",
				"type" : "METHOD",
				"code" : "getInfos()",
				"shortDescription" : " : Object",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Set the some basics metas of the widget to index widget.</p><h4>Returns</h4><ul><li>Object: With basics metas of the widget has properties.</li></ul><h4>Notes</h4><p>internal or advanced use only</p></div>"
			},
			{
				"name" : "setMetas",
				"type" : "METHOD",
				"code" : "setMetas(metas)",
				"shortDescription" : "(metas)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Set the metas of the widget.</p><h4>Parameters</h4><ul><li>Object metas: metas in a key:value form</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Notes</h4><p>internal or advanced use only</p></div>"
			},
			{
				"name" : "setMetasXML",
				"type" : "METHOD",
				"code" : "setMetasXML(metas)",
				"shortDescription" : "(metas)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Set the metas of the widget from an XML source.</p><h4>Parameters</h4><ul><li>Object metas: Xml Document instance</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Notes</h4><p>internal or advanced use only</p></div>"
			},
			{
				"name" : "setPeriodical",
				"type" : "METHOD",
				"code" : "setPeriodical(name, fn, delay, force)",
				"shortDescription" : "(name, fn, delay, force)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Register a function as periodical event.&nbsp; The function will automatically be binded to the current widget object.</p><h4>Parameters</h4><ul><li>String name: the name of the event</li><li>Function fn: the function to register</li><li>Integer delay: the execution delay in milliseconds</li><li>Boolean force: If true, fire the function for the time right now.</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Notes</h4><p>internal or advanced use only</p></div>"
			},
			{
				"name" : "clearPeriodical",
				"type" : "METHOD",
				"code" : "clearPeriodical(name)",
				"shortDescription" : "(name)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Unregister a periodical event previously registered with <i>setPeriodical</i></p><h4>Parameters</h4><ul><li>String name: the name of the event</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Notes</h4><p>internal or advanced use only</p></div>"
			},
			{
				"name" : "callback",
				"type" : "METHOD",
				"code" : "callback(name, args, bind)",
				"shortDescription" : "(name, args, bind)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Executes the callback method associated with the given callback name (key).&nbsp; Returns false if no callback method is associated with the given key.</p><h4>Parameters</h4><ul><li>String name: the callback name (e.g.&nbsp; \"onUpdateTitle\");</li><li>Object args: one optional argument</li><li>Object: an object to bind the callback to</li></ul><h4>Returns</h4><ul><li>Nothing, but calls the method associated with the given callback name (key)</li></ul></div>"
			},
			{
				"name" : "setCallback",
				"type" : "METHOD",
				"code" : "setCallback(name, fn)",
				"shortDescription" : "(name, fn)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Add callback method associated with the given callback name (key).</p><h4>Parameters</h4><ul><li>String name: the callback name (e.g.&nbsp; \"onUpdateTitle\");</li><li>Object fn: callback function</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Notes</h4><p>internal or advanced use only</p></div>"
			},
			{
				"name" : "onLoad",
				"type" : "METHOD",
				"code" : "onLoad()",
				"shortDescription" : "()",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Callback called when Widget <i>launch</i> is called.</p><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
			},
			{
				"name" : "onRefesh",
				"type" : "METHOD",
				"code" : "onRefesh()",
				"shortDescription" : "()",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Callback called when Widget refresh requested, call <i>onLoad</i> by default</p><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
			},
			{
				"name" : "onEdit",
				"type" : "METHOD",
				"code" : "onEdit()",
				"shortDescription" : "()",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Callback called when edition of Widget Preferences begin.</p><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
			},
			{
				"name" : "endEdit",
				"type" : "METHOD",
				"code" : "endEdit()",
				"shortDescription" : "()",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Callback called when edition of Widget Preferences ending.</p><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
			},
			{
				"name" : "setAutoRefresh",
				"type" : "METHOD",
				"code" : "setAutoRefresh(delay)",
				"shortDescription" : "(delay)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Sets the auto-refresh interval for the widget.&nbsp; The widget must have a \"onRefresh\" method to work properly.</p><h4>Parameters</h4><ul><li>Integer - delay: the refresh delay, in <b>minutes</b>.</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Example</h4><blockquote><pre>widget.setAutoRefresh(20); // Set the auto-refresh interval to 20 minutes</pre></blockquote></div>"
			},
			{
				"name" : "setSearchResultCount",
				"type" : "METHOD",
				"code" : "setSearchResultCount(count)",
				"shortDescription" : "(count)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Sets the search result count.</p><h4>Parameters</h4><ul><li>Integer - count: the count of results for the current search terms.</li></ul><h4>Returns</h4><ul><li>Nothing, but updates the title with the result count, if greater or equal to zero.</li></ul></div>"
			},
			{
				"name" : "setUnreadCount",
				"type" : "METHOD",
				"code" : "setUnreadCount(count)",
				"shortDescription" : "(count)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Sets the count of unread items.</p><h4>Parameters</h4><ul><li>Integer - count: the count of unread items.</li></ul><h4>Returns</h4><ul><li>Nothing, but updates the title with the unread count, if greater or equal to zero.</li></ul></div>"
			},
			{
				"name" : "getHistory",
				"type" : "METHOD",
				"code" : "getHistory()",
				"shortDescription" : "()",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Get History value From Environment or from widget Preference “history”.</p></div>"
			},
			{
				"name" : "setHistory",
				"type" : "METHOD",
				"code" : "setHistory(history)",
				"shortDescription" : "(history)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Set History value into Environment or has widget Preference \"history\".</p></div>"
			},
			{
				"name" : "saveHistory",
				"type" : "METHOD",
				"code" : "saveHistory()",
				"shortDescription" : "()",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<p>Save History into Environment</p>"
			},
			{
				"name" : "setDebugMode",
				"type" : "METHOD",
				"code" : "setDebugMode(mode)",
				"shortDescription" : "(mode)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Set the debugMode of the widget.</p><h4>Parameters</h4><ul><li>boolean mode: true to enable debug else false</li></ul><h4>Notes</h4><p>internal or advanced use only</p></div>"
			},
			{
				"name" : "log",
				"type" : "METHOD",
				"code" : "log(message)",
				"shortDescription" : "(message)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Logs widget’s messages in the console, if one exists and if the \"<i>debugMode</i>\" is true.&nbsp; It is using <i>UWA.log</i> which usually works with Firebug, Safari and Opera.</p><h4>Parameters</h4><ul><li>String message: the message to display in the console.</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Example</h4><blockquote><pre>widget.log(\"Widget is loading\");</pre></blockquote></div>"
			},
			{
				"name" : "setStyle",
				"type" : "METHOD",
				"code" : "setStyle(style)",
				"shortDescription" : "(style)",
				"fqn" : "UWA.Widget",
				"fullDescription" : "<div><p>Set the stylesheet of the widget with the given CSS rules.</p><h4>Notes</h4><p>Internal or advanced use only</p></div>"
			},
			{
				"name" : "navigator",
				"type" : "PROPERTY",
				"code" : "navigator",
				"shortDescription" : " : Navigator",
				"varType" : "Navigator",
				"fqn" : "Window",
				"fullDescription" : "<div><p>Set History value into Environment or has widget Preference \"history\".</p></div>"
			},
			]
}
