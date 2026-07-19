export interface Product {
  id: number;
  name: string;
  price: number;
  selected: boolean;
}

export type NewProduct = Omit<Product, 'id'>;
