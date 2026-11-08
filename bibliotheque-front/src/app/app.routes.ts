import { Routes } from '@angular/router';
import { AuteurPage } from './page/auteur-page/auteur-page';
import { HomePage } from './page/home-page/home-page';
import { EditeurPage } from './page/editeur-page/editeur-page';

export const routes: Routes = [

    {
    path: 'home',
    component: HomePage,
  },

    {
        path:"auteur",
        component:AuteurPage,
    },

    {
        path:"editeur",
        component:EditeurPage,
    },

    {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  }

];
