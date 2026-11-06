import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'livres' },
  { path: 'livres', loadComponent: () => import('./page/livre-page/livre-page').then(m => m.LivrePage) },
];
