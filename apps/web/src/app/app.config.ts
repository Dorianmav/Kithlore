import { provideHttpClient } from '@angular/common/http';
import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideTransloco } from '@jsverse/transloco';
import { TranslocoHttpLoader } from './core/transloco-http-loader';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(),
    provideRouter(routes),
    provideTransloco({
      config: {
        availableLangs: ['fr'],
        defaultLang: 'fr',
        fallbackLang: 'fr',
        prodMode: true,
      },
      loader: TranslocoHttpLoader,
    }),
  ],
};
