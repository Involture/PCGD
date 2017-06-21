var http = require('http');
var url = require('url');
var express = require('express');
var fs = require('fs');
var bodyParser = require('body-parser')

var app = express();

/*PARAMS*/
var width = 1600;
var height = 850;
/**/

console.log(process.cwd());


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use("/public", express.static(__dirname + '/public'));

app.get("/g/:n",function(req,res){
    //console.log(req);
    var i = req.params.n;
    if(! isNaN(parseInt(i))){
        var filename = ("graphs/graph"+i+".json");
        var json = fs.readFileSync((process.cwd()+"/"+filename), 'utf8');
        var graph = JSON.parse(json);
        var positions = {};
        res.render("graph.ejs",{
            graph:graph,
            n:req.params.n,
            positions:positions,
            posraw:"",
            vr:6,
            vc:-40,
            el:10,
            ew:4,
            gv:0.01,
            automode:0,
            amdelay:600
        });
    }
});

app.post("/g/:n", function(req,res){
    //console.log(req);
    var i = req.params.n;
    if(! isNaN(parseInt(i))){
        var filename = ("graphs/graph"+i+".json");
        var json = fs.readFileSync((process.cwd()+"/"+filename), 'utf8');
        var graph = JSON.parse(json);
        var positions = decoupe(req.body.positions);
        //console.log(positions);
        res.render("graph.ejs", {
            graph:graph, n:req.params.n,
            positions:positions,
            posraw:req.body.positions,
            vr:req.body.vr,
            vc:req.body.vc,
            el:req.body.el,
            ew:req.body.ew,
            gv:req.body.gv,
            automode:req.body.automode,
            amdelay:req.body.amdelay
        });
    }
});

app.listen(8080);
console.log("Server running on 8080...")

function decoupe(positions){
    var ret = {};
    var sep = "|";
    var sep2 = ";";
    var pos = positions;
    //console.log("Coucou");
    while(pos.indexOf(sep) != -1){
        loc=pos.substring(0, pos.indexOf(sep));
        //
        var n,cx,cy;
        n = loc.substring(0,loc.indexOf(sep2));
        loc = loc.substring(loc.indexOf(sep2)+1, loc.lengt);
        cx = loc.substring(0, loc.indexOf(sep2));
        loc = loc.substring(loc.indexOf(sep2)+1, loc.lengt);
        cy = loc;
        ret[n]={cx,cy};
        //
        pos = pos.substring(pos.indexOf(sep)+1, pos.length);
    }
    //console.log(ret);
    //console.log(ret["h1"]["cx"]);
    return ret;
}
