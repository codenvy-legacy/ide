(function(){var m=document,g=(/in\.js/),N=(/api_key/),B=(/\/\*((?:.|[\s])*?)\*\//m),x=(/\r/g),d=(/[\s]/g),c=(/^[\s]*(.*?)[\s]*:[\s]*(.*)[\s]*$/),u=(/^[\s]+|[\s]+$/g),f="\n",y=",",j="",A="@",I="&",k="extensions",l="api_key",L="on",r="onDOMReady",O="onOnce",M="script",D="https://www.linkedin.com/uas/js/userspace?v=0.0.1108-RC2.4983",z="http://platform.linkedin.com/js/anonymousFramework?v=0.0.1108-RC2.4983",v=m.getElementsByTagName("head")[0],q=m.getElementsByTagName(M),a=[],E={},P,h,J,o,C,w,b;
if(window.IN&&IN.ENV&&IN.ENV.js){return
}window.IN=window.IN||{};
IN.ENV={};
IN.ENV.js={};
IN.ENV.js.extensions={};
IN.ENV.evtQueue=[];
P=IN.ENV.evtQueue;
IN.Event={on:function(){P.push({type:L,args:arguments})
},onDOMReady:function(){P.push({type:r,args:arguments})
},onOnce:function(){P.push({type:O,args:arguments})
}};
IN.$extensions=function(S){var V,i,R,U,T=IN.ENV.js.extensions;
V=S.split(y);
for(var Q=0,e=V.length;
Q<e;
Q++){i=H(V[Q],A,2);
R=i[0].replace(u,j);
U=i[1];
if(!T[R]){T[R]={src:(U)?U.replace(u,j):j,loaded:false}
}}};
function H(S,Q,e){if(!e){return S.split(Q)
}var T=S.split(Q);
if(T.length<e){return T
}var R=T.splice(0,e-1);
var i=T.join(Q);
R.push(i);
return R
}h="";
for(G=0,n=q.length;
G<n;
G++){var b=q[G];
if(!b.src.match(g)){continue
}try{h=b.innerHTML.replace(u,j)
}catch(t){try{h=b.text.replace(u,j)
}catch(s){}}if(h&&h.match(N)){break
}}h=h.replace(B,"$1");
h=h.replace(u,j);
h=h.replace(x,j);
for(var G=0,F=h.split(f),n=F.length;
G<n;
G++){var p=F[G];
if(!p||p.replace(d,j).length<=0){continue
}try{J=p.match(c);
o=J[1].replace(u,j);
C=J[2].replace(u,j)
}catch(K){throw"Script tag contents must be key/value pairs separated by a colon. Source: "+K
}if(o==k){IN.$extensions(C);
C=null
}if(o==l){C=C.replace(d,j)
}if(C!==null){IN.ENV.js[o]=C;
a[a.length]=encodeURIComponent(o)+"="+encodeURIComponent(C)
}}w=m.createElement(M);
w.src=(IN.ENV.js.api_key)?D+I+a.join(I):z;
v.appendChild(w)
})();
