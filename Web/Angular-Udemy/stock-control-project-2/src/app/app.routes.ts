import { Routes } from '@angular/router';
import { HomeComponent } from './modules/home/home.component';

export const routes: Routes = [
  { path: 'home', component: HomeComponent }, // Rota para /home-page
  { path: '', redirectTo: '/home', pathMatch: 'full' }, // Rota padrão redireciona para /home-page
];
