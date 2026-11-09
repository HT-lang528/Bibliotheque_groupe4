import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';          // tap ajouté
import { HttpClient } from '@angular/common/http';
import { Auth } from '../model/auth';
// import AuthResponse supprimé : plus utilisé

@Injectable({
  providedIn: 'root', 
})
export class AuthService {
  private _token: string = sessionStorage.getItem('token') ?? "";

  public get token(): string {
    return this._token;
  }

  public set token(value: string) {
    this._token = value;
  }

  constructor(private http: HttpClient) {
  }

  public login(auth: Auth): Observable<string> {
  return this.http.post('/user/auth', auth, { responseType: 'text' }).pipe(
    tap(token => {
      this._token = token;
      sessionStorage.setItem('token', token);
    })
  );
}
}

