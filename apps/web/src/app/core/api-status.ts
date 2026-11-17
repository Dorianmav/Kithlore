import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { catchError, interval, map, of } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Injectable({ providedIn: 'root' })
export class ApiStatus {
  private readonly http = inject(HttpClient);
  readonly online = signal(false);

  constructor() {
    this.check();
    interval(30_000)
      .pipe(takeUntilDestroyed())
      .subscribe(() => this.check());
  }

  private check(): void {
    this.http
      .get<{ status: string }>('/api/actuator/health')
      .pipe(
        map((response) => response.status === 'UP'),
        catchError(() => of(false)),
      )
      .subscribe((online) => this.online.set(online));
  }
}
