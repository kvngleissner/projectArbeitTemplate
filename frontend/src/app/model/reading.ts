import {Customer} from './customer';

export interface Reading {
  id?: string;
  customer: Customer;
  kindOfMeter: 'WATER' | 'POWER' | 'HEAT' | 'UNKNOWN';
  meterId: string;
  meterCount: number;
  substitute: boolean;
  dateOfReading: Date;
  comment: string;
}
