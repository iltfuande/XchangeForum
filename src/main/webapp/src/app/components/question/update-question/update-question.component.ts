import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { QuestionService } from '../../../shared/service/question.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PostAuthenticationService } from '../../../shared/auth/post-authentication.service';
import { switchMap } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { QuestionDto } from '../../../model/dto/question/question.dto';
import { UpdateQuestionDto } from '../../../model/dto/question/update-question.dto';
import { ModerateQuestionDto } from '../../../model/dto/question/moderate-question.dto';

@Component({
  selector: 'app-update-question',
  templateUrl: './update-question.component.html',
  styleUrls: ['./update-question.component.scss'],
})
export class UpdateQuestionComponent implements OnInit, OnDestroy {

  private routeSub: Subscription;
  private questionId: number;
  isModerator: boolean = false;
  question: QuestionDto;
  questionForm: FormGroup;

  constructor(private fb: FormBuilder,
              private router: Router,
              private questionService: QuestionService,
              private route: ActivatedRoute,
              private snackBar: MatSnackBar,
              private postAuthenticationService: PostAuthenticationService) {
  }

  ngOnInit(): void {
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
  }

  ngOnDestroy() {
    this.routeSub.unsubscribe();
  }

  updateQuestionForm(): void {
    this.questionForm = this.fb.group({
      id: [this.question.id, Validators.required],
      title: [this.question.title, [Validators.required, Validators.minLength(10), Validators.maxLength(100)]],
      description: [this.question.description, [Validators.required, Validators.minLength(20), Validators.maxLength(5000)]],
      tag: this.fb.array(this.question.tag, [Validators.required]),
      hidden: [this.question.hidden],
    }, {
      validators: this.minSelectedElements,
    });
  }

  addTags(tagsInput: HTMLInputElement): void {
    const tags = tagsInput.value.split(' ');
    const existingTags = this.tags.controls.map(tag => tag.value.toLowerCase());

    let newTags: string[] = [];
    if (this.tags.controls.length < 5) {
      newTags = tags
        .filter(tag => tag.trim() && !existingTags.includes(tag.trim().toLowerCase()))
        .slice(0, 5 - this.tags.controls.length);
    }
    newTags.forEach(tag => {
      this.tags.push(this.fb.control(tag.trim(), [Validators.required]));
    });
    tagsInput.value = '';
  }

  removeTag(index: number): void {
    this.tags.removeAt(index);
  }

  get tags(): FormArray {
    return this.questionForm.get('tag') as FormArray;
  }

  updateQuestion(): void {
    const updateQuestionDto: UpdateQuestionDto = {
      title: this.questionForm.get('title')?.value.trim(),
      description: this.questionForm.get('description')?.value.trim(),
      tag: this.questionForm.get('tag')?.value,
    };

    this.questionService.updateQuestion(updateQuestionDto, this.question.id).subscribe({
      next: () => {
        this.router.navigate(['/question', this.question.id]);
      },
    });
  }

  moderateQuestion(): void {
    const updateQuestionDto: ModerateQuestionDto = {
      title: this.questionForm.get('title')?.value.trim(),
      description: this.questionForm.get('description')?.value.trim(),
      tag: this.questionForm.get('tag')?.value,
      hidden: this.questionForm.get('hidden')?.value,
    };

    this.questionService.moderateQuestion(updateQuestionDto, this.question.id).subscribe({
      next: () => {
        this.router.navigate(['/question', this.question.id]);
      },
    });
  }

  back(): void {
    this.router.navigate(['/question', this.question.id]);
  }

  private minSelectedElements(group: FormGroup): void {
    const tags = group.get('tags')?.value;
    return (tags && tags.length >= 1)
      ? group.get('tags')?.setErrors(null)
      : group.get('tags')?.setErrors({ emptyTags: true });
  }
}

