import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ErrorModel } from '../../model/dto/error/error-model';

@Injectable({
  providedIn: 'root',
})
export class ApiService {

  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {
  }

  get<T>(url: string, params?: HttpParams): Observable<T> {
    return this.http.get<T>(this.baseUrl + url, { params })
      .pipe(catchError(this.handleError.bind(this)));
  }

  delete<T>(url: string, params?: {}): Observable<T> {
    return this.http.delete<T>(this.baseUrl + url, { params })
      .pipe(catchError(this.handleError.bind(this)));
  }

  post<T>(url: string, data?: {}, params?: {}): Observable<T> {
    return this.http.post<T>(this.baseUrl + url, data, { params })
      .pipe(catchError(this.handleError.bind(this)));
  }

  put<T>(url: string, data?: {}, params?: {}): Observable<T> {
    return this.http.put<T>(this.baseUrl + url, data, { params })
      .pipe(catchError(this.handleError.bind(this)));
  }

  private handleError(error: { error: ErrorModel; status: any; message: any; }) {
    let result = error.error;
    console.dir(result, error);

    if (error.error instanceof ErrorEvent) {
      // eslint-disable-next-line no-console
      console.log(`Client error occured ${JSON.stringify(result)}`);
    } else {
      // eslint-disable-next-line no-console
      console.log(`Serve responsed with status ${error.status}. Body: ${JSON.stringify(error)}`);
      if (!error.error) {
        result = error;
      }
    }
    return throwError(result);
  }
}
