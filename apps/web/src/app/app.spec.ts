import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Translation, TranslocoLoader, provideTransloco } from '@jsverse/transloco';
import { of } from 'rxjs';
import { App } from './app';
import { ApiStatus } from './core/api-status';

@Injectable()
class TestTranslationLoader implements TranslocoLoader {
  getTranslation(): ReturnType<TranslocoLoader['getTranslation']> {
    return of({
      navigation: { label: 'Navigation principale' },
      brand: { name: 'Kithlore', tagline: 'Les histoires se tissent ensemble' },
      home: {
        eyebrow: 'Un monde vivant, à écrire ensemble',
        title: 'Un monde à façonner, ensemble.',
        description: 'Les récits se croisent.',
        badge: 'L’aventure commence bientôt',
        comingSoon: 'Le monde se prépare.',
      },
      api: {
        label: 'Services',
        title: 'État de la plateforme',
        description: 'Connexion vérifiée.',
        online: 'API en ligne',
        offline: 'API hors ligne',
      },
    } as Translation);
  }
}

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideTransloco({
          config: {
            availableLangs: ['fr'],
            defaultLang: 'fr',
            fallbackLang: 'fr',
            prodMode: true,
          },
          loader: TestTranslationLoader,
        }),
      ],
    }).compileComponents();
  });

  it('shows the API as online when the health endpoint responds UP', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    TestBed.inject(HttpTestingController).expectOne('/api/actuator/health').flush({ status: 'UP' });
    await fixture.whenStable();
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Un monde à façonner, ensemble.');
    expect(fixture.nativeElement.textContent).toContain('API en ligne');
    expect(TestBed.inject(ApiStatus).online()).toBe(true);
  });
});
