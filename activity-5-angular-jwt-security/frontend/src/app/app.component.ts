import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    @if (auth.authenticated()) {
      <header class="topbar">
        <a class="brand" routerLink="/accounts"><span>SB</span> SecureBank</a>
        <nav>
          <a routerLink="/accounts" routerLinkActive="active">Accounts</a>
          @if (auth.admin()) { <a routerLink="/customers" routerLinkActive="active">Customers</a> }
        </nav>
        <div class="profile"><span>{{ auth.session()?.username }}</span><button class="ghost" (click)="auth.logout()">Sign out</button></div>
      </header>
    }
    <main [class.authenticated]="auth.authenticated()"><router-outlet /></main>
  `
})
export class AppComponent { readonly auth = inject(AuthService); }
