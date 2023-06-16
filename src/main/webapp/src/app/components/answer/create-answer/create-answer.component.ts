import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AnswerService } from '../../../shared/service/answer.service';
import { CreateAnswerDto } from '../../../model/dto/answer/create-answer.dto';

@Component({
  selector: 'app-create-answer',
  templateUrl: './create-answer.component.html',
  styleUrls: ['./create-answer.component.scss'],
})
export class CreateAnswerComponent implements OnInit {

  answerForm: FormGroup;
  hiddenCreation: boolean = false;
  @Input() questionId!: number;

  constructor(private fb: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private answerService: AnswerService) {
  }

  ngOnInit(): void {
    this.answerForm = this.fb.group({
      questionId: [this.questionId, Validators.required],
      respond: ['', [Validators.required, Validators.minLength(20), Validators.maxLength(5000)]],
    });
  }

  createAnswer(): void {
    const createAnswerDto: CreateAnswerDto = {
      questionId: this.answerForm.get('questionId')?.value,
      respond: this.answerForm.get('respond')?.value.trim(),
    };

    this.answerService.createAnswer(createAnswerDto).subscribe({
      next: () => this.reloadPage(),
    });
  }

  reloadPage(): void {
    const currentUrl = this.router.url;
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigateByUrl(currentUrl);
    });
  }

  hide(): void {
    this.hiddenCreation = true;
  }

  unhide(): void {
    this.hiddenCreation = false;
  }
}
