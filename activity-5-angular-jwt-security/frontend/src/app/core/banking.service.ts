import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Account, Customer, OperationPage } from './models';

@Injectable({ providedIn: 'root' })
export class BankingService {
  private readonly http = inject(HttpClient);
  accounts() { return this.http.get<Account[]>('/api/accounts'); }
  history(id: string) { return this.http.get<OperationPage>(`/api/accounts/${id}/operations`); }
  debit(id: string, amount: number, description: string) { return this.http.post<void>(`/api/accounts/${id}/debit`, { amount, description }); }
  credit(id: string, amount: number, description: string) { return this.http.post<void>(`/api/accounts/${id}/credit`, { amount, description }); }
  transfer(sourceAccountId: string, destinationAccountId: string, amount: number, description: string) {
    return this.http.post<void>('/api/accounts/transfer', { sourceAccountId, destinationAccountId, amount, description });
  }
  customers() { return this.http.get<Customer[]>('/api/admin/customers'); }
  createCustomer(name: string, email: string) { return this.http.post<Customer>('/api/admin/customers', { name, email }); }
  deleteCustomer(id: number) { return this.http.delete<void>(`/api/admin/customers/${id}`); }
}
