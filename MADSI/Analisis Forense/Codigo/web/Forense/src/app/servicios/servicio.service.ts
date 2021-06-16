import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ServicioService {

  UrlApi='/Analisis/api';

  constructor(private http:HttpClient) { }

  getEstadisticasGenerales(): Observable<Array<String>>{
    return this.http.get<Array<String>>(this.UrlApi);
  }

  enviarArchivo(data: String): Observable<Array<String>>{
    console.log("Enviando archivo.");
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<any>(this.UrlApi+'/analizar/archivo', data, {headers : headers});
  }

  obtenerEstadisticasGen(): Observable<Array<String>>{
    console.log("Obteniendo Informacion de Ataques.");
    return this.http.get<Array<String>>(this.UrlApi+'/analizar/ataques');
  }

  obtenerIPs(): Observable<Array<String>>{
    console.log("Obteniendo Informacion de Ataques.");
    return this.http.get<Array<String>>(this.UrlApi+'/analizar/AllIPs');
  }

  obtenerUsuarios(): Observable<Array<String>>{
    console.log("Obteniendo Informacion de Ataques.");
    return this.http.get<Array<String>>(this.UrlApi+'/analizar/AllCountries');
  }

  obtenerPaises(): Observable<Array<String>>{
    console.log("Obteniendo Informacion de Ataques.");
    return this.http.get<Array<String>>(this.UrlApi+'/analizar/AllUsers');
  }

}
