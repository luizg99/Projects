import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideClientHydration } from '@angular/platform-browser';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient } from '@angular/common/http';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimations(),  // Equivalente ao BrowserAnimationsModule
    provideHttpClient(),  // Equivalente ao HttpClientModule
    provideClientHydration(),
    provideAnimationsAsync(), // ✅ Não precisa mais de providePrimeNG aqui
    providePrimeNG({
      theme: {
          preset: Aura,
      },
      ripple:true
  })
  ]
};
