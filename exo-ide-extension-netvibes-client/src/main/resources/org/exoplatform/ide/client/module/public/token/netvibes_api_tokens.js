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
			"fqn" : "UWA.Widget"
		} ];
