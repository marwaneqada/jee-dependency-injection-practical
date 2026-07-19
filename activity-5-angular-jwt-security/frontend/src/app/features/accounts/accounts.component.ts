import { Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { BankingService } from '../../core/banking.service';
import { Account, Operation } from '../../core/models';

@Component({
  standalone: true,
  imports: [CurrencyPipe, DatePipe, ReactiveFormsModule],
  template: `
    <section class="page">
      <div class="page-head"><div><span class="eyebrow">PORTFOLIO OVERVIEW</span><h1>Bank accounts</h1><p>Balances, owners, and recent account movements.</p></div><button class="ghost" (click)="load()">Refresh</button></div>
      @if (error()) { <div class="alert">{{ error() }}</div> }
      @if (loading()) { <div class="empty">Loading secure account data…</div> }
      <div class="account-grid">
        @for (account of accounts(); track account.id) {
          <button class="account-card" [class.selected]="selected()?.id === account.id" (click)="select(account)">
            <div><span class="pill">{{ account.type }}</span><span class="status">● {{ account.status }}</span></div>
            <small>{{ account.customer.name }}</small><strong>{{ account.balance | currency:'MAD ':'symbol':'1.2-2' }}</strong>
            <code>{{ account.id.slice(0, 8) }} ···· {{ account.id.slice(-4) }}</code>
          </button>
        }
      </div>
      @if (selected(); as account) {
        <div class="detail-grid">
          <section class="panel">
            <div class="panel-head"><div><span class="eyebrow">RECENT ACTIVITY</span><h2>Operations</h2></div><span>{{ operations().length }} shown</span></div>
            <div class="operations">
              @for (operation of operations(); track operation.id) {
                <div class="operation"><span [class.credit]="operation.type === 'CREDIT'">{{ operation.type === 'CREDIT' ? '↙' : '↗' }}</span><div><b>{{ operation.description }}</b><small>{{ operation.operationDate | date:'medium' }}</small></div><strong [class.negative]="operation.type === 'DEBIT'">{{ operation.type === 'DEBIT' ? '−' : '+' }}{{ operation.amount | currency:'MAD ':'symbol':'1.2-2' }}</strong></div>
              } @empty { <div class="empty">No operations for this account.</div> }
            </div>
          </section>
          <form class="panel action-form" [formGroup]="operationForm" (ngSubmit)="submitOperation()">
            <span class="eyebrow">NEW TRANSACTION</span><h2>Move funds</h2>
            <div class="segmented"><button type="button" [class.active]="mode() === 'credit'" (click)="mode.set('credit')">Credit</button><button type="button" [class.active]="mode() === 'debit'" (click)="mode.set('debit')">Debit</button><button type="button" [class.active]="mode() === 'transfer'" (click)="mode.set('transfer')">Transfer</button></div>
            <label>Amount (MAD)<input type="number" min="0.01" step="0.01" formControlName="amount"></label>
            @if (mode() === 'transfer') {
              <label>Destination account<select formControlName="destination"><option value="">Choose an account</option>@for (target of accounts(); track target.id) { @if (target.id !== account.id) { <option [value]="target.id">{{ target.customer.name }} — {{ target.id.slice(0, 8) }}</option> } }</select></label>
            }
            <label>Description<input formControlName="description" placeholder="Transaction purpose"></label>
            <button class="primary" [disabled]="operationForm.invalid || submitting()">Confirm {{ mode() }}</button>
          </form>
        </div>
      }
    </section>
  `
})
export class AccountsComponent implements OnInit {
  private readonly api = inject(BankingService);
  private readonly fb = inject(FormBuilder);
  readonly accounts = signal<Account[]>([]); readonly selected = signal<Account | null>(null);
  readonly operations = signal<Operation[]>([]); readonly loading = signal(true); readonly submitting = signal(false);
  readonly error = signal(''); readonly mode = signal<'credit' | 'debit' | 'transfer'>('credit');
  readonly operationForm = this.fb.nonNullable.group({ amount: [100, [Validators.required, Validators.min(0.01)]], description: ['', Validators.required], destination: [''] });
  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading.set(true); this.error.set('');
    this.api.accounts().pipe(finalize(() => this.loading.set(false))).subscribe({
      next: accounts => { this.accounts.set(accounts); if (accounts.length) this.select(accounts.find(a => a.id === this.selected()?.id) ?? accounts[0]); },
      error: err => this.error.set(err.error?.message ?? 'Could not load accounts')
    });
  }
  select(account: Account): void { this.selected.set(account); this.api.history(account.id).subscribe(page => this.operations.set(page.operations)); }
  submitOperation(): void {
    const account = this.selected(); if (!account || this.operationForm.invalid) return;
    const value = this.operationForm.getRawValue(); this.error.set('');
    if (this.mode() === 'transfer' && !value.destination) { this.error.set('Choose a destination account'); return; }
    this.submitting.set(true);
    const request = this.mode() === 'credit' ? this.api.credit(account.id, value.amount, value.description)
      : this.mode() === 'debit' ? this.api.debit(account.id, value.amount, value.description)
      : this.api.transfer(account.id, value.destination, value.amount, value.description);
    request.pipe(finalize(() => this.submitting.set(false))).subscribe({ next: () => { this.operationForm.patchValue({ description: '' }); this.load(); }, error: err => this.error.set(err.error?.message ?? 'Transaction failed') });
  }
}
