import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {catchError, from, Observable, of, switchMap} from 'rxjs';
import {Customer} from './model/customer';
import {Reading} from './model/reading';

@Injectable({
  providedIn: 'root',
})
export class RestService {
  //@TODO change url later
  private baseUrl = 'http://localhost:8056/rest';
  private username = 'Test';
  private password = 'password';
  //private token = this.getAuthToken();

  constructor(private http: HttpClient) {}

  getAuthToken() {
    return this.http.get<string>(`${this.baseUrl}/auth/token/${this.username}/${this.password}`, {responseType: 'text' as 'json'});
  }

  getAuthTokenAsPromise():Promise<string>{
    return new Promise((resolve, reject) => {
      this.getAuthToken().subscribe((result:string) => {
        resolve(result);
      });
    })
}

////// Requests Customer

  getCustomer(): Observable<any> {
    return from(this.getAuthTokenAsPromise()).pipe(
      switchMap((token: string) => {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        });
        return this.http.get<any>(`${this.baseUrl}/customers`, { headers });
      }),
      catchError((err) => {
        console.error('Error in getCustomer:', err);
        return of(null);
      })
    );
  }

  createCustomer(customer:Customer): Observable<any> {
    return from(this.getAuthTokenAsPromise()).pipe(
      switchMap((token: string) => {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        });
        return this.http.post<any>(`${this.baseUrl}/customers/`, customer, { headers });
      }),
      catchError((err) => {
        console.error('Error in createCustomer:', err);
        return of(null);
      })
    );
  }

  updateCustomer(customer:Customer): Observable<any> {
    return from(this.getAuthTokenAsPromise()).pipe(
      switchMap((token: string) => {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        });
        return this.http.put<any>(`${this.baseUrl}/customers/`, customer, { headers });
      }),
      catchError((err) => {
        console.error('Error in putCustomer:', err);
        return of(null);
      })
    );
  }

  deleteCustomer(customerId:string): Observable<any> {
    return from(this.getAuthTokenAsPromise()).pipe(
      switchMap((token: string) => {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        });
        return this.http.delete<any>(`${this.baseUrl}/customers/`+customerId, { headers });
      }),
      catchError((err) => {
        console.error('Error in deleteCustomer:', err);
        return of(null);
      })
    );
  }

////// Requests Reading
  createReading(reading:Reading): Observable<any> {
    return from(this.getAuthTokenAsPromise()).pipe(
      switchMap((token: string) => {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        });
        return this.http.post<any>(`${this.baseUrl}/readings/`, reading, { headers });
      }),
      catchError((err) => {
        console.error('Error in postReading:', err);
        return of(null);
      })
    );
  }

  updateReading(reading:Reading): Observable<any> {
    return from(this.getAuthTokenAsPromise()).pipe(
      switchMap((token: string) => {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        });
        return this.http.put<any>(`${this.baseUrl}/readings/`, reading, { headers });
      }),
      catchError((err) => {
        console.error('Error in putReading:', err);
        return of(null);
      })
    );
  }

  deleteReading(readingId:string): Observable<any> {
    return from(this.getAuthTokenAsPromise()).pipe(
      switchMap((token: string) => {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        });
        return this.http.delete<any>(`${this.baseUrl}/readings/`+readingId, { headers });
      }),
      catchError((err) => {
        console.error('Error in deleteReading:', err);
        return of(null);
      })
    );
  }

  getReading(customerId:string):  Observable<any>{
    return from(this.getAuthTokenAsPromise()).pipe(
      switchMap((token: string) => {
        const headers = new HttpHeaders({
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        });

        const start = null;
        const end = null;
        const kindOfMeter = null;
        let params = new HttpParams();

        customerId != null ? params = params.set('customer', customerId) : params = params.set('customer', customerId);
        start != null ? params = params.set('start', '2022-01-01') : null;
        end != null ? params = params.set('end', '2022-12-01') : null;
        kindOfMeter != null ? params = params.set('kindOfMeter', kindOfMeter) : null;

        return this.http.get<any>(`${this.baseUrl}/readings`, {headers: headers, params: params});
      }),
      catchError((err) => {
        (<HTMLInputElement> document.getElementById("table-notification-empty")).setAttribute('style', 'display:relative');

        console.error('Error in getReading:', err);
        return of(null);
      })
    );
  }
}
