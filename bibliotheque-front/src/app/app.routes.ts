import { Routes } from '@angular/router';
import { AuteurPage } from './page/auteur-page/auteur-page';
import { HomePage } from './page/home-page/home-page';

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
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  }

];
