import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MeuPrimeiro2Component } from "./meu-primeiro2/meu-primeiro2.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    MeuPrimeiro2Component
],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'Ol,áaaa';
}
