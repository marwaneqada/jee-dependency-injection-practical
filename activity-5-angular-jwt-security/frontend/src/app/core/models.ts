export interface AuthResponse { accessToken: string; tokenType: string; expiresIn: number; username: string; roles: string[]; }
export interface Customer { id: number; name: string; email: string; }
export interface Account {
  id: string; type: 'CURRENT' | 'SAVING'; balance: number; createdAt: string;
  status: string; customer: Customer; overdraft: number | null; interestRate: number | null;
}
export interface Operation { id: number; operationDate: string; amount: number; type: 'DEBIT' | 'CREDIT'; description: string; }
export interface OperationPage { accountId: string; balance: number; totalElements: number; totalPages: number; operations: Operation[]; }
