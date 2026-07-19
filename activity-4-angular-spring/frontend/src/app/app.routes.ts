import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { ProductsComponent } from './features/products/products.component';

export const routes: Routes = [
  { path: '', component: HomeComponent, title: 'Product Desk' },
  { path: 'products', component: ProductsComponent, title: 'Products · Product Desk' },
  { path: '**', redirectTo: '' }
];
