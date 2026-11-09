import { HttpInterceptorFn } from '@angular/common/http';

export const apiUrlInterceptor: HttpInterceptorFn = (req, next) => {
  // Le proxy Angular transmet /api au backend ; ne pas doubler le préfixe.
  if (!req.url.startsWith('/') || req.url.startsWith('//') || req.url.startsWith('/api/')) {
    return next(req);
  }
  return next(req.clone({ url: `/api${req.url}` }));
};
