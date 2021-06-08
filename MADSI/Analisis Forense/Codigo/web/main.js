/*window.onload = function(){
    document.getElementById("btnEnviar").addEventListener("click", function(){
        fetch('http://localhost:48137/AnalisisForense_gf/app/Principal/setContent', {
                method: 'GET',
                body: JSON.stringify({
                    name: "Taylor",
                    surname: "Swift"
                }),
                headers: {
                    "Content-type": "application/json"
                }
            })
        .then(response => response.json())
        .then(json => console.log(json))
    });
}*/
function readFile (e) {
  e.preventDefault();
  let file = e.dataTransfer.files[0];
  
  if (file.type === 'text/plain') {
    let reader = new FileReader();
    reader.onloadend = () => printFileContents(reader.result);
    reader.readAsText(file, 'ISO-8859-1');
  } else {
    alert('¡He dicho archivo de texto!');
  }
}

function printFileContents (contents) {
  area.style.lineHeight = '30px';
  area.textContent = '';
  let lines = contents.split(/\n/);

  lines.forEach(line => area.textContent += line + '\n');
}

function eMap(){
    $.ajax({
        data: {"content": arguments[0]},
        type: "POST",
        dataType: "json",
        url: "http://localhost:48137/AnalisisForense_gf/app/Principal/setContent"
    })
    .done(function(data, textStatus, jqXHR){
        if(console && console.log){
            console.log("La solicitud se ha completadocorrectamente");
        }
    })
    .fail(function( jqXHR, textStatus, errorThrown){
        if(console && console.log){
            console.log("La solicitud a fallado "+ textStatus);
        }
    });
}

window.onload = function(){
    document.getElementById("btnEnviar").addEventListener("click", function(){
        eMap("buuu" + document.getElementById("ruta").value);
    });
}

