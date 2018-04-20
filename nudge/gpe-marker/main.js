var http = require('http');

function main(args) {
    if(args === undefined || args.length !== 1) {
        console.log("Usage: node main.js '<text>'");
        return
    }

    ent(args[0])
}

function ent(text){
    var data = JSON.stringify({text: text, model: "en"});
    var req = http.request({
        hostname: '139.162.230.113',
        port: 8888,
        path: '/ent',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Content-Length': Buffer.byteLength(data)
        }
    },function(res){
        res.on('data', function (body) {
            var data = JSON.parse(body);
            console.log(augmentText(text,data));
        });
        req.on('error', function(e) {
            console.log('problem with request: ' + e.message);
        });
    });

    req.write(data);
    req.end();
}

function augmentText(text,data) {
    var offset = 0;
    var tags = {
        start: "<span data-entity=\"gpe\">",
        end: "</span>",
        length: function () {
            return this.start.length + this.end.length;
        }
    };

    for (var i = 0; i < data.length; i++) {
        var gpe = data[i];
        text = text.slice(0, gpe.start + offset) + tags.start + text.slice(gpe.start + offset, gpe.end + offset) + tags.end + text.slice(gpe.end + offset);
        offset += tags.length();
    }
    return text;
}

var args = process.argv.slice(2);
main(args);