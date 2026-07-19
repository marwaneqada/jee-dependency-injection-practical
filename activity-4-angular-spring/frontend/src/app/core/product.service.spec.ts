import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ProductService } from './product.service';

describe('ProductService', () => {
  let service: ProductService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ProductService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads products from the REST API', () => {
    service.findAll().subscribe(products => expect(products.length).toBe(1));

    const request = http.expectOne('/api/products');
    expect(request.request.method).toBe('GET');
    request.flush([{ id: 1, name: 'Monitor', price: 2500, selected: false }]);
  });

  it('updates selection with the expected query parameter', () => {
    service.updateSelected(7, true).subscribe(product => expect(product.selected).toBeTrue());

    const request = http.expectOne(candidate =>
      candidate.url === '/api/products/7/selected' && candidate.params.get('value') === 'true');
    expect(request.request.method).toBe('PUT');
    request.flush({ id: 7, name: 'Keyboard', price: 600, selected: true });
  });

  it('deletes a product by id', () => {
    service.delete(3).subscribe();

    const request = http.expectOne('/api/products/3');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });
});
