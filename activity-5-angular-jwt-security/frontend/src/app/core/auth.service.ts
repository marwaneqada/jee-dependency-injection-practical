import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { AuthResponse } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly storageKey = 'secure-banking-session';
  readonly session = signal<AuthResponse | null>(this.readSession());
  readonly authenticated = computed(() => !!this.session());
  readonly admin = computed(() => this.session()?.roles.includes('ROLE_ADMIN') ?? false);

  login(username: string, password: string) {
    return this.http.post<AuthResponse>('/api/auth/login', { username, password }).pipe(tap(session => {
      localStorage.setItem(this.storageKey, JSON.stringify(session));
      this.session.set(session);
    }));
  }
  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.session.set(null);
    void this.router.navigateByUrl('/login');
  }
  token(): string | null { return this.session()?.accessToken ?? null; }
  private readSession(): AuthResponse | null {
    try { return JSON.parse(localStorage.getItem(this.storageKey) ?? 'null') as AuthResponse | null; }
    catch { localStorage.removeItem(this.storageKey); return null; }
  }
}
