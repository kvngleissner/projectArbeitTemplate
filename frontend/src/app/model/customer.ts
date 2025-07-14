export interface Customer {
  id: String;
  gender?: 'D' | 'M' | 'U' | 'W';
  lastName?: String;
  firstName?: String;
  birthDate?: Date;
}
