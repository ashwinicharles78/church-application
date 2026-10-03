export interface PaymentVoucher {
  serialNumber: string;
  voucherDate: string;
  approvedAmount: number;
  amountInWords: string;
  purpose: string;
  payee: string;
  billNumber: string | null;
  billDate: string | null;
  paymentMethod: string;
  paymentReferenceNumber: string | null;
  paymentReferenceDate: string | null;
  totalBillsAmount: number;
  advanceAmount: number;
  netAmount: number;
  paymentDate: string;
  receiptMethod: string | null;
  receiptReferenceNumber: string | null;
  receiptDate: string | null;
  receiptAmount: number | null;
  paidBy: string | null;
  createdAt: string;
}

export interface PaymentVoucherRequest {
  voucherDate: string;
  approvedAmount: number;
  amountInWords: string;
  purpose: string;
  payee: string;
  billNumber: string | null;
  billDate: string | null;
  paymentMethod: 'CASH' | 'CHEQUE';
  paymentReferenceNumber: string | null;
  paymentReferenceDate: string | null;
  totalBillsAmount: number;
  advanceAmount: number;
  paymentDate: string;
  receiptMethod: 'CASH' | 'CHEQUE' | null;
  receiptReferenceNumber: string | null;
  receiptDate: string | null;
  receiptAmount: number | null;
  paidBy: string | null;
}

export interface PaymentVoucherPage {
  content: PaymentVoucher[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}