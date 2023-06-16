import { Component, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { QuestionService } from '../../../shared/service/question.service';
import { QuestionDto } from '../../../model/dto/question/question.dto';
import { Router } from '@angular/router';
import { SearchService } from '../../../shared/service/search.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-question-list',
  templateUrl: './question-list.component.html',
  styleUrls: ['./question-list.component.scss'],
})
export class QuestionListComponent implements OnInit {
  questions: QuestionDto[];
  totalQuestions: number;
  pageSize: number = 10;
  pageNumber: number = 0;
  sortBy: string = 'title';
  searchValue: string = '';
  order: string = '';

  constructor(private questionService: QuestionService,
              private router: Router,
              private searchService: SearchService) {
  }

  ngOnInit(): void {
    this.searchService.search$.subscribe(searchValue => {
      this.searchValue = searchValue;
      this.getQuestions();
    });
    this.searchService.filter$.subscribe(filterValue => {
      console.log(filterValue);
      if (filterValue === '') {
        this.sortBy = 'title';
        this.order = '';
      } else if (filterValue === 'old') {
        this.sortBy = 'timeCreated';
        this.order = 'ASC';
      } else if (filterValue === 'new') {
        this.sortBy = 'timeCreated';
        this.order = 'DESC';
      }
      if (this.searchValue === '') {
        this.getQuestions();
      }
    });
  }

  getQuestions(): void {
    const pageable = {
      pageNumber: this.pageNumber,
      pageSize: this.pageSize,
      sortBy: this.sortBy,
      searchText: this.searchValue,
      order: this.order,
    };

    this.questionService.getQuestions(pageable).subscribe(
      (response) => {
        this.questions = response.content;
        this.totalQuestions = response.totalElements;
      },
      (error) => console.log(error),
    );
  }

  openQuestion(question: QuestionDto) {
    this.router.navigate(['/question', question.id], { state: { question } });
  }

  onPageChanged(pageEvent: PageEvent) {
    this.pageNumber = pageEvent.pageIndex;
    this.pageSize = pageEvent.pageSize;
    this.getQuestions();
  }

  onTagClick(tag: string): void {
    this.searchValue = tag;
    this.getQuestions();
  }
}
