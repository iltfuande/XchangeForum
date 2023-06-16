import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { PostAuthenticationService } from './post-authentication.service';
import { QuestionService } from '../service/question.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AnswerService } from '../service/answer.service';
import { UserDto } from '../../model/dto/user/user.dto';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {

  private QUESTION: string = 'question';
  private ANSWER: string = 'answer';
  private UPDATE: string = 'update';
  private currentUser: UserDto | undefined;

  constructor(private postAuthenticationService: PostAuthenticationService,
              private questionService: QuestionService,
              private answerService: AnswerService,
              private router: Router,
              private snackBar: MatSnackBar) {
  }

  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    this.postAuthenticationService.currentUser.subscribe(user => {
      this.currentUser = user;
    });
    if (this.currentUser === undefined) {
      this.snackBar.open('Будь ласка, увійдіть на сайт або створіть обліковий запис', 'Закрити', { duration: 5000 });
      this.router.navigate(['/login']);
      return false;
    }

    const isModerator = this.postAuthenticationService.isModerator();
    if (isModerator) {
      return true;
    }

    const url = route.url;
    if (url[0].path === this.QUESTION && url[2].path === this.UPDATE) {
        const canAccess = await this.checkQuestion(parseInt(url[1].path), this.currentUser?.id);
        if (canAccess) {
          return true;
        }
    }
    if (url[0].path === this.QUESTION && url[2].path === this.ANSWER && url[4].path === this.UPDATE) {
      const canAccess = await this.checkAnswer(parseInt(url[3].path), this.currentUser?.id);
      if (canAccess) {
        return true;
      }
    }

    this.snackBar.open('Ви не маєте доступу сюди', 'Закрити', { duration: 5000 });
    this.router.navigate(['']);
    return false;
  }

  private async checkQuestion(questionId: number, userId: number | undefined): Promise<boolean> {
    const question = await this.questionService.getQuestionById(questionId).toPromise();
    return question.id === questionId && question.createdUserId === userId;
  }

  private async checkAnswer(answerId: number, userId: number | undefined): Promise<boolean> {
    const answer = await this.answerService.getAnswerById(answerId).toPromise();
    return answer.id === answerId && answer.userId === userId;
  }
}
