import { HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from '../service/auth-service';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { inject } from '@angular/core';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const localApi = req.url.startsWith('/api/') || req.url.startsWith('http://localhost:8080/api/');
  const publicRoute = /\/user\/(auth|subscribe)$/.test(req.url);
  if (!localApi || publicRoute || !auth.token) return next(req);
  return next(req.clone({ setHeaders: { Authorization: `Bearer ${auth.token}` } })).pipe(
    catchError(error => {
      if (error.status === 401) { auth.logout(); router.navigate(['/login']); }
      return throwError(() => error);
    })
  );
};
