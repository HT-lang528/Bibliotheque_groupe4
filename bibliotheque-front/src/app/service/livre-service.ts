import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { forkJoin, Observable } from 'rxjs';
import { AuteurLivre, EditeurLivre, Livre, LivreReferences, LivreRequest } from '../model/livre';
import { Collection } from '../model/collection';

@Injectable({ providedIn: 'root' })
export class LivreService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/livre';

  findAll(): Observable<Livre[]> { return this.http.get<Livre[]>(this.apiUrl); }
  save(livre: LivreRequest, id?: number): Observable<Livre> {
    return id === undefined
      ? this.http.post<Livre>(this.apiUrl, livre)
      : this.http.put<Livre>(`${this.apiUrl}/${id}`, livre);
  }
  remove(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/${id}`); }

  // Lecture des listes nécessaires aux sélecteurs, sans gérer les autres CRUD.
  references(): Observable<LivreReferences> {
    return forkJoin({
      auteurs: this.http.get<AuteurLivre[]>('/api/auteur'),
      editeurs: this.http.get<EditeurLivre[]>('/api/editeur'),
      collections: this.http.get<Collection[]>('/api/collection'),
    });
  }
}
