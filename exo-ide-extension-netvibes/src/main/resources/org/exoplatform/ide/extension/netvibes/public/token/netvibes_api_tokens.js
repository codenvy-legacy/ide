var netvibes_api_tokens = [
		{
			"name" : "UWA",
			"type" : "CLASS",
			"code" : "UWA",
			"subTokenList" : [

					{
						"name" : "extend",
						"type" : "METHOD",
						"code" : "extend(original,extended)",
						"shortDescription" : "(original,extended)",
						"fqn" : "UWA",
						"fullDescription" : "<div><h3><a name=\"UWA.extend\"></a>UWA.<wbr></wbr>extend</h3><div><p>Copies all the properties from the second passed object to the first passed Object.</p><h4>Parameters</h4><ul><li>Object original: The object to extends.</li><li>Object extended: The object to copy.</li></ul></div></div>"
					},
					{
						"name" : "merge",
						"type" : "METHOD",
						"code" : "merge(object,object)",
						"shortDescription" : "(object,object)",
						"fqn" : "UWA",
						"fullDescription" : "<div><p>Copies the properties from the second passed object to the first passed Object if it not exists already.</p><h4>Example</h4><blockquote><pre>UWA.merge({toto: \"tata\", tutu: \"titi\"}, {toto: \"titi\"});</pre></blockquote></div>"
					},
					{
						"name" : "clone",
						"type" : "METHOD",
						"code" : "clone(object)",
						"shortDescription" : "(object)",
						"fqn" : "UWA",
						"fullDescription" : "<div><p>Copies the properties from the object and remove references.</p><h4>Parameters</h4><ul><li>Object object: The object to copy.</li></ul><h4>Example</h4><blockquote><pre>var Toto = {toto: \"titi\"};/nvar Titi = UWA.clone(Toto);</pre></blockquote></div>"
					},
					{
						"name" : "toArray",
						"type" : "METHOD",
						"code" : "toArray(object)",
						"shortDescription" : "(object) : Array",
						"fqn" : "UWA",
						"fullDescription" : "Convert an object to an Array"
					},
					{
						"name" : "log",
						"type" : "METHOD",
						"code" : "log(message)",
						"shortDescription" : "(message)",
						"fqn" : "UWA",
						"fullDescription" : "<div><p>Log a message to a console, if one available.</p><h4>Parameters</h4><ul><li>String message: The message to log.</li></ul><h4>Example</h4><blockquote><pre>UWA.log(\"Widget is loading ...\")</pre></blockquote></div>"
					},
					{
						"code" : "Data",
						"fqn" : "UWA",
						"fullDescription" : "The Data class provides abstract methods to access external resources using Ajax (XMLHttpRequest) requests.",
						"name" : "Data",
						"type" : "CLASS",
						"subTokenList" : [
								{
									"code" : "useJsonpRequest",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><b>Boolean</b></td><td>Enable JsonP Request for Cross domain Ajax Request</td></tr></tbody></table></div>",
									"name" : "useJsonpRequest",
									"shortDescription" : ": Boolean",
									"type" : "PROPERTY"
								},
								{
									"code" : "useProxyRequest",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td ><b>Boolean</b></td><td>Enable Proxy Request for Cross domain Ajax Request</td></tr></tbody></table></div>",
									"name" : "useProxyRequest",
									"shortDescription" : ": Boolean",
									"type" : "PROPERTY"
								},
								{
									"code" : "useOfflineRequest",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><b>Boolean</b></td><td>Enable Offline JsonP Request offline using UWA.Data.Storage</td></tr></tbody></table></div>",
									"name" : "useOfflineRequest",
									"shortDescription" : ": Boolean",
									"type" : "PROPERTY"
								},
								{
									"code" : "getFeed(url, callback)",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">getFeed: function (</td><td nowrap=\"\">url,</td></tr><tr><td></td><td nowrap=\"\">callback</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Gets the content of a feed, in a JSON format.</p><h4>Parameters</h4><ul><li>String url: the URL of the feed data source.</li><li>Function callback: the callback method that will be triggered when the request is succesful.  This method <b>must have one parameter</b> to receive the feed (JSON format) returned by the request.</li></ul><h4 >Returns</h4><ul><li>Nothing, but if the request is successful, the callback method is fired and receives the feed as parameter.</li></ul><h4>Example</h4><blockquote><pre>UWA.Data.getFeed('http://feeds.feedburner.com/NetvibesDevBlog', MyWidget.display);<br>MyWidget.display = function (aFeed) {<br>// your display code<br>}</pre></blockquote><h4>Notes</h4><p>In this example, the callback method is named display, and is used to display the feed content, which is contained in the aFeed variable.</p></div>",
									"name" : "getFeed",
									"shortDescription" : "(url, callback)",
									"type" : "METHOD"
								},
								{
									"code" : "getXml(url, callback)",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">getText: function (</td><td nowrap=\"\">url,</td></tr><tr><td></td><td nowrap=\"\">callback</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>This method is used to get the content of an external data source.  It can be used to retrieve any kind of content, as long as it is made of text.</p><h4>Parameters</h4><ul><li>String url: the URL of the data source,</li><li>Function callback: the callback method that will be fired when the request is succesful.  This method <b>must have one parameter</b> to receive the text content returned by the request.</li></ul><h4>Returns</h4><ul><li>Nothing, but if the request is successful, the callback method is fired and receives the XML content as parameter.</li></ul><h4>Notes</h4><p>In this example, the callback method is named 'parse', and is used to display the feed content, which is contained in the 'text' variable.</p><h4>Example</h4><blockquote><pre>UWA.Data.getText('http://example.com/content.txt', MyWidget.parse);<br>MyWidget.parse = function (xml) { <br>  // your parsing code<br><br>}<br></pre></blockquote></div>",
									"name" : "getXml",
									"shortDescription" : "(url, callback)",
									"type" : "METHOD"
								},
								{
									"code" : "getText(url, callback)",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">getText: function (</td><td nowrap=\"\" >url,</td></tr><tr><td></td><td nowrap=\"\">callback</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>This method is used to get the content of an external data source.  It can be used to retrieve any kind of content, as long as it is made of text.</p><h4>Parameters</h4><ul><li>String url: the URL of the data source,</li><li>Function callback: the callback method that will be fired when the request is succesful.  This method <b>must have one parameter</b> to receive the text content returned by the request.</li></ul><h4>Returns</h4><ul><li>Nothing, but if the request is successful, the callback method is fired and receives the XML content as parameter.</li></ul><h4>Notes</h4><p>In this example, the callback method is named 'parse', and is used to display the feed content, which is contained in the 'text' variable.</p><h4>Example</h4><blockquote><pre>UWA.Data.getText('http://example.com/content.txt', MyWidget.parse);<br>MyWidget.parse = function (text) {<br> // your parsing code<br>}</pre></blockquote></div>",
									"name" : "getText",
									"shortDescription" : "(url, callback)",
									"type" : "METHOD"
								},
								{
									"code" : "request(url, options)",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><h3 ><a name=\"request\"></a>request</h3><div ><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">request: function (</td><td nowrap=\"\">url,</td></tr><tr><td></td><td nowrap=\"\">options</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>This method is used to get the content of an external data source.  It can be used to retrieve or set any kind of data: text-based, XML, JSON or a feed.  The other Ajax methods (getText(), getXml(), getJson(), getFeed()) are all shortcut methods to specific uses of request().  This method is also the only way to perform HTTP POST request, as well as authenticated requests</p><h4>Parameters</h4><ul><li>String url: the URL of the data source.</li><li>Object options: a JavaScript object containing setting/value pairs.  This object can take a handful of settings, the only required one being 'onComplete', because you need to always set a callback method that will receive the Ajax response.  That method <b>must have one parameter</b> to receive the JSON content returned by the request.</li></ul><h4>Sample request object</h4><blockquote><pre>{ method : 'get', proxy: 'ajax', type: 'xml', onComplete: callback }</pre></blockquote><h4>Available methods</h4><blockquote><pre>^ Setting           ^ Options                      ^ Default option<br>method              GET, POST (in uppercase!)      GET<br>type                json, xml, text, html          text<br>proxy               ajax, feed                     ajax<br>async               asynchronous XmlRpc request    true<br>cache               seconds of server caching      undefined<br>onComplete          choose your own method         undefined<br>onTimeout           choose your own method         throw<br>onComplete          choose your own method         throw<br>parameters          your POST parameters           undefined<br>postBody            in case you need to set it     undefined<br>authentication      the auth object. See doc       undefined<br>headers             the headers object. See doc    undefined<br>useOfflineRequest   enable Offline cache           false<br>useJsonpRequest     enable JsonP Fallback          true<br></pre></blockquote><h4>Returns</h4><ul><li>Nothing, but if the request is successful, the callback method is fired and receives the content as parameter.</li></ul><h4>Example</h4><blockquote><pre>UWA.Data.request('http://example.org/api.php', {<br>  method: 'get',<br>  proxy: 'ajax',<br>  type: 'xml',<br>  cache: 3600,<br>onComplete: MyWidget.parse<br>});<br><br>MyWidget.parse = function (response) {<br>  // your parsing code<br>}</pre></blockquote></div></div>",
									"name" : "request",
									"shortDescription" : "(url, options)",
									"type" : "METHOD"
								},
								{
									"code" : "(url, options)",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><p>Compare 2 url to check if domain and protocol match.</p><h4>Parameters</h4><ul><li>String originaUrl: first url to compare.</li><li>String requestUrl: second url to compare.</li></ul><h4>Returns</h4><ul><li>Boolean: true if urls match else false.</li></ul></div>",
									"name" : "domainMatch",
									"shortDescription" : "(url, options) : Boolean",
									"type" : "METHOD"
								},
								{
									"code" : "proxifyUrl (url, options)",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">proxifyUrl: function (</td><td nowrap=\"\">url,</td></tr><tr><td></td><td nowrap=\"\">options</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Proxify an URL using UWA proxy.</p><h4>Parameters</h4><ul><li>String url: url to proxify.</li><li>String options: some options from <a onmouseout=\"HideTip('tt5')\" onmouseover=\"ShowTip(event, 'tt5', 'link19')\" id=\"link19\" href=\"#request\">request</a> options parameter.</li></ul><h4 >Returns</h4><ul><li>String: full url to proxy with proxified has parameter.</li></ul></div>",
									"name" : "proxifyUrl",
									"shortDescription" : "(url, options)",
									"type" : "METHOD"
								},
								{
									"code" : "getOfflineCache ()",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>getOfflineCache: function ()</td></tr></tbody></table></blockquote><p>Get UWA.Data.Storage instance for UWA.Data.request cache.</p><h4>Returns</h4><ul><li>Object: Instance of UWA.Data.Storage.Abstract.</li></ul></div>",
									"name" : "getOfflineCache",
									"shortDescription" : "()",
									"type" : "METHOD"
								},
								{
									"code" : "storeInCache (url, callbackArguments)",
									"fqn" : "UWA.Data",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">storeInCache: function (</td><td nowrap=\"\">url,</td></tr><tr><td></td><td nowrap=\"\">callbackArguments</td><td nowrap=\"\")</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Store into UWA.Data.Storage instance for UWA.Data.request cache the request result.</p><h4>Parameters</h4><ul><li>String url: the URL of the data source.</li><li>Object callbackArguments: the arguments of request callback</li></ul><h4>Returns</h4><ul><li>Void: Stored value.</li></ul></div>",
									"name" : "storeInCache",
									"shortDescription" : "(url, callbackArguments)",
									"type" : "METHOD"
								} ]

					},
					{
						"name" : "Json",
						"type" : "CLASS",
						"code" : "Json",
						"fqn" : "UWA",
						"fullDescription" : "The Json class provides abstract methods to access external resources using JSON",
						"subTokenList" : [
								{
									"name" : "jsonp",
									"type" : "PROPERTY",
									"code" : "jsonp",
									"shortDescription" : ": Array",
									"fqn" : "UWA.Json",
									"fullDescription" : "<b>Array</b>: Array to hold active jsonp calls"
								},
								{
									"name" : "encode",
									"type" : "METHOD",
									"code" : "encode()",
									"fqn" : "UWA.Json",
									"fullDescription" : "<b>Function</b>: Alias to JSON.stringify native browser function"
								},
								{
									"name" : "decode",
									"type" : "METHOD",
									"code" : "decode()",
									"fqn" : "UWA.Json",
									"fullDescription" : "<b>Function</b>: Alias to JSON.parse native browser function"
								},
								{
									"name" : "useOfflineRequest",
									"type" : "PROPERTY",
									"code" : "useOfflineRequest",
									"shortDescription" : ": Boolean",
									"fqn" : "UWA.Json",
									"fullDescription" : "<b>Boolean</b>: Enable Offline JsonP Request offline using UWA.Data.Storage"
								},
								{
									"name" : "request",
									"type" : "METHOD",
									"code" : "request(url, options)",
									"shortDescription" : "(url,options)",
									"fqn" : "UWA.Json",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>"
											+ "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\" >"
											+ "request: function (</td><td nowrap=\"\" >url,</td></tr><tr><td></td><td nowrap=\"\" >options</td>"
											+ "<td nowrap=\"\" >)</td></tr></tbody></table></td></tr></tbody></table></blockquote>"
											+ "<p>Make JSONP Request or Ajax for Cross Domain (<a target=\"_top\" "
											+ "href=\"http://en.wikipedia.org/wiki/JSON#JSONP\">http://en.wikipedia.org/wiki/JSON#JSONP</a>).</p>"
											+ "<h4 >Parameters</h4><ul><li>String url: the URL of the data source.</li>"
											+ "<li>Object request: a JavaScript object containing setting/value pairs.&nbsp; "
											+ "This object can take a handful of settings, the only required one being ‘onComplete’, "
											+ "because you need to always set a callback method that will receive the Ajax response.&nbsp; "
											+ "That method <b>must have one parameter</b> to receive the JSON content returned by the request.</li></ul>"
											+ "<h4>Sample request object</h4><blockquote><pre>{ method : 'get', onComplete: callback }</pre>"
											+ "</blockquote><h4>Available methods</h4><blockquote><pre>"
											+ "^ Setting          ^ Options                        ^ Default option\n"
											+ "  method             GET, POST (in uppercase!)        GET\n"
											+ "  async              use async JsonP                  true\n"
											+ "  timeout            set you request timeout in ms    10000\n"
											+ "  onComplete         choose your own method           undefined\n"
											+ "  onTimeout          choose your own method           throw\n"
											+ "  onComplete         choose your own method           throw\n"
											+ "  callbackMode       JsonP mode function or object    function\n"
											+ "  callbackName       JsonP callback name              callback\n"
											+ "  useOfflineRequest  enable Offline cache             false</pre>"
											+ "</blockquote><h4 >Returns</h4><ul><li>Nothing, but if the request is successful, the callback method is "
											+ "fired and receives the content as parameter.</li></ul>"
											+ "<h4>Example</h4><blockquote><pre>UWA.Json.request('http://example.org/api.php', {"
											+ "  onComplete: myModule.parse"
											+ "});</pre></blockquote><p>Will request url “<a target=\"_top\" "
											+ "href=\"http://example.org/api.php?callback=\">http://example.org/api.php?callback=</a>&lt;random&gt;” "
											+ "and expect has response “&lt;random&gt;(“Your data Json or Not”)”</p><blockquote>"
											+ "<pre>myModule.parse = function (response) {\n"
											+ "  // your parsing code"
											+ "}</pre></blockquote></div>"
								},
								{
									"name" : "cleanRequest",
									"type" : "METHOD",
									"code" : "cleanRequest(jsonpId)",
									"shortDescription" : "(jsonpId)",
									"fqn" : "UWA.Json",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">cleanRequest: function (</td><td nowrap=\"\">jsonpId</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Remove JSONP tag script and callback function.</p><h4>Parameters</h4><ul><li>String script: the URL of the data source.</li><li>Object id: the id of request callback.</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
								},
								{
									"name" : "getOfflineCache",
									"type" : "METHOD",
									"code" : "getOfflineCache()",
									"fqn" : "UWA.Json",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>getOfflineCache: function ()</td></tr></tbody></table></blockquote><p>Get UWA.Data.Storage instance for UWA.Json.request cache.</p><h4>Returns</h4><ul><li>Object: UWA.Data.Storage Instance for JSONP Offline Cache.</li></ul></div>"
								},
								{
									"name" : "storeInCache",
									"type" : "METHOD",
									"code" : "storeInCache(url, callbackData)",
									"shortDescription" : "(url, callbackData)",
									"fqn" : "UWA.Json",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">storeInCache: function (</td><td nowrap=\"\">url,</td></tr><tr><td></td><td nowrap=\"\">callbackData</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Store into UWA.Data.Storage instance for UWA.Json.request cache the request result</p><h4>Parameters</h4><ul><li>String url: the URL of the data source.</li><li>Object callbackData: the data of request callback</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul></div>"
								},
								{
									"name" : "getFromCache",
									"type" : "METHOD",
									"code" : "getFromCache(url)",
									"shortDescription" : "(url) : Object",
									"fqn" : "UWA.Json",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">getFromCache: function (</td><td nowrap=\"\">url</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Get from UWA.Data.Storage instance for UWA.Json.request the cached the request result</p><h4>Parameters</h4><ul><li>String url: the URL of the data source.</li></ul><h4>Returns</h4><ul><li>Object: Data from UWA.Data.Storage Instance for JSONP Offline Cache.</li></ul></div>"
								} ]
					},
					{
						"name" : "Utils",
						"type" : "CLASS",
						"code" : "Utils",
						"fqn" : "UWA",
						"subTokenList" : [
								{

									"name" : "buildUrl",
									"type" : "METHOD",
									"code" : "buildUrl(moduleUrl, linkHref)",
									"shortDescription" : "(moduleUrl, linkHref): String",
									"fqn" : "UWA.Utils",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">buildUrl: function (</td><td nowrap=\"\">moduleUrl,</td></tr><tr><td></td><td nowrap=\"\">linkHref</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Build an url from linkHref param in same context than moduleUrl param.</p><h4>Parameters</h4><ul><li>String moduleUrl: current module Url.</li><li>String linkHref: another url or simple file path (e.g.&nbsp; “/index.html”).</li></ul><h4>Returns</h4><ul><li>String: a new url related to moduleUrl url absolute value.</li></ul><h4>Example</h4><blockquote><pre>UWA.Utils.buildUrl(\"http://example.org/mywidget.html\", \"/index.html\");</pre></blockquote><p>will return “<a target=\"_top\" href=\"http://example.org/index.html\">http://example.org/index.html</a>”</p></div>"
								},
								{

									"name" : "parseUrl",
									"type" : "METHOD",
									"code" : "parseUrl(sourceUri)",
									"shortDescription" : "(sourceUri): Object",
									"fqn" : "UWA.Utils",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">parseUrl: function (</td><td nowrap=\"\">sourceUri</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Parse an Url to extract uri parts (protocol, domain, ...).&nbsp; The URL should include the protocol (<a target=\"_top\"href=\"http://\">http://</a>).</p><h4>Parameters</h4><ul><li>String sourceUri: an valide url (e.g.&nbsp; “<a target=\"_top\" href=\"http://netvibes.com\">http://netvibes.com</a>”).</li></ul><h4>Returns</h4><ul><li>Object: with following properties: source, protocol, authority, domain, port, path, directoryPath, fileName, query, anchor.</li></ul><h4>Example</h4><blockquote><pre>UWA.Utils.parseUrl(\"http://me@example.com:80\");</pre></blockquote></div>"
								},
								{

									"name" : "setCss",
									"type" : "METHOD",
									"code" : "setCss(id, content, namespace)",
									"shortDescription" : "(id, content, namespace)",
									"fqn" : "UWA.Utils",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">setCss: function (</td><td nowrap=\"\">id,</td></tr><tr><td></td><td nowrap=\"\">content,</td></tr><tr><td></td><td nowrap=\"\">namespace</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Add a &lt;styles&gt; tag to current document with specific id.</p><h4>Parameters</h4><ul><li>String id: style tag id attribute.</li><li>String content: style tag CSS content (e.g.&nbsp; “a.myClass { color: red; }”).</li><li>String namespace: optional, to avoid conflict you can add a preffix to id.</li></ul><h4>Returns</h4><ul><li>Nothing.</li></ul><h4>Example</h4><blockquote><pre>UWA.Utils.setCss(\"myStyle\", \"a.myClass { color: red; }\", \"1234\");</pre></blockquote></div>"
								},
								{

									"name" : "isArray",
									"type" : "METHOD",
									"code" : "isArray(obj)",
									"shortDescription" : "(obj): Boolean",
									"fqn" : "UWA.Utils",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">isArray: function (</td><td nowrap=\"\">obj</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Test if array param is an array or not.</p><h4>Parameters</h4><ul><li>String array: variable need to be check has array or not.</li></ul><h4>Returns</h4><ul><li>Boolean: true if array param is an array else false.</li></ul><h4>Example</h4><blockquote><pre>var myArray = [];\nvar isValidArray = UWA.Utils.isArray(myArray);</pre></blockquote></div>"
								},
								{

									"name" : "splat",
									"type" : "METHOD",
									"code" : "splat(array)",
									"shortDescription" : "(array): Array",
									"fqn" : "UWA.Utils",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">splat: function (</td><td nowrap=\"\">array</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Converts the argument passed in to an array if it is defined and not already an array</p><h4>Parameters</h4><ul><li>String array: variable need to be cast has array.</li></ul><h4>Returns</h4><ul><li>Array: same array than param if array param is an array else an array with array param has value.</li></ul><h4>Example</h4><blockquote><pre>var myArray = \"\";\nvar isValidArray = UWA.Utils.splat(myArray);</pre></blockquote></div>"
								},
								{

									"name" : "toQueryString",
									"type" : "METHOD",
									"code" : "toQueryString(myObject, base)",
									"shortDescription" : "(myObject, base): String",
									"fqn" : "UWA.Utils",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">toQueryString: function (</td><td nowrap=\"\">myObject,</td></tr><tr><td></td><td nowrap=\"\">base</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Encode an Object to a url string.</p><h4>Parameters</h4><ul><li>Object myObject: object to encode.</li><li>Object base: sub object to encode.</li></ul><h4>Returns</h4><ul><li>String: encoded string.</li></ul></div>"
								},
								{

									"name" : "encodeURIComponent",
									"type" : "METHOD",
									"code" : "encodeURIComponent(str)",
									"shortDescription" : "(str): String",
									"fqn" : "UWA.Utils",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">encodeURIComponent: function (</td><td nowrap=\"\">str</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Simple encodeURIComponent overided to escape “.” to “&#37;2e” also.</p><h4>Parameters</h4><ul><li>String str: string to encode.</li></ul><h4>Returns</h4><ul><li>String: encoded string.</li></ul></div>"
								},
								{

									"name" : "crc32",
									"type" : "METHOD",
									"code" : "crc32(str)",
									"shortDescription" : "(str): String",
									"fqn" : "UWA.Utils",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">crc32: function (</td><td nowrap=\"\">str</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Simple checksum using crc32 algo.</p><h4>Parameters</h4><ul><li>String str: string to check.</li></ul><h4>Returns</h4><ul><li>String: checksum for string passed as argument.</li></ul><h4>Notes</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>experimental</td><td>internal or advanced use only.</td></tr></tbody></table></div>"
								} ]
					},
					{
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
								} ]
					},
					{
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
					},
					{
						"name" : "Array",
						"type" : "CLASS",
						"code" : "Array",
						"subTokenList" : [
								{

									"name" : "forEach",
									"type" : "METHOD",
									"code" : "forEach(fn, bind)",
									"shortDescription" : "(fn, bind)",
									"fqn" : "UWA.Array",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">forEach: function (</td><td nowrap=\"\">fn,</td></tr><tr><td></td><td nowrap=\"\">bind</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Executes a provided function once per array element.</p><h4>Notes</h4><p>Javascript 1.6 method</p><h4>See also</h4><p><a target=\"_top\" href=\"http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:forEach\">http://developer.mozilla.org<wbr></wbr>/en<wbr></wbr>/docs<wbr></wbr>/Core_JavaScript_1.5_Reference:Global_Objects:Array:forEach</a></p></div>"
								},
								{

									"name" : "filter",
									"type" : "METHOD",
									"code" : "filter(fn, bind)",
									"shortDescription" : "(fn, bind)",
									"fqn" : "UWA.Array",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">filter: function (</td><td nowrap=\"\">fn,</td></tr><tr><td></td><td nowrap=\"\">bind</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Creates a new array with all elements that pass the test implemented by the provided function.</p><h4>Notes</h4><p>Javascript 1.6 method</p><h4>See also</h4><p><a target=\"_top\" href=\"http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:filter\">http://developer.mozilla.org<wbr></wbr>/en<wbr></wbr>/docs<wbr></wbr>/Core_JavaScript_1.5_Reference:Global_Objects:Array:filter</a></p></div>"
								},
								{

									"name" : "map",
									"type" : "METHOD",
									"code" : "map(fn, bind)",
									"shortDescription" : "(fn, bind)",
									"fqn" : "UWA.Array",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">map: function (</td><td nowrap=\"\">fn,</td></tr><tr><td></td><td nowrap=\"\">bind</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Creates a new array with the results of calling a provided function on every element in this array.</p><h4>Notes</h4><p>Javascript 1.6 method</p><h4>See also</h4><p><a target=\"_top\" href=\"http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:map\">http://developer.mozilla.org<wbr></wbr>/en<wbr></wbr>/docs<wbr></wbr>/Core_JavaScript_1.5_Reference:Global_Objects:Array:map</a></p></div>"
								},
								{

									"name" : "every",
									"type" : "METHOD",
									"code" : "every(fn, bind)",
									"shortDescription" : "(fn, bind)",
									"fqn" : "UWA.Array",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">every: function (</td><td nowrap=\"\">fn,</td></tr><tr><td></td><td nowrap=\"\">bind</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Tests whether all elements in the array pass the test implemented by the provided function.</p><h4>Notes</h4><p>Javascript 1.6 method</p><h4>See also</h4><p><a target=\"_top\" href=\"http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:every\">http://developer.mozilla.org<wbr></wbr>/en<wbr></wbr>/docs<wbr></wbr>/Core_JavaScript_1.5_Reference:Global_Objects:Array:every</a></p></div>"
								},
								{

									"name" : "some",
									"type" : "METHOD",
									"code" : "some(fn, bind)",
									"shortDescription" : "(fn, bind)",
									"fqn" : "UWA.Array",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">some: function (</td><td nowrap=\"\">fn,</td></tr><tr><td></td><td nowrap=\"\">bind</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Tests whether some element in the array passes the test implemented by the provided function.</p><h4>Notes</h4><p>Javascript 1.6 method</p><h4>See also</h4><p><a target=\"_top\" href=\"http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:some\">http://developer.mozilla.org<wbr></wbr>/en<wbr></wbr>/docs<wbr></wbr>/Core_JavaScript_1.5_Reference:Global_Objects:Array:some</a></p></div>"
								},
								{

									"name" : "indexOf",
									"type" : "METHOD",
									"code" : "indexOf(item, from)",
									"shortDescription" : "(item, from)",
									"fqn" : "UWA.Array",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">indexOf: function (</td><td nowrap=\"\">item,</td></tr><tr><td></td><td nowrap=\"\">from</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Returns the first index at which a given element can be found in the array, or -1 if it is not present.</p><h4>Notes</h4><p>Javascript 1.6 method</p><h4>See also</h4><p><a target=\"_top\" href=\"http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Array:indexOf\">http://developer.mozilla.org<wbr></wbr>/en<wbr></wbr>/docs<wbr></wbr>/Core_JavaScript_1.5_Reference:Global_Objects:Array:indexOf</a></p></div>"
								},
								{

									"name" : "normalize",
									"type" : "METHOD",
									"code" : "normalize(sum)",
									"shortDescription" : "(sum)",
									"fqn" : "UWA.Array",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">normalize: function (</td><td nowrap=\"\">sum</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Not documented</p><h4>Notes</h4><ul><li>needed for compatibility with a third-party UWA implementation</li></ul></div>"
								},
								{

									"name" : "equals",
									"type" : "METHOD",
									"code" : "equals(compare)",
									"shortDescription" : "(compare): Boolean",
									"fqn" : "UWA.Array",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">equals: function (</td><td nowrap=\"\">compare</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Test wether the array equals to the one passed as parameter.</p><h4>Parameters</h4><ul><li>Object compare: Array need to compare.</li></ul><h4>Returns</h4><ul><li>Bool: true if Arrays have the sames values else false.</li></ul></div>"
								},
								{

									"name" : "detect",
									"type" : "METHOD",
									"code" : "detect(iterator)",
									"shortDescription" : "(iterator): Boolean",
									"fqn" : "UWA.Array",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">detect: function (</td><td nowrap=\"\">iterator</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Test wether a array value is valid using a function.</p><h4>Parameters</h4><ul><li>Function iterator: Test function with param value and index</li></ul><h4>Returns</h4><ul><li>Bool: true if Arrays is valid else false.</li></ul></div>"
								} ]
					},
					{
						"name" : "String",
						"type" : "CLASS",
						"code" : "String",
						"subTokenList" : [
								{

									"name" : "stripTags",
									"type" : "METHOD",
									"code" : "stripTags()",
									"shortDescription" : "(): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>stripTags: function ()</td></tr></tbody></table></blockquote><p>Strips a string of any HTML tags.</p><h4>Returns</h4><ul><li>String: Converted string.</li></ul></div>"
								},
								{

									"name" : "truncate",
									"type" : "METHOD",
									"code" : "truncate(length, truncation)",
									"shortDescription" : "(length, truncation): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">truncate: function (</td><td nowrap=\"\">length,</td></tr><tr><td></td><td nowrap=\"\">truncation</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Truncates a string to the given length and appends a suffix to it (indicating that it is only an excerpt).</p><h4>Parameters</h4><ul><li>Integer length: max length of the string.</li><li>Integer truncation: truncation String used for elipsis, “...” by default.</li></ul><h4>Returns</h4><ul><li>String: Truncate string.</li></ul></div>"
								},
								{

									"name" : "cut",
									"type" : "METHOD",
									"code" : "cut(length, truncation)",
									"shortDescription" : "(length, truncation): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">cut: function (</td><td nowrap=\"\">length,</td></tr><tr><td></td><td nowrap=\"\">truncation</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Cut a string to the given length and appends a suffix to it (indicating that it is only an excerpt).</p><h4>Parameters</h4><ul><li>Integer length: max length of the string.</li><li>Integer truncation: truncation String used for elipsis, “...” by default.</li></ul><h4>Returns</h4><ul><li>String: Cut string.</li></ul></div>"
								},
								{

									"name" : "escapeRegExp",
									"type" : "METHOD",
									"code" : "escapeRegExp()",
									"shortDescription" : "(): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>escapeRegExp: function ()</td></tr></tbody></table></blockquote><p>Returns string with escaped regular expression characters</p><h4>Returns</h4><ul><li>String: Escaped RegExp string.</li></ul></div>"
								},
								{

									"name" : "trim",
									"type" : "METHOD",
									"code" : "trim()",
									"shortDescription" : "(): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>trim: function ()</td></tr></tbody></table></blockquote><p>Trims the leading and trailing spaces off a string.</p><h4>Returns</h4><ul><li>String: Trimed string.</li></ul></div>"
								},
								{

									"name" : "isEmail",
									"type" : "METHOD",
									"code" : "isEmail()",
									"shortDescription" : "(): Boolean",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>isEmail: function ()</td></tr></tbody></table></blockquote><p>Validate if current String is a valid email adresse.</p><h4>Returns</h4><ul><li>Boolean: True if is a valid email adresse else false.</li></ul></div>"
								},
								{

									"name" : "format",
									"type" : "METHOD",
									"code" : "format()",
									"shortDescription" : "(): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>format: function ()</td></tr></tbody></table></blockquote><p>Replace {d+} into string by the respetive argument position.</p><h4>Returns</h4><ul><li>String: Current String Instance.</li></ul></div>"
								},
								{

									"name" : "parseRelativeTime",
									"type" : "METHOD",
									"code" : "parseRelativeTime(raw, offset)",
									"shortDescription" : "(raw, offset): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">parseRelativeTime: function (</td><td nowrap=\"\">raw,</td></tr><tr><td></td><td nowrap=\"\">offset</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Convert date string to a human readable elapsed time, like “2 days ago”.</p><h4>Parameters</h4><ul><li>String raw: TODO.</li><li>Integer offset: TODO.</li></ul><h4>Returns</h4><ul><li>String: Relative Time.</li></ul></div>"
								},
								{

									"name" : "contains",
									"type" : "METHOD",
									"code" : "contains(string, separator)",
									"shortDescription" : "(string, separator): Boolean",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">contains: function (</td><td nowrap=\"\">string,</td></tr><tr><td></td><td nowrap=\"\">separator</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Check if String contain value using a separator.</p><h4>Parameters</h4><ul><li>String string: TODO.</li><li>String separator: TODO.</li></ul><h4>Returns</h4><ul><li>Boolean: True is String match requested String else false.</li></ul></div>"
								},
								{

									"name" : "camelCase",
									"type" : "METHOD",
									"code" : "camelCase()",
									"shortDescription" : "(): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>camelCase: function ()</td></tr></tbody></table></blockquote><p>Convert current String with space and coma in CamelCase valid string.</p><h4>Returns</h4><ul><li>String: The converted string.</li></ul></div>"
								},
								{

									"name" : "capitalize",
									"type" : "METHOD",
									"code" : "capitalize()",
									"shortDescription" : "(): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>capitalize: function ()</td></tr></tbody></table></blockquote><p>Converts the first letter of each word in a string to uppercase.</p><h4>Returns</h4><ul><li>String: The converted string.</li></ul></div>"
								},
								{

									"name" : "unescapeHTML",
									"type" : "METHOD",
									"code" : "unescapeHTML()",
									"shortDescription" : "(): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>unescapeHTML: function ()</td></tr></tbody></table></blockquote><p>Unescape HTML on current String.</p><h4>Returns</h4><ul><li>String: The converted string.</li></ul></div>"
								},
								{

									"name" : "escapeHTML",
									"type" : "METHOD",
									"code" : "escapeHTML()",
									"shortDescription" : "(): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>escapeHTML: function ()</td></tr></tbody></table></blockquote><p>Escape HTML on current String.</p><h4>Returns</h4><ul><li>String: The converted string.</li></ul></div>"
								},
								{

									"name" : "test",
									"type" : "METHOD",
									"code" : "test(string, regexp)",
									"shortDescription" : "(string, regexp): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">test: function (</td><td nowrap=\"\">string,</td></tr><tr><td></td><td nowrap=\"\">regexp</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Apply regexp on current String.</p><h4>Parameters</h4><ul><li>String string: TODO.</li><li>String regexp: TODO.</li></ul><h4>Returns</h4><ul><li>String: The RegExp results.</li></ul></div>"
								},
								{

									"name" : "makeClickable",
									"type" : "METHOD",
									"code" : "makeClickable()",
									"shortDescription" : "(): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>makeClickable: function ()</td></tr></tbody></table></blockquote><p>Convert Url String has clickable link.</p><h4>Returns</h4><ul><li>String: The &lt;a&gt; HTML link with String value has href attribute.</li></ul></div>"
								},
								{

									"name" : "ucfirst",
									"type" : "METHOD",
									"code" : "ucfirst()",
									"shortDescription" : "(): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td>ucfirst: function ()</td></tr></tbody></table></blockquote><p>Convert first String letter has UpperCase.</p><h4>Returns</h4><ul><li>String: updated String</li></ul></div>"
								},
								{

									"name" : "highlight",
									"type" : "METHOD",
									"code" : "highlight(search)",
									"shortDescription" : "(search): String",
									"fqn" : "UWA.String",
									"fullDescription" : "<div><blockquote><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td nowrap=\"\">highlight: function (</td><td nowrap=\"\">search</td><td nowrap=\"\">)</td></tr></tbody></table></td></tr></tbody></table></blockquote><p>Add span Element around search text.</p><h4>Parameters</h4><ul><li>String search: string to highlight.</li></ul><h4>Returns</h4><ul><li>String: updated String</li></ul></div>"
								} ]
					} ]
		},
		{
			"name" : "UWA_SERVER",
			"type" : "VARIABLE",
			"code" : "UWA_SERVER",
			"shortDescription" : " : String",
			"fullDescription" : "<pre>String: Url to Exposition Server endpoint.<br><b>Notes</b> Internal or advanced use only.</pre>"
		},
		{
			"name" : "UWA_WIDGET",
			"type" : "VARIABLE",
			"code" : "UWA_WIDGET",
			"shortDescription" : " : String",
			"fullDescription" : "<div><p><b>String</b>: Url to Exposition Server Widget endpoint.</p><h4>Notes</h4><p>Internal or advanced use only.</p></div>"
		},
		{
			"name" : "UWA_JS",
			"type" : "VARIABLE",
			"code" : "UWA_JS",
			"shortDescription" : " : String",
			"fullDescription" : "<div><p><b>String</b>: Url to UWA JS Runetime Javascript files.</p><h4>Notes</h4><p>Internal or advanced use only.</p></div>"
		},
		{
			"name" : "UWA_CSS",
			"type" : "VARIABLE",
			"code" : "UWA_CSS",
			"shortDescription" : " : String",
			"fullDescription" : "<div><p><b>String</b>: Url to Exposition Server Widget CSS and Themes files.</p></div>"
		},
		{
			"name" : "UWA_PROXY",
			"type" : "VARIABLE",
			"code" : "UWA_PROXY",
			"shortDescription" : " : String",
			"fullDescription" : "<div><p><b>String</b>: Url to Exposition Server Widget proxy endpoint.</p><h4>Notes</h4><p>Internal or advanced use only.</p></div>"
		},
		{
			"name" : "UWA_STATIC",
			"type" : "VARIABLE",
			"code" : "UWA_STATIC",
			"shortDescription" : " : String",
			"fullDescription" : "<div><p><b>String</b>: Url to Exposition Server Widget images files.</p><h4>Notes</h4><p>Internal or advanced use only.</p></div>"
		},
		{
			"name" : "UWA_ECO",
			"type" : "VARIABLE",
			"code" : "UWA_ECO",
			"shortDescription" : " : String",
			"fullDescription" : "<div><p><b>String</b>: Url to Ecosytem to share widget.</p><h4>Notes</h4><p>Internal or advanced use only.</p></div>"
		},
		{
			"name" : "NV_HOST",
			"type" : "VARIABLE",
			"code" : "NV_HOST",
			"shortDescription" : " : String",
			"fullDescription" : "<div><p><b>String</b>: Url to Netvibes dashboard</p><h4>Notes</h4><p>Internal or advanced use only.</p></div>"
		},
		{
			"name" : "_",
			"type" : "FUNCTION",
			"code" : "_(s)",
			"shortDescription" : " : String",
			"fullDescription" : "<div><b>window._</b><br><p>Translation of text from English to another language</p><h4>Parameters</h4><ul><li>String s: The message to translate.</li></ul><h4>Example</h4><blockquote><pre>alert(_('Your Name'));</pre></blockquote></div>"
		}, {
			"name" : "widget",
			"type" : "VARIABLE",
			"varType" : "UWA.Widget"
		} ];
