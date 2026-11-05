import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Collection } from '../model/collection';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class CollectionService {
  private apiUrl: string = "/collection";
  private http: HttpClient = inject(HttpClient);

  public findAll(): Observable<Collection[]> {
    return this.http.get<Collection[]>(this.apiUrl);
  }

  public save(collection: Collection): Observable<Collection> {
    if (!collection.id) {
      return this.http.post<Collection>(this.apiUrl, collection);
    }

    return this.http.put<Collection>(`${ this.apiUrl }/${ collection.id }`, collection);
  }

  public remove(collection: Collection): Observable<void> {
    return this.http.delete<void>(`${ this.apiUrl }/${ collection.id }`);
  }
}

