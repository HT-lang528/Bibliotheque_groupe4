import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Avis } from '../model/avis';

@Service()
export class AvisService {
//   private apiUrl: string = "/avis";
  private http: HttpClient = inject(HttpClient);
  private apiUrl: string = "/api/avis";

  public findAll(): Observable<Avis[]> {
    return this.http.get<Avis[]>(this.apiUrl);
  }

  public save(avis: Avis): Observable<Avis> {
    if (!avis.id) {
      return this.http.post<Avis>(this.apiUrl, avis);
    }

    return this.http.put<Avis>(`${ this.apiUrl }/${ avis.id }`, avis);
  }

  public remove(avis: Avis): Observable<void> {
    return this.http.delete<void>(`${ this.apiUrl }/${ avis.id }`);
  }

  public findByLivreId(idLivre: number): Observable<Avis[]> {
    return this.http.get<Avis[]>(`${this.apiUrl}/livre/${idLivre}`);
  }
}
