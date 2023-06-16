import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SearchService {

  private searchValueSubject = new BehaviorSubject<string>('');
  private filterValueSubject = new BehaviorSubject<string>('');

  get search$(): Observable<string> {
    return this.searchValueSubject.asObservable();
  }

  setSearchValue(value: string): void {
    this.searchValueSubject.next(value);
  }

  get filter$(): Observable<string> {
    return this.filterValueSubject.asObservable();
  }

  setFilterValue(value: string): void {
    this.filterValueSubject.next(value);
  }
}
