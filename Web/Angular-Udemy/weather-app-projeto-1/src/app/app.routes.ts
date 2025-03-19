import { Routes } from '@angular/router';
import { HomePageComponent } from './pages/home-page/home-page.component';


export const routes: Routes = [
  { path: 'home-page', component: HomePageComponent }, // Rota para /home-page
  { path: '', redirectTo: '/home-page', pathMatch: 'full' }, // Rota padrão redireciona para /home-page
];
