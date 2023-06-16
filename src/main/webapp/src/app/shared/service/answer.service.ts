import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { AnswerDto } from '../../model/dto/answer/answer.dto';
import { AnswerListDto } from '../../model/dto/answer/answer-list.dto';
import { CreateAnswerDto } from '../../model/dto/answer/create-answer.dto';
import { ModerateAnswerDto } from '../../model/dto/answer/moderate-answer.dto';
import { UpdateAnswerDto } from '../../model/dto/answer/update-answer.dto';

@Injectable({
  providedIn: 'root',
})
export class AnswerService {

  private answerUrl: string = '/api/v1/answer';

  constructor(private apiService: ApiService) {
  }

  getAnswerById(answerId: number): Observable<AnswerDto> {
    return this.apiService.get<AnswerDto>(`${this.answerUrl}/${answerId}`);
  }

  getAnswers(pageable: { pageNumber: number; pageSize: number; sortBy: string }, questionId: number): Observable<AnswerListDto> {
    let params = new HttpParams()
      .set('pageNumber', pageable.pageNumber.toString())
      .set('pageSize', pageable.pageSize.toString())
      .set('sortBy', pageable.sortBy)
      .set('questionId', questionId);
    return this.apiService.get<AnswerListDto>(this.answerUrl, params);
  }

  createAnswer(model: CreateAnswerDto): Observable<AnswerDto> {
    return this.apiService.post<AnswerDto>(this.answerUrl, model);
  }

  updateAnswer(model: UpdateAnswerDto, answerId: number): Observable<AnswerDto> {
    return this.apiService.put<AnswerDto>(`${this.answerUrl}/${answerId}`, model);
  }

  moderateAnswer(model: ModerateAnswerDto, answerId: number): Observable<AnswerDto> {
    return this.apiService.put<AnswerDto>(`${this.answerUrl}/${answerId}/moderate`, model);
  }

  deleteAnswer(answerId: number): Observable<string> {
    return this.apiService.delete<string>(`${this.answerUrl}/${answerId}`);
  }
}
