$.fn.extend({  
    showAtCenter: function( showBackground ){
		if(showBackground) {
			var width = document.documentElement.clientWidth;
			var height = document.documentElement.clientHeight;	
			var divEffect = document.getElementById('divEffect');
			if(divEffect==null){
				divEffect = document.createElement( 'div' );		
				divEffect.id = "divEffect";
				divEffect.style.position = "fixed";
				divEffect.style.top = 0;
				divEffect.style.width = width + "px";
				divEffect.style.height = height + "px";
				divEffect.style.background = "#000000";
				divEffect.style.opacity = 0.5;
				divEffect.style.zIndex = 10;	
				document.body.appendChild( divEffect );	
			}
		}
		var div = document.getElementById(this.attr('id'));
		var width = div.style.width;
		var height = div.style.height;		
		var x = (document.documentElement.clientHeight / 2) - new Number(height.replace('px',''))/2;
		var y = (document.documentElement.clientWidth / 2) - new Number(width.replace('px',''))/2;	
		div.style.top = x +"px";
		div.style.left  = y +"px";	
		this.show();
	}  
});