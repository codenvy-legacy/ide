var gadgets=gadgets||{};
gadgets.log=function(A){gadgets.log.logAtLevel(gadgets.log.INFO,A)
};
gadgets.warn=function(A){gadgets.log.logAtLevel(gadgets.log.WARNING,A)
};
gadgets.error=function(A){gadgets.log.logAtLevel(gadgets.log.ERROR,A)
};
gadgets.setLogLevel=function(A){gadgets.log.logLevelThreshold_=A
};
gadgets.log.logAtLevel=function(C,B){if(C<gadgets.log.logLevelThreshold_||!window.console){return 
}var A=window.console.log;
if(C==gadgets.log.WARNING&&window.console.warn){A=window.console.warn
}else{if(C==gadgets.log.ERROR&&window.console.error){A=window.console.error
}}A(B)
};
gadgets.log.INFO=1;
gadgets.log.WARNING=2;
gadgets.log.ERROR=3;
gadgets.log.NONE=4;
gadgets.log.logLevelThreshold_=gadgets.log.INFO;;
var gadgets=gadgets||{};
gadgets.util=function(){function G(){var L;
var K=document.location.href;
var I=K.indexOf("?");
var J=K.indexOf("#");
if(J===-1){L=K.substr(I+1)
}else{L=[K.substr(I+1,J-I-1),"&",K.substr(J+1)].join("")
}return L.split("&")
}var E=null;
var D={};
var C={};
var F=[];
var A={0:false,10:true,13:true,34:true,39:true,60:true,62:true,92:true,8232:true,8233:true};
function B(I,J){return String.fromCharCode(J)
}function H(I){D=I["core.util"]||{}
}if(gadgets.config){gadgets.config.register("core.util",null,H)
}return{getUrlParameters:function(){if(E!==null){return E
}E={};
var L=G();
var O=window.decodeURIComponent?decodeURIComponent:unescape;
for(var J=0,I=L.length;
J<I;
++J){var N=L[J].indexOf("=");
if(N===-1){continue
}var M=L[J].substring(0,N);
var K=L[J].substring(N+1);
K=K.replace(/\+/g," ");
E[M]=O(K)
}return E
},makeClosure:function(L,N,M){var K=[];
for(var J=2,I=arguments.length;
J<I;
++J){K.push(arguments[J])
}return function(){var O=K.slice();
for(var Q=0,P=arguments.length;
Q<P;
++Q){O.push(arguments[Q])
}return N.apply(L,O)
}
},makeEnum:function(J){var L={};
for(var K=0,I;
(I=J[K]);
++K){L[I]=I
}return L
},getFeatureParameters:function(I){return typeof D[I]==="undefined"?null:D[I]
},hasFeature:function(I){return typeof D[I]!=="undefined"
},getServices:function(){return C
},registerOnLoadHandler:function(I){F.push(I)
},runOnLoadHandlers:function(){for(var J=0,I=F.length;
J<I;
++J){F[J]()
}},escape:function(I,M){if(!I){return I
}else{if(typeof I==="string"){return gadgets.util.escapeString(I)
}else{if(typeof I==="array"){for(var L=0,J=I.length;
L<J;
++L){I[L]=gadgets.util.escape(I[L])
}}else{if(typeof I==="object"&&M){var K={};
for(var N in I){if(I.hasOwnProperty(N)){K[gadgets.util.escapeString(N)]=gadgets.util.escape(I[N],true)
}}return K
}}}}return I
},escapeString:function(M){var J=[],L,N;
for(var K=0,I=M.length;
K<I;
++K){L=M.charCodeAt(K);
N=A[L];
if(N===true){J.push("&#",L,";")
}else{if(N!==false){J.push(M.charAt(K))
}}}return J.join("")
},unescapeString:function(I){return I.replace(/&#([0-9]+);/g,B)
}}
}();
gadgets.util.getUrlParameters();;
var gadgets=gadgets||{};
if(window.JSON){gadgets.json={parse:function(B){try{return window.JSON.parse(B)
}catch(A){return false
}},stringify:function(B){try{return window.JSON.stringify(B)
}catch(A){return null
}}}
}else{gadgets.json=function(){function f(n){return n<10?"0"+n:n
}Date.prototype.toJSON=function(){return[this.getUTCFullYear(),"-",f(this.getUTCMonth()+1),"-",f(this.getUTCDate()),"T",f(this.getUTCHours()),":",f(this.getUTCMinutes()),":",f(this.getUTCSeconds()),"Z"].join("")
};
var m={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"};
function stringify(value){var a,i,k,l,r=/["\\\x00-\x1f\x7f-\x9f]/g,v;
switch(typeof value){case"string":return r.test(value)?'"'+value.replace(r,function(a){var c=m[a];
if(c){return c
}c=a.charCodeAt();
return"\\u00"+Math.floor(c/16).toString(16)+(c%16).toString(16)
})+'"':'"'+value+'"';
case"number":return isFinite(value)?String(value):"null";
case"boolean":case"null":return String(value);
case"object":if(!value){return"null"
}a=[];
if(typeof value.length==="number"&&!value.propertyIsEnumerable("length")){l=value.length;
for(i=0;
i<l;
i+=1){a.push(stringify(value[i])||"null")
}return"["+a.join(",")+"]"
}for(k in value){if(value.hasOwnProperty(k)){if(typeof k==="string"){v=stringify(value[k]);
if(v){a.push(stringify(k)+":"+v)
}}}}return"{"+a.join(",")+"}"
}}return{stringify:stringify,parse:function(text){if(/^[\],:{}\s]*$/.test(text.replace(/\\["\\\/b-u]/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""))){return eval("("+text+")")
}return false
}}
}()
};;
var gadgets=gadgets||{};
gadgets.rpctx=gadgets.rpctx||{};
gadgets.rpctx.wpm=function(){var A;
return{getCode:function(){return"wpm"
},isParentVerifiable:function(){return true
},init:function(B,C){A=C;
window.addEventListener("message",function(D){B(gadgets.json.parse(D.data))
},false);
A("..",true);
return true
},setup:function(C,B){if(C===".."){gadgets.rpc.call(C,gadgets.rpc.ACK)
}return true
},call:function(B,F,D){var C=B===".."?parent:window.frames[B];
var E=gadgets.rpc.getRelayUrl(B);
if(E){C.postMessage(gadgets.json.stringify(D),E)
}else{gadgets.error("No relay set, cannot send cross-domain message")
}return true
}}
}();;
var gadgets=gadgets||{};
gadgets.rpctx=gadgets.rpctx||{};
gadgets.rpctx.frameElement=function(){var E="__g2c_rpc";
var B="__c2g_rpc";
var D;
var C;
function A(G,K,J){try{if(K!==".."){var F=window.frameElement;
if(typeof F[E]==="function"){if(typeof F[E][B]!=="function"){F[E][B]=function(L){D(gadgets.json.parse(L))
}
}F[E](gadgets.json.stringify(J));
return 
}}else{var I=document.getElementById(G);
if(typeof I[E]==="function"&&typeof I[E][B]==="function"){I[E][B](gadgets.json.stringify(J));
return 
}}}catch(H){}return true
}return{getCode:function(){return"fe"
},isParentVerifiable:function(){return false
},init:function(F,G){D=F;
C=G;
return true
},setup:function(J,F){if(J!==".."){try{var I=document.getElementById(J);
I[E]=function(K){D(gadgets.json.parse(K))
}
}catch(H){return false
}}if(J===".."){C("..",true);
var G=function(){window.setTimeout(function(){gadgets.rpc.call(J,gadgets.rpc.ACK)
},500)
};
gadgets.util.registerOnLoadHandler(G)
}return true
},call:function(F,H,G){A(F,H,G)
}}
}();;
var gadgets=gadgets||{};
gadgets.rpctx=gadgets.rpctx||{};
gadgets.rpctx.nix=function(){var C="GRPC____NIXVBS_wrapper";
var D="GRPC____NIXVBS_get_wrapper";
var F="GRPC____NIXVBS_handle_message";
var B="GRPC____NIXVBS_create_channel";
var A=10;
var J=500;
var I={};
var H;
var G=0;
function E(){var L=I[".."];
if(L){return 
}if(++G>A){gadgets.warn("Nix transport setup failed, falling back...");
H("..",false);
return 
}if(!L&&window.opener&&"GetAuthToken" in window.opener){L=window.opener;
if(L.GetAuthToken()==gadgets.rpc.getAuthToken("..")){var K=gadgets.rpc.getAuthToken("..");
L.CreateChannel(window[D]("..",K),K);
I[".."]=L;
window.opener=null;
H("..",true);
return 
}}window.setTimeout(function(){E()
},J)
}return{getCode:function(){return"nix"
},isParentVerifiable:function(){return false
},init:function(L,M){H=M;
if(typeof window[D]!=="unknown"){window[F]=function(O){window.setTimeout(function(){L(gadgets.json.parse(O))
},0)
};
window[B]=function(O,Q,P){if(gadgets.rpc.getAuthToken(O)===P){I[O]=Q;
H(O,true)
}};
var K="Class "+C+"\n Private m_Intended\nPrivate m_Auth\nPublic Sub SetIntendedName(name)\n If isEmpty(m_Intended) Then\nm_Intended = name\nEnd If\nEnd Sub\nPublic Sub SetAuth(auth)\n If isEmpty(m_Auth) Then\nm_Auth = auth\nEnd If\nEnd Sub\nPublic Sub SendMessage(data)\n "+F+"(data)\nEnd Sub\nPublic Function GetAuthToken()\n GetAuthToken = m_Auth\nEnd Function\nPublic Sub CreateChannel(channel, auth)\n Call "+B+"(m_Intended, channel, auth)\nEnd Sub\nEnd Class\nFunction "+D+"(name, auth)\nDim wrap\nSet wrap = New "+C+"\nwrap.SetIntendedName name\nwrap.SetAuth auth\nSet "+D+" = wrap\nEnd Function";
try{window.execScript(K,"vbscript")
}catch(N){return false
}}return true
},setup:function(O,K){if(O===".."){E();
return true
}try{var M=document.getElementById(O);
var N=window[D](O,K);
M.contentWindow.opener=N
}catch(L){return false
}return true
},call:function(K,N,M){try{if(I[K]){I[K].SendMessage(gadgets.json.stringify(M))
}}catch(L){return false
}return true
}}
}();;
var gadgets=gadgets||{};
gadgets.rpctx=gadgets.rpctx||{};
gadgets.rpctx.rmr=function(){var G=500;
var E=10;
var H={};
var B;
var I;
function K(O,M,N){var P=function(){document.body.appendChild(O);
O.src="about:blank";
O.src=M+"#"+N
};
if(document.body){P()
}else{gadgets.util.registerOnLoadHandler(function(){P()
})
}}function C(O){if(typeof H[O]==="object"){return 
}var P=document.createElement("iframe");
var M=P.style;
M.position="absolute";
M.top="0px";
M.border="0";
M.opacity="0";
M.width="10px";
M.height="1px";
P.id="rmrtransport-"+O;
P.name=P.id;
var N=gadgets.rpc.getOrigin(gadgets.rpc.getRelayUrl(O))+"/robots.txt";
H[O]={frame:P,receiveWindow:null,relayUri:N,searchCounter:0,width:10,waiting:true,queue:[],sendId:0,recvId:0};
if(O!==".."){K(P,N,A(O))
}D(O)
}function D(N){var O=null;
H[N].searchCounter++;
if(N===".."){O=window.parent.frames["rmrtransport-"+window.name]
}else{O=window.frames[N].frames["rmrtransport-.."]
}var M=false;
if(O){M=F(N,O)
}if(!M){if(H[N].searchCounter>E){return 
}setTimeout(function(){D(N)
},G)
}}function J(N,P,T,S){var O=null;
if(T!==".."){O=H[".."]
}else{O=H[N]
}if(O){if(P!==gadgets.rpc.ACK){O.queue.push(S)
}if(O.waiting||(O.queue.length===0&&!(P===gadgets.rpc.ACK&&S&&S.ackAlone===true))){return true
}if(O.queue.length>0){O.waiting=true
}var M=O.relayUri+"#"+A(N);
try{O.frame.contentWindow.location=M;
var Q=O.width==10?20:10;
O.frame.style.width=Q+"px";
O.width=Q
}catch(R){return false
}}return true
}function A(N){var O=H[N];
var M={id:O.sendId};
if(O){M.d=Array.prototype.slice.call(O.queue,0);
M.d.push({s:gadgets.rpc.ACK,id:O.recvId})
}return gadgets.json.stringify(M)
}function L(X){var U=H[X];
var Q=U.receiveWindow.location.hash.substring(1);
var Y=gadgets.json.parse(decodeURIComponent(Q))||{};
var N=Y.d||[];
var O=false;
var T=false;
var V=0;
var M=(U.recvId-Y.id);
for(var P=0;
P<N.length;
++P){var S=N[P];
if(S.s===gadgets.rpc.ACK){I(X,true);
if(U.waiting){T=true
}U.waiting=false;
var R=Math.max(0,S.id-U.sendId);
U.queue.splice(0,R);
U.sendId=Math.max(U.sendId,S.id||0);
continue
}O=true;
if(++V<=M){continue
}++U.recvId;
B(S)
}if(O||(T&&U.queue.length>0)){var W=(X==="..")?window.name:"..";
J(X,gadgets.rpc.ACK,W,{ackAlone:O})
}}function F(P,R){var O=H[P];
try{var N=false;
N="document" in R;
if(!N){return false
}N=typeof R.document=="object";
if(!N){return false
}var Q=R.location.href;
if(Q==="about:blank"){return false
}}catch(M){return false
}O.receiveWindow=R;
R.onresize=function(){L(P)
};
if(P===".."){K(O.frame,O.relayUri,A(P))
}L(P);
return true
}return{getCode:function(){return"rmr"
},isParentVerifiable:function(){return true
},init:function(M,N){B=M;
I=N;
return true
},setup:function(O,M){try{C(O)
}catch(N){gadgets.warn("Caught exception setting up RMR: "+N);
return false
}return true
},call:function(M,O,N){return J(M,N.s,O,N)
}}
}();;
var gadgets=gadgets||{};
gadgets.rpctx=gadgets.rpctx||{};
gadgets.rpctx.ifpc=function(){var E=[];
var D=0;
var C;
function B(H){var F=[];
for(var I=0,G=H.length;
I<G;
++I){F.push(encodeURIComponent(gadgets.json.stringify(H[I])))
}return F.join("&")
}function A(I){var G;
for(var F=E.length-1;
F>=0;
--F){var J=E[F];
try{if(J&&(J.recyclable||J.readyState==="complete")){J.parentNode.removeChild(J);
if(window.ActiveXObject){E[F]=J=null;
E.splice(F,1)
}else{J.recyclable=false;
G=J;
break
}}}catch(H){}}if(!G){G=document.createElement("iframe");
G.style.border=G.style.width=G.style.height="0px";
G.style.visibility="hidden";
G.style.position="absolute";
G.onload=function(){this.recyclable=true
};
E.push(G)
}G.src=I;
setTimeout(function(){document.body.appendChild(G)
},0)
}return{getCode:function(){return"ifpc"
},isParentVerifiable:function(){return true
},init:function(F,G){C=G;
C("..",true);
return true
},setup:function(G,F){C(G,true);
return true
},call:function(F,K,I){var J=gadgets.rpc.getRelayUrl(F);
++D;
if(!J){gadgets.warn("No relay file assigned for IFPC");
return 
}var H=null;
if(I.l){var G=I.a;
H=[J,"#",B([K,D,1,0,B([K,I.s,"","",K].concat(G))])].join("")
}else{H=[J,"#",F,"&",K,"@",D,"&1&0&",encodeURIComponent(gadgets.json.stringify(I))].join("")
}A(H);
return true
}}
}();;
var gadgets=gadgets||{};
gadgets.rpc=function(){var O="__cb";
var M="";
var F="__ack";
var L=500;
var G=10;
var B={};
var C={};
var S={};
var H={};
var J=0;
var b={};
var R={};
var D={};
var Z={};
var I={};
var Q={};
var T=(window.top!==window.self);
var a=gadgets.rpctx.Ifpc;
if(gadgets.util){Z=gadgets.util.getUrlParameters()
}H[".."]=Z.rpctoken||Z.ifpctok||0;
var U=(Z.rpc_earlyq==="1");
function A(){return typeof window.postMessage==="function"?gadgets.rpctx.wpm:window.ActiveXObject?gadgets.rpctx.nix:navigator.userAgent.indexOf("WebKit")>0?gadgets.rpctx.rmr:navigator.product==="Gecko"?gadgets.rpctx.frameElement:gadgets.rpctx.ifpc
}function W(h,f){var d=X;
if(!f){d=a
}I[h]=d;
var c=Q[h]||[];
for(var e=0;
e<c.length;
++e){var g=c[e];
g.t=gadgets.rpc.getAuthToken(h);
d.call(h,g.f,g)
}Q[h]=[]
}function P(d){if(d&&typeof d.s==="string"&&typeof d.f==="string"&&d.a instanceof Array){if(H[d.f]){if(H[d.f]!==d.t){throw new Error("Invalid auth token. "+H[d.f]+" vs "+d.t)
}}if(d.s===F){window.setTimeout(function(){W(d.f,true)
},0);
return 
}if(d.c){d.callback=function(e){gadgets.rpc.call(d.f,O,null,d.c,e)
}
}var c=(B[d.s]||B[M]).apply(d,d.a);
if(d.c&&typeof c!=="undefined"){gadgets.rpc.call(d.f,O,null,d.c,c)
}}}function Y(e){if(!e){return""
}e=e.toLowerCase();
if(e.indexOf("//")==0){e=window.location.protocol+":"+e
}if(e.indexOf("http://")!=0&&e.indexOf("https://")!=0){e=window.location.protocol+"://"+e
}var f=e.substring(e.indexOf("://")+3);
var c=f.indexOf("/");
if(c!=-1){f=f.substring(0,c)
}var h=e.substring(0,e.indexOf("://"));
var g="";
var i=f.indexOf(":");
if(i!=-1){var d=f.substring(i+1);
f=f.substring(0,i);
if((h==="http"&&d!=="80")||(h==="https"&&d!=="443")){g=":"+d
}}return h+"://"+f+g
}var X=A();
B[M]=function(){gadgets.warn("Unknown RPC service: "+this.s)
};
B[O]=function(d,c){var e=b[d];
if(e){delete b[d];
e(c)
}};
function K(e,c){if(R[e]===true){return 
}if(typeof R[e]==="undefined"){R[e]=0
}var d=document.getElementById(e);
if(e===".."||d!=null){if(X.setup(e,c)===true){R[e]=true;
return 
}}if(R[e]!==true&&R[e]++<G){window.setTimeout(function(){K(e,c)
},L)
}else{X=a;
R[e]=true
}}function E(d,h){if(typeof D[d]==="undefined"){D[d]=false;
var g=gadgets.rpc.getRelayUrl(d);
if(Y(g)!==Y(window.location.href)){return false
}var f=null;
if(d===".."){f=parent
}else{f=frames[d]
}try{D[d]=f.gadgets.rpc.receiveSameDomain
}catch(c){gadgets.error("Same domain call failed: parent= incorrectly set.")
}}if(typeof D[d]==="function"){D[d](h);
return true
}return false
}if(T&&gadgets.config){function V(d){var f=d?d.rpc:{};
var c=f.parentRelayUrl;
if(c.substring(0,7)!=="http://"&&c.substring(0,8)!=="https://"&&c.substring(0,2)!=="//"){if(Z.parent!==""){c=Y(Z.parent)+c
}}C[".."]=c;
var e=!!f.useLegacyProtocol;
S[".."]=e;
if(e){X=gadgets.rpctx.Ifpc;
X.init(P,W)
}if(X.setup("..")===false){X=a
}}var N={parentRelayUrl:gadgets.config.NonEmptyStringValidator};
gadgets.config.register("rpc",N,V)
}return{register:function(d,c){if(d===O||d===F){throw new Error("Cannot overwrite callback/ack service")
}if(d===M){throw new Error("Cannot overwrite default service: use registerDefault")
}B[d]=c
},unregister:function(c){if(c===O||c===F){throw new Error("Cannot delete callback/ack service")
}if(c===M){throw new Error("Cannot delete default service: use unregisterDefault")
}delete B[c]
},registerDefault:function(c){B[M]=c
},unregisterDefault:function(){delete B[M]
},forceParentVerifiable:function(){if(!X.isParentVerifiable()){X=gadgets.rpctx.Ifpc
}},call:function(c,d,i,g){c=c||"..";
var h="..";
if(c===".."){h=window.name
}++J;
if(i){b[J]=i
}var f={s:d,f:h,c:i?J:0,a:Array.prototype.slice.call(arguments,3),t:H[c],l:S[c]};
if(E(c,f)){return 
}var e=U?I[c]:X;
if(!e){if(!Q[c]){Q[c]=[f]
}else{Q[c].push(f)
}return 
}if(S[c]){e=gadgets.rpctx.Ifpc
}if(e.call(c,h,f)===false){X=a;
X.call(c,h,f)
}},getRelayUrl:function(d){var c=C[d];
if(c&&c.indexOf("//")==0){c=document.location.protocol+c
}return c
},setRelayUrl:function(d,c,e){C[d]=c;
S[d]=!!e
},setAuthToken:function(c,d){d=d||"";
H[c]=String(d);
K(c,d)
},getAuthToken:function(c){return H[c]
},getRelayChannel:function(){return X.getCode()
},receive:function(c){if(c.length>4){P(gadgets.json.parse(decodeURIComponent(c[c.length-1])))
}},receiveSameDomain:function(c){c.a=Array.prototype.slice.call(c.a);
window.setTimeout(function(){P(c)
},0)
},getOrigin:Y,init:function(){if(X.init(P,W)===false){X=a
}},ACK:F}
}();
gadgets.rpc.init();;
