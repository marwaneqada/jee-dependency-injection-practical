import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/auth.service';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <section class="login-shell">
      <div class="login-story">
        <span class="eyebrow">SECURE DIGITAL BANKING</span>
        <h1>Your finances,<br><em>clearly protected.</em></h1>
        <p>Explore account activity and banking operations through a JWT-secured Spring Boot API.</p>
        <div class="security-note"><b>JWT</b><span>Stateless authentication<br><small>Role-based access control</small></span></div>
      </div>
      <form class="login-card" [formGroup]="form" (ngSubmit)="submit()">
        <div class="mark">SB</div><h2>Welcome back</h2><p>Sign in to your secure workspace.</p>
        <label>Username<input formControlName="username" autocomplete="username"></label>
        <label>Password<input type="password" formControlName="password" autocomplete="current-password"></label>
        @if (error()) { <div class="alert">{{ error() }}</div> }
        <button class="primary full" [disabled]="form.invalid || loading()">{{ loading() ? 'Signing in…' : 'Sign in securely' }}</button>
        <div class="demo"><strong>Demo accounts</strong><span>admin / admin123</span><span>user / user123</span></div>
      </form>
    </section>
  `
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly form = this.fb.nonNullable.group({ username: ['admin', Validators.required], password: ['admin123', Validators.required] });
  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true); this.error.set('');
    this.auth.login(this.form.getRawValue().username, this.form.getRawValue().password)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({ next: () => void this.router.navigateByUrl('/accounts'), error: err => this.error.set(err.error?.message ?? 'Unable to sign in') });
  }
}
