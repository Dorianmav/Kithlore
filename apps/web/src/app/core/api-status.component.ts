import { Component, inject } from '@angular/core';
import { TranslocoModule } from '@jsverse/transloco';
import { ApiStatus } from './api-status';

@Component({
  imports: [TranslocoModule],
  selector: 'app-api-status',
  template: `
    <ng-container *transloco="let t">
      <p class="mt-6 flex items-center gap-3 text-lg font-medium" role="status" aria-live="polite">
        <span
          class="size-3 rounded-full"
          [class.bg-emerald-400]="status.online()"
          [class.bg-rose-400]="!status.online()"
          aria-hidden="true"
        ></span>
        <span>{{ status.online() ? t('api.online') : t('api.offline') }}</span>
      </p>
    </ng-container>
  `,
})
export class ApiStatusComponent {
  protected readonly status = inject(ApiStatus);
}
