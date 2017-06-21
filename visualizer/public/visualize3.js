//COULEURS : SAPIN; VERT; MENTHE; ANIS;
//				0	1		2		3 
//			 MARINE; BLEU; CIEL; CYAN;
//				4		5	6		7 
//			 BORDEAUX; ROUGE; TOMATE; ROSE;
//				8		9		10		11
//			 PRUNE; VIOLET; LAVANDE; FUCHSIA;
//				12		13		14		15 
//			 NOIR; GRIS; ACIER; BLANC;
//				16	17		18		19 
//			 MARRON; ORANGE; FEU; JAUNE;
//				20		21	22		23 
//			 KAKI
//				24
var couleurs2 =["#003300", "#00cc00", "#339966", "#80ff00", 
				"#00004d", "#0000ff", "#1a75ff", "#00ffff", 
				"#660000", "#cc0000", "#ff5050", "#ff66cc", 
				"#330033", "#660066", "#9966ff", "#ff00ff", 
				"#000000", "#4d4d4d", "#c5c5f0", "#ffffff",/*"#cc0000",*/
				"#663300", "#ff6600", "#ff9900", "#ffff00", 
				"#666633"];
//var ticks=0;
function layout(){
	var svg = d3.select("svg");
	var lines = svg.selectAll("line")
	var circles = svg.selectAll("circle");
	var nodes = toNodeArray(circles[0]);
	var links = toEdgeArray(lines[0], nodes);
	circles.data(nodes);
	lines.data(links);
	force = d3.layout.force()
		.charge(document.getElementById("vcinput").value)
		.linkDistance(document.getElementById("elinput").value)
		.size([1600, 1050])
		.alpha(0.01)
		.gravity(document.getElementById("gvinput").value)
		.nodes(nodes)
		.links(links)
		.start()
		;
	//
	circles.style("fill", function(d){return couleurs2[d.state%couleurs2.length]})
		.attr("r", document.getElementById("vrinput").value)
		.call(force.drag);

	lines.style("stroke-width", document.getElementById("ewinput").value)
		.style("stroke", "#222");
	//
	force.on("tick", function() {
		lines.attr("x1", function(d) { return d.source.x; })
			.attr("y1", function(d) { return d.source.y; })
			.attr("x2", function(d) { return d.target.x; })
			.attr("y2", function(d) { return d.target.y; })
			.attr("sourceID",function(d) {return d.source.name;})
			.attr("targetID",function(d) {return d.target.name;});

		circles.attr("cx", function(d) { return d.x; })
			.attr("cy", function(d) { return d.y; });
		//ticks++;
		//if(ticks > 5){
			fillPosInputs();
		//	ticks=0;
		//}
	});

	force.on("end", function(){
		fillPosInputs();
	});
}

function fillPosInputs(){
	var circles = document.getElementsByClassName("node");
	var i = 0;
	var tx = "";
	while(i < circles.length){
		var name = circles[i].getAttribute("name");
		var cx = circles[i].getAttribute("cx");
		cx = cx.substring(0, cx.indexOf("."));
		var cy = circles[i].getAttribute("cy");
		cy = cy.substring(0, cy.indexOf("."));
		tx = tx +name+";"+cx+";"+cy+"|";
		i++;
	}
	document.getElementById("pos_input_p").value=tx;
	document.getElementById("pos_input_n").value=tx;
}

function toNodeArray(circles){
	var nodeArray = [];
	var i = 0;
	while(i < circles.length){
		nodeArray.push({
			name:circles[i].getAttribute("name"),
			state:circles[i].getAttribute("state"),
			x:parseInt(circles[i].getAttribute("cx")),
			//fixed:true,
			y:parseInt(circles[i].getAttribute("cy"))
		});
		//console.log(i+" : "+nodeArray[i].name);
		i++;
	}
	return nodeArray;
}

function toEdgeArray(lines, nodes){
	var edgeArray = []
	var i = 0;
	while(i < lines.length){
		var x = 0;
		while(x < nodes.length && nodes[x].name != lines[i].getAttribute("src")){x++;}
		var k = 0;
		while(k < nodes.length && nodes[k].name != lines[i].getAttribute("dst")){k++;}
		edgeArray.push({
			source:x,
			target:k
		});
		i++;
	}
	return edgeArray;
}

function updatePostNumbers(){
	document.getElementById("vr_input_p").value = document.getElementById("vrinput").value;
	document.getElementById("vr_input_n").value = document.getElementById("vrinput").value;
	document.getElementById("vc_input_p").value = document.getElementById("vcinput").value;
	document.getElementById("vc_input_n").value = document.getElementById("vcinput").value;
	document.getElementById("el_input_p").value = document.getElementById("elinput").value;
	document.getElementById("el_input_n").value = document.getElementById("elinput").value;
	document.getElementById("ew_input_p").value = document.getElementById("ewinput").value;
	document.getElementById("ew_input_n").value = document.getElementById("ewinput").value;
	document.getElementById("ad_input_p").value = document.getElementById("adinput").value;
	document.getElementById("ad_input_n").value = document.getElementById("adinput").value;
	document.getElementById("gv_input_p").value = document.getElementById("gvinput").value;
	document.getElementById("gv_input_n").value = document.getElementById("gvinput").value;
}

function toAutoMode0(){
	document.getElementById("automode_input_p").value = 0;
	document.getElementById("automode_input_n").value = 0;
}
function toAutoMode1(){
	var amdelay = document.getElementById("adinput").value;
	document.getElementById("automode_input_p").value = 1;
	document.getElementById("automode_input_n").value = 1;
	window.setTimeout(function(){autoTransition()}, amdelay);
}
function toAutoMode2(){
	var amdelay = document.getElementById("adinput").value;
	document.getElementById("automode_input_p").value = 2;
	document.getElementById("automode_input_n").value = 2;
	window.setTimeout(function(){autoTransition()}, amdelay);
}

function autoTransition(){
	var automode = document.getElementById("automode_input_n").value;
	if(automode==1){
		document.next.submit();
	}else if(automode==2){
		document.previous.submit();
	}
}
