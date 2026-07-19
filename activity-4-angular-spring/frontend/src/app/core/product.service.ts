import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { NewProduct, Product } from './product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/products';

  findAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.endpoint);
  }

  create(product: NewProduct): Observable<Product> {
    return this.http.post<Product>(this.endpoint, product);
  }

  updateSelected(id: number, selected: boolean): Observable<Product> {
    const params = new HttpParams().set('value', selected);
    return this.http.put<Product>(`${this.endpoint}/${id}/selected`, null, { params });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpoint}/${id}`);
  }
}
