import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { CreateQuestionDto } from '../../model/dto/question/create-question.dto';
import { QuestionDto } from '../../model/dto/question/question.dto';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { QuestionListDto } from '../../model/dto/question/question-list.dto';
import { UpdateQuestionDto } from '../../model/dto/question/update-question.dto';
import { ModerateQuestionDto } from '../../model/dto/question/moderate-question.dto';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {

  private questionUrl: string = '/api/v1/question'

  constructor(private apiService: ApiService) { }

  getQuestionById(questionId: number): Observable<QuestionDto> {
    return this.apiService.get<QuestionDto>(`${this.questionUrl}/${questionId}`);
  }

  getQuestions(pageable: { pageNumber: number; pageSize: number; sortBy: string; searchText: string; order: string; }): Observable<QuestionListDto> {
    let params = new HttpParams()
      .set('pageNumber', pageable.pageNumber.toString())
      .set('pageSize', pageable.pageSize.toString())
      .set('sortBy', pageable.sortBy)
      .set('order', pageable.order)
      .set('searchText', pageable.searchText);

    return this.apiService.get<QuestionListDto>(this.questionUrl, params);
  }

  createQuestion(model: CreateQuestionDto): Observable<QuestionDto> {
    return this.apiService.post<QuestionDto>(this.questionUrl, model);
  }

  updateQuestion(model: UpdateQuestionDto, questionId: number): Observable<QuestionDto> {
    return this.apiService.put<QuestionDto>(`${this.questionUrl}/${questionId}`, model);
  }

  moderateQuestion(model: ModerateQuestionDto, questionId: number): Observable<QuestionDto> {
    return this.apiService.put<QuestionDto>(`${this.questionUrl}/${questionId}/moderate`, model);
  }

  canUserAnswer(questionId: number): Observable<boolean> {
    return this.apiService.get<boolean>(`${this.questionUrl}/${questionId}/answer`);
  }

  deleteQuestion(questionId: number): Observable<string> {
    return this.apiService.delete<string>(`${this.questionUrl}/${questionId}`);
  }
}
