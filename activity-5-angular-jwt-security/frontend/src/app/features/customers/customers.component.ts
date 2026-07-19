import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BankingService } from '../../core/banking.service';
import { Customer } from '../../core/models';

@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <section class="page">
      <div class="page-head"><div><span class="eyebrow">ADMINISTRATION</span><h1>Customers</h1><p>Create and manage the bank's customer directory.</p></div></div>
      @if (error()) { <div class="alert">{{ error() }}</div> }
      <div class="detail-grid customers-layout">
        <section class="panel customer-list"><div class="panel-head"><h2>Directory</h2><span>{{ customers().length }} customers</span></div>
          @for (customer of customers(); track customer.id) {
            <div class="customer"><span>{{ initials(customer.name) }}</span><div><b>{{ customer.name }}</b><small>{{ customer.email }}</small></div><button class="danger" (click)="remove(customer)">Delete</button></div>
          }
        </section>
        <form class="panel action-form" [formGroup]="form" (ngSubmit)="create()">
          <span class="eyebrow">NEW PROFILE</span><h2>Add customer</h2>
          <label>Full name<input formControlName="name" placeholder="Customer name"></label>
          <label>Email address<input type="email" formControlName="email" placeholder="name@example.com"></label>
          <button class="primary" [disabled]="form.invalid">Create customer</button>
        </form>
      </div>
    </section>
  `
})
export class CustomersComponent implements OnInit {
  private readonly api = inject(BankingService); private readonly fb = inject(FormBuilder);
  readonly customers = signal<Customer[]>([]); readonly error = signal('');
  readonly form = this.fb.nonNullable.group({ name: ['', Validators.required], email: ['', [Validators.required, Validators.email]] });
  ngOnInit(): void { this.load(); }
  load(): void { this.api.customers().subscribe({ next: data => this.customers.set(data), error: err => this.error.set(err.error?.message ?? 'Could not load customers') }); }
  create(): void { if (this.form.invalid) return; const value = this.form.getRawValue(); this.api.createCustomer(value.name, value.email).subscribe({ next: () => { this.form.reset(); this.load(); }, error: err => this.error.set(err.error?.message ?? 'Could not create customer') }); }
  remove(customer: Customer): void { if (confirm(`Delete ${customer.name}?`)) this.api.deleteCustomer(customer.id).subscribe({ next: () => this.load(), error: err => this.error.set(err.error?.message ?? 'Customer still owns bank accounts') }); }
  initials(name: string): string { return name.split(' ').slice(0, 2).map(part => part[0]).join('').toUpperCase(); }
}
