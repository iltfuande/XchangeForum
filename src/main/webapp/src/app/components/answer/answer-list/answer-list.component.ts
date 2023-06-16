import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { AnswerDto } from '../../../model/dto/answer/answer.dto';
import { AnswerService } from '../../../shared/service/answer.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';
import { PostAuthenticationService } from '../../../shared/auth/post-authentication.service';
import { UserService } from '../../../shared/service/user.service';
import { MatDialog } from '@angular/material/dialog';
import { ProfileDialogData } from '../../../model/dto/profile-dialog-data';
import { ProfileButtonComponent } from '../../profile/profile-button/profile-button.component';
import { ConfirmDialogData } from '../../../model/dto/confirm-dialog-data';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { ModerateQuestionDto } from '../../../model/dto/question/moderate-question.dto';
import { ModerateAnswerDto } from '../../../model/dto/answer/moderate-answer.dto';

@Component({
  selector: 'app-answer-list',
  templateUrl: './answer-list.component.html',
  styleUrls: ['./answer-list.component.scss'],
})
export class AnswerListComponent implements OnInit {

  @Output() deleteClick = new EventEmitter();
  answers: AnswerDto[];
  totalAnswer: number;
  currentUserId: number | undefined;
  isModerator: boolean;
  pageSize = 10;
  pageNumber = 0;
  sortBy = 'id';

  constructor(private answerService: AnswerService,
              private postAuthentication: PostAuthenticationService,
              private userService: UserService,
              private dialog: MatDialog,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.getAnswers();
    this.postAuthentication.currentUser.subscribe(user => {
      this.currentUserId = user?.id;
    });
  }

  getAnswers(): void {
    const pageable = {
      pageNumber: this.pageNumber,
      pageSize: this.pageSize,
      sortBy: this.sortBy,
    };

    this.isModerator = this.postAuthentication.isModerator();
    const questionId = this.route.snapshot.params['questionId'];
    this.answerService.getAnswers(pageable, questionId).subscribe(
      (response) => {
        this.answers = response.content;
        this.totalAnswer = response.totalElements;
      },
      (error) => console.log(error),
    );
  }

  reloadPage(): void {
    const currentUrl = this.router.url;
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(currentUrl);
    });
  }

  onPageChanged(pageEvent: PageEvent) {
    this.pageNumber = pageEvent.pageIndex;
    this.pageSize = pageEvent.pageSize;
    this.getAnswers();
  }

  updateAnswer(questionId: number, answerId: number) {
    this.router.navigate(['/question', questionId, 'answer', answerId, 'update']);
  }

  deleteAnswer(answerId: number): void {
    const confirmDialogData: ConfirmDialogData = {
      title: 'Видалення відповіді',
      message: 'Ви підтверджуєте видалення відповіді на питання?',
      confirmText: 'Так',
      cancelText: 'Ні',
    };
    this.dialog.open(ConfirmDialogComponent, {
      data: confirmDialogData,
    }).afterClosed().subscribe(data => {
      if (data) {
        this.answerService.deleteAnswer(answerId).subscribe(() => {
          this.reloadPage();
        });
      }
    });
  }

  showProfileInfo(createdUserId: number): void {
    this.userService.getUserById(createdUserId).subscribe(data => {
      const profileDialogData: ProfileDialogData = {
        title: 'Автор відповіді',
        user: data,
      };
      if (this.postAuthentication.isModerator()
        && !this.postAuthentication.isSameCurrentUserId(createdUserId)) {
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

  hide(answerId: number): void {
    const confirmDialogData: ConfirmDialogData = {
      title: 'Сховати відповідь',
      message: 'Ви дійсно хочете сховати відповідь?',
      confirmText: 'Так',
      cancelText: 'Ні',
    };
    this.dialog.open(ConfirmDialogComponent, {
      data: confirmDialogData,
    }).afterClosed().subscribe(data => {
      if (data) {
        const moderateDto: ModerateAnswerDto = { hidden: true };
        this.answerService.moderateAnswer(moderateDto, answerId).subscribe(() => {
          this.reloadPage();
        });
      }
    });
  }

  unhide(answerId: number): void {
    const confirmDialogData: ConfirmDialogData = {
      title: 'Відобразити відповідь',
      message: 'Ви дійсно хочете відобразити відповідь для всіх?',
      confirmText: 'Так',
      cancelText: 'Ні',
    };
    this.dialog.open(ConfirmDialogComponent, {
      data: confirmDialogData,
    }).afterClosed().subscribe(data => {
      if (data) {
        const moderateDto: ModerateAnswerDto = { hidden: false };
        this.answerService.moderateAnswer(moderateDto, answerId).subscribe(() => {
          this.reloadPage();
        });
      }
    });
  }
}
