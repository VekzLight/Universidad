import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ServicioService } from './servicios/servicio.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Forense';
  contenido= '';

  constructor(private router:Router, private servicio:ServicioService){}

  enviar(){
    console.log("Navegando a comunicacion.");
    this.router.navigate(['comunicacion'],{ queryParams: { contenido: this.contenido } });
  }

  onFileSelected(e: any) {
    const reader = new FileReader();
    
    if(e.target.files && e.target.files.length) {
      const [file] = e.target.files;
      reader.readAsDataURL(file);
    
      reader.onload = () => {
        this.contenido = reader.result as string;
        console.log(this.contenido);
      };
    }

    console.log("Archivo Seleccionado.");
  }
}
