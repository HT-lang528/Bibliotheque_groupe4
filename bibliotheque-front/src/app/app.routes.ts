import { authGuard } from './guard/auth-guard';
import { Routes } from '@angular/router';
import { AuteurPage } from './page/auteur-page/auteur-page';
import { HomePage } from './page/home-page/home-page';
import { EditeurPage } from './page/editeur-page/editeur-page';
import { LoginPage } from './page/login-page/login-page';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: 'inscription', loadComponent: () => import('./page/signup-page/signup-page').then(m => m.SignupPage) },
  { path: 'login', component: LoginPage},
  { path: 'home', canActivate: [authGuard], component: HomePage },
  { path: 'auteur', canActivate: [authGuard], component: AuteurPage },
  { path: 'editeur', canActivate: [authGuard], component: EditeurPage },
  { path: 'livres', canActivate: [authGuard], loadComponent: () => import('./page/livre-page/livre-page').then(m => m.LivrePage) },
  { path: 'collection', canActivate: [authGuard], loadComponent: () => import('./page/collection-page/collection-page').then(m => m.CollectionPage) },
  {path: 'avis/:idLivre',canActivate: [authGuard],loadComponent: () => import('./page/avis-page/avis-page').then(m => m.AvisPage)},
];
