import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ComunicacionComponent } from './forense/comunicacion/comunicacion.component';
import { MapComponent } from './map/map.component';

const routes: Routes = [
  {path:'comunicacion', component:ComunicacionComponent},
  {path:'mapa', component:MapComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
