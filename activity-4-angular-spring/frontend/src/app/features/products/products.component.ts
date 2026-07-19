import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { Product } from '../../core/product.model';
import { ProductService } from '../../core/product.service';

@Component({
  selector: 'app-products',
  imports: [DecimalPipe],
  templateUrl: './products.component.html',
  styleUrl: './products.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductsComponent implements OnInit {
  private readonly productService = inject(ProductService);

  readonly products = signal<Product[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly pendingIds = signal<ReadonlySet<number>>(new Set());
  readonly selectedCount = computed(() => this.products().filter(product => product.selected).length);

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading.set(true);
    this.error.set('');
    this.productService.findAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: products => this.products.set(products),
        error: () => this.error.set('The API could not be reached. Confirm that the backend is running on port 8080.')
      });
  }

  toggleSelected(product: Product): void {
    if (this.isPending(product.id)) return;

    this.markPending(product.id, true);
    this.error.set('');
    this.productService.updateSelected(product.id, !product.selected)
      .pipe(finalize(() => this.markPending(product.id, false)))
      .subscribe({
        next: updated => this.products.update(products =>
          products.map(current => current.id === updated.id ? updated : current)),
        error: () => this.error.set(`Could not update “${product.name}”.`)
      });
  }

  deleteProduct(product: Product): void {
    if (this.isPending(product.id) || !confirm(`Delete “${product.name}”?`)) return;

    this.markPending(product.id, true);
    this.error.set('');
    this.productService.delete(product.id)
      .pipe(finalize(() => this.markPending(product.id, false)))
      .subscribe({
        next: () => this.products.update(products =>
          products.filter(current => current.id !== product.id)),
        error: () => this.error.set(`Could not delete “${product.name}”.`)
      });
  }

  isPending(id: number): boolean {
    return this.pendingIds().has(id);
  }

  private markPending(id: number, pending: boolean): void {
    this.pendingIds.update(ids => {
      const next = new Set(ids);
      pending ? next.add(id) : next.delete(id);
      return next;
    });
  }
}
