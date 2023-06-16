import { Component } from '@angular/core';
import { SearchService } from '../../shared/service/search.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.scss'],
})
export class SearchBarComponent {

  searchValue: string = '';
  selectedFilter: string = '';

  constructor(private searchService: SearchService,
              private router: Router) {
  }

  onSearch(): void {
    this.router.navigate(['']);
    this.searchService.setSearchValue(this.searchValue);
  }

  applyFilter(filter: string) {
    this.router.navigate(['']);
    this.searchService.setFilterValue(filter);
  }

  clean(): void {
    this.router.navigate(['']);
    this.searchService.setSearchValue('');
    this.searchValue = '';
  }
}
