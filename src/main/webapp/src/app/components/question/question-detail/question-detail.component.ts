import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { QuestionDto } from '../../../model/dto/question/question.dto';
import { QuestionService } from '../../../shared/service/question.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PostAuthenticationService } from '../../../shared/auth/post-authentication.service';
import { switchMap } from 'rxjs/operators';
import { ConfirmDialogData } from '../../../model/dto/confirm-dialog-data';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../../shared/service/user.service';
import { ProfileButtonComponent } from '../../profile/profile-button/profile-button.component';
import { ProfileDialogData } from '../../../model/dto/profile-dialog-data';
import { ModerateQuestionDto } from '../../../model/dto/question/moderate-question.dto';
import { SearchService } from '../../../shared/service/search.service';

@Component({
  selector: 'app-question-detail',
  templateUrl: './question-detail.component.html',
  styleUrls: ['./question-detail.component.scss'],
})
export class QuestionDetailComponent implements OnInit, OnDestroy {

  private routeSub: Subscription;
  private questionId: number;
  currentUserId: number | undefined;
  canUserAnswer: boolean = false;
  isModerator: boolean = false;
  question: QuestionDto;
  questionForm: FormGroup;

  constructor(private questionService: QuestionService,
              private userService: UserService,
              private route: ActivatedRoute,
              private router: Router,
              private fb: FormBuilder,
              private dialog: MatDialog,
              private snackBar: MatSnackBar,
              private postAuthenticationService: PostAuthenticationService,
              private searchService: SearchService) {
  }

  ngOnInit(): void {
    this.postAuthenticationService.currentUser.subscribe(user =>
      this.currentUserId = user?.id,
    );
    this.routeSub = this.route.paramMap.pipe(
      switchMap((params) => {
        const questionId = params.get('questionId');
        if (questionId !== null) {
          this.questionId = parseInt(questionId, 10);
        }
        return this.questionService.getQuestionById(this.questionId);
      }),
    ).subscribe(
      (questionDto) => {
        this.question = questionDto;
        this.isModerator = this.postAuthenticationService.isModerator();
        this.updateQuestionForm();
      },
      () => {
        this.snackBar.open('Питання не знайдено', 'Закрити', {
          duration: 5000,
        });
        this.router.navigate(['']);
      },
    );
    this.questionService.canUserAnswer(this.questionId).subscribe(data =>
      this.canUserAnswer = data,
    );
  }

  ngOnDestroy(): void {
    this.routeSub.unsubscribe();
  }

  updateQuestionForm(): void {
    this.questionForm = new FormGroup({
      id: new FormControl(this.question.id),
      createdUserId: new FormControl(this.question.createdUserId),
      moderatorId: new FormControl(this.question.moderatorId),
      close: new FormControl(this.question.close),
      title: new FormControl(this.question.title),
      description: new FormControl(this.question.description),
      tag: new FormControl(this.question.tag),
      viewed: new FormControl(this.question.viewed),
      timeCreated: new FormControl(this.question.timeCreated),
      timeModify: new FormControl(this.question.timeModify),
      hidden: new FormControl(this.question.hidden),
    });
  }

  onTagClick(tag: string): void {
    this.searchService.setSearchValue(tag);
    this.router.navigate(['']);
  }

  updateQuestion(): void {
    this.router.navigate(['/question', this.question.id, 'update']);
  }

  deleteQuestion(): void {
    const confirmDialogData: ConfirmDialogData = {
      title: 'Видалення питання',
      message: 'Ви підтверджуєте видалення питання?',
      confirmText: 'Так',
      cancelText: 'Ні',
    };
    this.dialog.open(ConfirmDialogComponent, {
      data: confirmDialogData,
    }).afterClosed().subscribe(data => {
      if (data) {
        this.questionService.deleteQuestion(this.question.id).subscribe(() => {
          this.router.navigate(['']);
        });
      }
    });
  }

  canAnswer(): boolean {
    return this.question !== undefined && this.canUserAnswer;
  }

  showProfileInfo(): void {
    this.userService.getUserById(this.question.createdUserId).subscribe(data => {
      const profileDialogData: ProfileDialogData = {
        title: 'Автор питання',
        user: data,
      };
      if (this.postAuthenticationService.isModerator()
        && !this.postAuthenticationService.isSameCurrentUserId(this.question.createdUserId)) {
        profileDialogData.banText = 'Забанити';
        profileDialogData.unbanText = 'Розбанити';
        profileDialogData.moderateText = 'Змінити';
      }
      this.dialog.open(ProfileButtonComponent, {
        panelClass: 'dialog-class',
        data: profileDialogData,
      });
    });
  }

  hide(): void {
    const confirmDialogData: ConfirmDialogData = {
      title: 'Сховати питання',
      message: 'Ви дійсно хочете сховати питання?',
      confirmText: 'Так',
      cancelText: 'Ні',
    };
    this.dialog.open(ConfirmDialogComponent, {
      data: confirmDialogData,
    }).afterClosed().subscribe(data => {
      if (data) {
        const moderateDto: ModerateQuestionDto = { hidden: true };
        this.questionService.moderateQuestion(moderateDto, this.question.id).subscribe(() => {
          this.reloadPage();
        });
      }
    });
  }

  unhide(): void {
    const confirmDialogData: ConfirmDialogData = {
      title: 'Відобразити питання',
      message: 'Ви дійсно хочете відобразити питання для всіх?',
      confirmText: 'Так',
      cancelText: 'Ні',
    };
    this.dialog.open(ConfirmDialogComponent, {
      data: confirmDialogData,
    }).afterClosed().subscribe(data => {
      if (data) {
        const moderateDto: ModerateQuestionDto = { hidden: false };
        this.questionService.moderateQuestion(moderateDto, this.question.id).subscribe(() => {
          this.reloadPage();
        });
      }
    });
  }

  reloadPage(): void {
    const currentUrl = this.router.url;
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(currentUrl);
    });
  }
}
