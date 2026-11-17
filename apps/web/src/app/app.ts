import { Component } from '@angular/core';
import { TranslocoModule } from '@jsverse/transloco';
import { ApiStatusComponent } from './core/api-status.component';

@Component({
  imports: [TranslocoModule, ApiStatusComponent],
  selector: 'app-root',
  templateUrl: './app.html',
})
export class App {}
