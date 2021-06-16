import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ServicioService } from 'src/app/servicios/servicio.service';

@Component({
  selector: 'app-comunicacion',
  templateUrl: './comunicacion.component.html',
  styleUrls: ['./comunicacion.component.scss']
})
export class ComunicacionComponent implements OnInit {
  contenido = 'wait';

  nAtaques  = 'wait';
  nIntentos = 'wait';
  nIPs      = 'wait';
  nIPMost   = 'wait';
  ipMost    = 'wait';
  nUsuarios     = 'wait';
  nUsuarioMost  = 'wait';
  usuarioMost   = 'wait'; 
  nPaises   = 'wait';
  nPaisMost = 'wait';
  paisMost  = 'wait'; 

  ips: Array<String> = [];
  ipsn: Array<String> = [];
  longitudes: Array<String> = [];
  latitudes: Array<String> = [];
  usuarios: Array<String> = [];
  paises: Array<String> = [];

  constructor(private router:Router, private route:ActivatedRoute, private servicio:ServicioService) {
    
  }

  ngOnInit(): void {
    console.log("In Comunicacion");
    this.route.queryParams.subscribe(params => {
      this.contenido = params.contenido;
    });

    this.servicio.enviarArchivo(this.contenido).subscribe(
      (data: Array<String>) =>{
        console.log("Archivo Recibido.");
        console.log(data);
        this.nAtaques = data[0].toString();
        this.nIntentos = data[1].toString();
        this.nIPs = data[2].toString();
        this.nUsuarios = data[3].toString();
        this.nPaises = data[4].toString();
        
        this.ipMost = data[5].toString();
        this.nIPMost = data[6].toString();
        this.usuarioMost = data[7].toString();
        this.nUsuarioMost = data[8].toString();
        this.paisMost = data[9].toString();
        this.nPaisMost = data[10].toString();
    
        this.servicio.obtenerIPs().subscribe(
          (data: Array<String>) =>{
            console.log("Informacion de ataques IPs");
            console.log(data);
            for(var i = 0; i < data.length; i++){
              var datasplit = data[i].split(":");
              this.ips.push(datasplit[0]);
              this.latitudes.push(datasplit[1]);
              this.longitudes.push(datasplit[2]);
              this.ipsn.push(datasplit[3]);
            }
          }
        );

        this.servicio.obtenerUsuarios().subscribe(
          (data: Array<String>) =>{
            console.log("Informacion de ataques Usuarios");
            console.log(data);
            for(var i = 0; i < data.length; i++){
              var datasplit = data[i].split(":");
              this.usuarios.push(datasplit[0]);
            }
          }
        );

        this.servicio.obtenerPaises().subscribe(
          (data: Array<String>) =>{
            console.log("Informacion de ataques Paises");
            console.log(data);
            for(var i = 0; i < data.length; i++){
              var datasplit = data[i].split(":");
              this.paises.push(datasplit[0]);
            }
          }
        );
      }
    );

 }

}
