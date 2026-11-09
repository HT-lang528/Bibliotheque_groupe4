import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthResponse } from '../model/auth-response';
import { HttpClient } from '@angular/common/http';
import { Auth } from '../model/auth';

@Injectable({
  providedIn: 'root', 
})
export class AuthService {
  private _token: string = sessionStorage.getItem('token') ?? "";

  public get token(): string {
    if (this._token) {
      try {
        const payload = JSON.parse(atob(this._token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
        if (typeof payload.exp !== 'number' || payload.exp * 1000 <= Date.now()) this.logout();
      } catch { this.logout(); }
    }
    return this._token;
  }

  public set token(value: string) {
    this._token = value;
    if (value) sessionStorage.setItem('token', value);
    else sessionStorage.removeItem('token');
  }

  constructor(private http: HttpClient) {
  }

  public subscribe(auth: Auth): Observable<number> {
    return this.http.post<number>('/api/user/subscribe', auth);
  }

  public logout(): void {
    this._token = '';
    sessionStorage.removeItem('token');
  }

  public login(auth: Auth): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/user/auth', auth);
  }
}

