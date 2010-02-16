OpenLayers.Renderer.VML=OpenLayers.Class(OpenLayers.Renderer.Elements,{xmlns:"urn:schemas-microsoft-com:vml",symbolCache:{},offset:null,initialize:function(containerID){if(!this.supported()){return;}
if(!document.namespaces.olv){document.namespaces.add("olv",this.xmlns);var style=document.createStyleSheet();var shapes=['shape','rect','oval','fill','stroke','imagedata','group','textbox'];for(var i=0,len=shapes.length;i<len;i++){style.addRule('olv\\:'+shapes[i],"behavior: url(#default#VML); "+"position: absolute; display: inline-block;");}}
OpenLayers.Renderer.Elements.prototype.initialize.apply(this,arguments);this.offset={x:0,y:0};},destroy:function(){OpenLayers.Renderer.Elements.prototype.destroy.apply(this,arguments);},supported:function(){return!!(document.namespaces);},setExtent:function(extent,resolutionChanged){OpenLayers.Renderer.Elements.prototype.setExtent.apply(this,arguments);var resolution=this.getResolution();var left=extent.left/resolution;var top=extent.top/resolution-this.size.h;if(resolutionChanged){this.offset={x:left,y:top};left=0;top=0;}else{left=left-this.offset.x;top=top-this.offset.y;}
var org=left+" "+top;this.root.coordorigin=org;var roots=[this.root,this.vectorRoot,this.textRoot];var root;for(var i=0,len=roots.length;i<len;++i){root=roots[i];var size=this.size.w+" "+this.size.h;root.coordsize=size;}
this.root.style.flip="y";return true;},setSize:function(size){OpenLayers.Renderer.prototype.setSize.apply(this,arguments);var roots=[this.rendererRoot,this.root,this.vectorRoot,this.textRoot];var w=this.size.w+"px";var h=this.size.h+"px";var root;for(var i=0,len=roots.length;i<len;++i){root=roots[i];root.style.width=w;root.style.height=h;}},getNodeType:function(geometry,style){var nodeType=null;switch(geometry.CLASS_NAME){case"OpenLayers.Geometry.Point":if(style.externalGraphic){nodeType="olv:rect";}else if(this.isComplexSymbol(style.graphicName)){nodeType="olv:shape";}else{nodeType="olv:oval";}
break;case"OpenLayers.Geometry.Rectangle":nodeType="olv:rect";break;case"OpenLayers.Geometry.LineString":case"OpenLayers.Geometry.LinearRing":case"OpenLayers.Geometry.Polygon":case"OpenLayers.Geometry.Curve":case"OpenLayers.Geometry.Surface":nodeType="olv:shape";break;default:break;}
return nodeType;},setStyle:function(node,style,options,geometry){style=style||node._style;options=options||node._options;var widthFactor=1;if(node._geometryClass=="OpenLayers.Geometry.Point"){if(style.externalGraphic){if(style.graphicTitle){node.title=style.graphicTitle;}
var width=style.graphicWidth||style.graphicHeight;var height=style.graphicHeight||style.graphicWidth;width=width?width:style.pointRadius*2;height=height?height:style.pointRadius*2;var resolution=this.getResolution();var xOffset=(style.graphicXOffset!=undefined)?style.graphicXOffset:-(0.5*width);var yOffset=(style.graphicYOffset!=undefined)?style.graphicYOffset:-(0.5*height);node.style.left=((geometry.x/resolution-this.offset.x)+xOffset).toFixed();node.style.top=((geometry.y/resolution-this.offset.y)-(yOffset+height)).toFixed();node.style.width=width+"px";node.style.height=height+"px";node.style.flip="y";style.fillColor="none";options.isStroked=false;}else if(this.isComplexSymbol(style.graphicName)){var cache=this.importSymbol(style.graphicName);node.path=cache.path;node.coordorigin=cache.left+","+cache.bottom;var size=cache.size;node.coordsize=size+","+size;this.drawCircle(node,geometry,style.pointRadius);node.style.flip="y";}else{this.drawCircle(node,geometry,style.pointRadius);}}
if(options.isFilled){node.fillcolor=style.fillColor;}else{node.filled="false";}
var fills=node.getElementsByTagName("fill");var fill=(fills.length==0)?null:fills[0];if(!options.isFilled){if(fill){node.removeChild(fill);}}else{if(!fill){fill=this.createNode('olv:fill',node.id+"_fill");}
fill.opacity=style.fillOpacity;if(node._geometryClass=="OpenLayers.Geometry.Point"&&style.externalGraphic){if(style.graphicOpacity){fill.opacity=style.graphicOpacity;}
fill.src=style.externalGraphic;fill.type="frame";if(!(style.graphicWidth&&style.graphicHeight)){fill.aspect="atmost";}}
if(fill.parentNode!=node){node.appendChild(fill);}}
if(typeof style.rotation!="undefined"){if(style.externalGraphic){this.graphicRotate(node,xOffset,yOffset);fill.opacity=0;}else{node.style.rotation=style.rotation;}}
if(options.isStroked){node.strokecolor=style.strokeColor;node.strokeweight=style.strokeWidth+"px";}else{node.stroked=false;}
var strokes=node.getElementsByTagName("stroke");var stroke=(strokes.length==0)?null:strokes[0];if(!options.isStroked){if(stroke){node.removeChild(stroke);}}else{if(!stroke){stroke=this.createNode('olv:stroke',node.id+"_stroke");node.appendChild(stroke);}
stroke.opacity=style.strokeOpacity;stroke.endcap=!style.strokeLinecap||style.strokeLinecap=='butt'?'flat':style.strokeLinecap;stroke.dashstyle=this.dashStyle(style);}
if(style.cursor!="inherit"&&style.cursor!=null){node.style.cursor=style.cursor;}
return node;},graphicRotate:function(node,xOffset,yOffset){var style=style||node._style;var options=node._options;var aspectRatio,size;if(!(style.graphicWidth&&style.graphicHeight)){var img=new Image();img.onreadystatechange=OpenLayers.Function.bind(function(){if(img.readyState=="complete"||img.readyState=="interactive"){aspectRatio=img.width/img.height;size=Math.max(style.pointRadius*2,style.graphicWidth||0,style.graphicHeight||0);xOffset=xOffset*aspectRatio;style.graphicWidth=size*aspectRatio;style.graphicHeight=size;this.graphicRotate(node,xOffset,yOffset);}},this);img.src=style.externalGraphic;return;}else{size=Math.max(style.graphicWidth,style.graphicHeight);aspectRatio=style.graphicWidth/style.graphicHeight;}
var width=Math.round(style.graphicWidth||size*aspectRatio);var height=Math.round(style.graphicHeight||size);node.style.width=width+"px";node.style.height=height+"px";var image=document.getElementById(node.id+"_image");if(!image){image=this.createNode("olv:imagedata",node.id+"_image");node.appendChild(image);}
image.style.width=width+"px";image.style.height=height+"px";image.src=style.externalGraphic;image.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader("+"src='', sizingMethod='scale')";var rotation=style.rotation*Math.PI/180;var sintheta=Math.sin(rotation);var costheta=Math.cos(rotation);var filter="progid:DXImageTransform.Microsoft.Matrix(M11="+costheta+",M12="+(-sintheta)+",M21="+sintheta+",M22="+costheta+",SizingMethod='auto expand')\n";var opacity=style.graphicOpacity||style.fillOpacity;if(opacity&&opacity!=1){filter+="progid:DXImageTransform.Microsoft.BasicImage(opacity="+
opacity+")\n";}
node.style.filter=filter;var centerPoint=new OpenLayers.Geometry.Point(-xOffset,-yOffset);var imgBox=new OpenLayers.Bounds(0,0,width,height).toGeometry();imgBox.rotate(style.rotation,centerPoint);var imgBounds=imgBox.getBounds();node.style.left=Math.round(parseInt(node.style.left)+imgBounds.left)+"px";node.style.top=Math.round(parseInt(node.style.top)-imgBounds.bottom)+"px";},postDraw:function(node){var fillColor=node._style.fillColor;var strokeColor=node._style.strokeColor;if(fillColor=="none"&&node.fillcolor!=fillColor){node.fillcolor=fillColor;}
if(strokeColor=="none"&&node.strokecolor!=strokeColor){node.strokecolor=strokeColor;}},setNodeDimension:function(node,geometry){var bbox=geometry.getBounds();if(bbox){var resolution=this.getResolution();var scaledBox=new OpenLayers.Bounds((bbox.left/resolution-this.offset.x).toFixed(),(bbox.bottom/resolution-this.offset.y).toFixed(),(bbox.right/resolution-this.offset.x).toFixed(),(bbox.top/resolution-this.offset.y).toFixed());node.style.left=scaledBox.left+"px";node.style.top=scaledBox.top+"px";node.style.width=scaledBox.getWidth()+"px";node.style.height=scaledBox.getHeight()+"px";node.coordorigin=scaledBox.left+" "+scaledBox.top;node.coordsize=scaledBox.getWidth()+" "+scaledBox.getHeight();}},dashStyle:function(style){var dash=style.strokeDashstyle;switch(dash){case'solid':case'dot':case'dash':case'dashdot':case'longdash':case'longdashdot':return dash;default:var parts=dash.split(/[ ,]/);if(parts.length==2){if(1*parts[0]>=2*parts[1]){return"longdash";}
return(parts[0]==1||parts[1]==1)?"dot":"dash";}else if(parts.length==4){return(1*parts[0]>=2*parts[1])?"longdashdot":"dashdot";}
return"solid";}},createNode:function(type,id){var node=document.createElement(type);if(id){node.id=id;}
node.unselectable='on';node.onselectstart=function(){return(false);};return node;},nodeTypeCompare:function(node,type){var subType=type;var splitIndex=subType.indexOf(":");if(splitIndex!=-1){subType=subType.substr(splitIndex+1);}
var nodeName=node.nodeName;splitIndex=nodeName.indexOf(":");if(splitIndex!=-1){nodeName=nodeName.substr(splitIndex+1);}
return(subType==nodeName);},createRenderRoot:function(){return this.nodeFactory(this.container.id+"_vmlRoot","div");},createRoot:function(suffix){return this.nodeFactory(this.container.id+suffix,"olv:group");},drawPoint:function(node,geometry){return this.drawCircle(node,geometry,1);},drawCircle:function(node,geometry,radius){if(!isNaN(geometry.x)&&!isNaN(geometry.y)){var resolution=this.getResolution();node.style.left=((geometry.x/resolution-this.offset.x).toFixed()-radius)+"px";node.style.top=((geometry.y/resolution-this.offset.y).toFixed()-radius)+"px";var diameter=radius*2;node.style.width=diameter+"px";node.style.height=diameter+"px";return node;}
return false;},drawLineString:function(node,geometry){return this.drawLine(node,geometry,false);},drawLinearRing:function(node,geometry){return this.drawLine(node,geometry,true);},drawLine:function(node,geometry,closeLine){this.setNodeDimension(node,geometry);var resolution=this.getResolution();var numComponents=geometry.components.length;var parts=new Array(numComponents);var comp,x,y;for(var i=0;i<numComponents;i++){comp=geometry.components[i];x=(comp.x/resolution-this.offset.x);y=(comp.y/resolution-this.offset.y);parts[i]=" "+x.toFixed()+","+y.toFixed()+" l ";}
var end=(closeLine)?" x e":" e";node.path="m"+parts.join("")+end;return node;},drawPolygon:function(node,geometry){this.setNodeDimension(node,geometry);var resolution=this.getResolution();var path=[];var linearRing,i,j,len,ilen,comp,x,y;for(j=0,len=geometry.components.length;j<len;j++){linearRing=geometry.components[j];path.push("m");for(i=0,ilen=linearRing.components.length;i<ilen;i++){comp=linearRing.components[i];x=comp.x/resolution-this.offset.x;y=comp.y/resolution-this.offset.y;path.push(" "+x.toFixed()+","+y.toFixed());if(i==0){path.push(" l");}}
path.push(" x ");}
path.push("e");node.path=path.join("");return node;},drawRectangle:function(node,geometry){var resolution=this.getResolution();node.style.left=(geometry.x/resolution-this.offset.x)+"px";node.style.top=(geometry.y/resolution-this.offset.y)+"px";node.style.width=geometry.width/resolution+"px";node.style.height=geometry.height/resolution+"px";return node;},drawText:function(featureId,style,location){var label=this.nodeFactory(featureId+this.LABEL_ID_SUFFIX,"olv:rect");var textbox=this.nodeFactory(featureId+this.LABEL_ID_SUFFIX+"_textbox","olv:textbox");var resolution=this.getResolution();label.style.left=(location.x/resolution-this.offset.x).toFixed()+"px";label.style.top=(location.y/resolution-this.offset.y).toFixed()+"px";label.style.flip="y";textbox.innerText=style.label;if(style.fillColor){textbox.style.color=style.fontColor;}
if(style.fontFamily){textbox.style.fontFamily=style.fontFamily;}
if(style.fontSize){textbox.style.fontSize=style.fontSize;}
if(style.fontWeight){textbox.style.fontWeight=style.fontWeight;}
textbox.style.whiteSpace="nowrap";textbox.inset="1px,0px,0px,0px";if(!label.parentNode){label.appendChild(textbox);this.textRoot.appendChild(label);}
var align=style.labelAlign||"cm";var xshift=textbox.clientWidth*(OpenLayers.Renderer.VML.LABEL_SHIFT[align.substr(0,1)]);var yshift=textbox.clientHeight*(OpenLayers.Renderer.VML.LABEL_SHIFT[align.substr(1,1)]);label.style.left=parseInt(label.style.left)-xshift-1+"px";label.style.top=parseInt(label.style.top)+yshift+"px";},drawSurface:function(node,geometry){this.setNodeDimension(node,geometry);var resolution=this.getResolution();var path=[];var comp,x,y;for(var i=0,len=geometry.components.length;i<len;i++){comp=geometry.components[i];x=comp.x/resolution-this.offset.x;y=comp.y/resolution-this.offset.y;if((i%3)==0&&(i/3)==0){path.push("m");}else if((i%3)==1){path.push(" c");}
path.push(" "+x+","+y);}
path.push(" x e");node.path=path.join("");return node;},moveRoot:function(renderer){var layer=this.map.getLayer(renderer.container.id);if(layer instanceof OpenLayers.Layer.Vector.RootContainer){layer=this.map.getLayer(this.container.id);}
layer&&layer.renderer.clear();OpenLayers.Renderer.Elements.prototype.moveRoot.apply(this,arguments);layer&&layer.redraw();},importSymbol:function(graphicName){var id=this.container.id+"-"+graphicName;var cache=this.symbolCache[id];if(cache){return cache;}
var symbol=OpenLayers.Renderer.symbol[graphicName];if(!symbol){throw new Error(graphicName+' is not a valid symbol name');return;}
var symbolExtent=new OpenLayers.Bounds(Number.MAX_VALUE,Number.MAX_VALUE,0,0);var pathitems=["m"];for(var i=0;i<symbol.length;i=i+2){x=symbol[i];y=symbol[i+1];symbolExtent.left=Math.min(symbolExtent.left,x);symbolExtent.bottom=Math.min(symbolExtent.bottom,y);symbolExtent.right=Math.max(symbolExtent.right,x);symbolExtent.top=Math.max(symbolExtent.top,y);pathitems.push(x);pathitems.push(y);if(i==0){pathitems.push("l");}}
pathitems.push("x e");var path=pathitems.join(" ");var diff=(symbolExtent.getWidth()-symbolExtent.getHeight())/2;if(diff>0){symbolExtent.bottom=symbolExtent.bottom-diff;symbolExtent.top=symbolExtent.top+diff;}else{symbolExtent.left=symbolExtent.left-diff;symbolExtent.right=symbolExtent.right+diff;}
cache={path:path,size:symbolExtent.getWidth(),left:symbolExtent.left,bottom:symbolExtent.bottom};this.symbolCache[id]=cache;return cache;},CLASS_NAME:"OpenLayers.Renderer.VML"});OpenLayers.Renderer.VML.LABEL_SHIFT={"l":0,"c":.5,"r":1,"t":0,"m":.5,"b":1};