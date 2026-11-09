import { HttpClient } from '@angular/common/http';
import { inject, Injectable, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { Auteur } from '../model/auteur';
import { Livre } from '../model/livre';

@Injectable({
  providedIn: 'root',
})
export class AuteurService {
    private apiUrl:string="/api/auteur";
    private apiUrlLivre:string="/api/livre/auteur";
    private http: HttpClient = inject(HttpClient);
    
    public findAll(): Observable<Auteur[]> {
        console.log("service find all ")
    return this.http.get<Auteur[]>(this.apiUrl);
    }


    public findLivre(auteur: Auteur):Observable<Livre[]>{
      return this.http.get<Livre[]>(`${ this.apiUrlLivre }/${ auteur.id }`);
    }

    public save(auteur: Auteur): Observable<Auteur> {
    if (!auteur.id) {
      return this.http.post<Auteur>(this.apiUrl, auteur);
    }

        return this.http.put<Auteur>(`${ this.apiUrl }/${ auteur.id }`, auteur);
    }

    public remove(auteur: Auteur): Observable<void> {
        return this.http.delete<void>(`${ this.apiUrl }/${ auteur.id }`);
    }

}
