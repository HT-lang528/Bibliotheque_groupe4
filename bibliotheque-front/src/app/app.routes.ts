import { Routes } from '@angular/router';
import { AuteurPage } from './page/auteur-page/auteur-page';
import { HomePage } from './page/home-page/home-page';
import { EditeurPage } from './page/editeur-page/editeur-page';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'home' },
  { path: 'home', component: HomePage },
  { path: 'auteur', component: AuteurPage },
  { path: 'editeur', component: EditeurPage },
  { path: 'livres', loadComponent: () => import('./page/livre-page/livre-page').then(m => m.LivrePage) },
  { path: 'collection', loadComponent: () => import('./page/collection-page/collection-page').then(m => m.CollectionPage) },
];
