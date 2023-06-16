import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PostAuthenticationService } from '../../../shared/auth/post-authentication.service';
import { switchMap } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { AnswerDto } from '../../../model/dto/answer/answer.dto';
import { AnswerService } from '../../../shared/service/answer.service';
import { UpdateAnswerDto } from '../../../model/dto/answer/update-answer.dto';
import { ModerateAnswerDto } from '../../../model/dto/answer/moderate-answer.dto';

@Component({
  selector: 'app-update-answer',
  templateUrl: './update-answer.component.html',
  styleUrls: ['./update-answer.component.scss'],
})
export class UpdateAnswerComponent implements OnInit, OnDestroy {

  private routeSub: Subscription;
  private questionId: number;
  private answerId: number;
  isModerator: boolean = false;
  answer: AnswerDto;
  answerForm: FormGroup;

  constructor(private fb: FormBuilder,
              private router: Router,
              private answerService: AnswerService,
              private route: ActivatedRoute,
              private snackBar: MatSnackBar,
              private postAuthenticationService: PostAuthenticationService) {
  }

  ngOnInit(): void {
    this.routeSub = this.route.paramMap.pipe(
      switchMap((params) => {
        const questionId = params.get('questionId');
        const answerId = params.get('answerId');
        if (questionId !== null && answerId !== null) {
          this.questionId = parseInt(questionId, 10);
          this.answerId = parseInt(answerId, 10);
        }
        return this.answerService.getAnswerById(this.answerId);
      }),
    ).subscribe(
      (answerDto) => {
        this.answer = answerDto;
        this.isModerator = this.postAuthenticationService.isModerator();
        this.updateAnswerForm();
      },
      () => {
        this.snackBar.open('Відповіді не знайдено', 'Закрити', {
          duration: 5000,
        });
        this.back();
      },
    );
  }

  ngOnDestroy(): void {
    this.routeSub.unsubscribe();
  }

  updateAnswerForm(): void {
    this.answerForm = this.fb.group({
      id: [this.answer.id, Validators.required],
      respond: [this.answer.respond, [Validators.required, Validators.minLength(20), Validators.maxLength(5000)]],
      hidden: [this.answer.hidden],
    });
  }

  updateAnswer(): void {
    const updateAnswerDto: UpdateAnswerDto = {
      respond: this.answerForm.get('respond')?.value.trim(),
    };

    this.answerService.updateAnswer(updateAnswerDto, this.answerId).subscribe({
      next: () => {
        this.router.navigate(['/question', this.questionId]);
      },
    });
  }

  moderateAnswer(): void {
    const updateAnswerDto: ModerateAnswerDto = {
      respond: this.answerForm.get('respond')?.value.trim(),
      hidden: this.answerForm.get('hidden')?.value,
    };
    console.log(updateAnswerDto.hidden);
    this.answerService.moderateAnswer(updateAnswerDto, this.answerId).subscribe({
      next: () => {
        this.router.navigate(['/question', this.questionId]);
      },
    });
  }

  back(): void {
    this.router.navigate(['/question', this.questionId]);
  }
}

