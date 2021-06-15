import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
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
  nUsuarios     = 'wait';
  nUsuarioMost  = 'wait';
  nPaises   = 'wait';
  nPaisMost = 'wait';

  constructor(private router:Router, private route:ActivatedRoute, private servicio:ServicioService) {
    
  }

  mapa(){
    console.log("Navegando a mapa");
    this.router.navigate(['mapa']);
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
        this.nIPMost = data[3].toString();
        this.nUsuarios = data[4].toString();
        this.nUsuarioMost = data[5].toString();
        this.nPaises = data[6].toString();
        this.nPaisMost = data[7].toString();
      }
    );
  }
}
/*
this.servicio.getEstadisticasGenerales().subscribe(
  (data: Array<String>) =>{
    console.log(data);
  }
);*/