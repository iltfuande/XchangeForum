import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { QuestionService } from '../../../shared/service/question.service';
import { CreateQuestionDto } from '../../../model/dto/question/create-question.dto';

@Component({
  selector: 'app-create-question',
  templateUrl: './create-question.component.html',
  styleUrls: ['./create-question.component.scss'],
})
export class CreateQuestionComponent implements OnInit {

  questionForm: FormGroup;

  constructor(private fb: FormBuilder,
              private router: Router,
              private questionService: QuestionService) {
  }

  ngOnInit(): void {
    this.questionForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(20), Validators.maxLength(5000)]],
      tag: this.fb.array([], [Validators.required]),
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

  createQuestion(): void {
    const createQuestionDto: CreateQuestionDto = {
      title: this.questionForm.get('title')?.value.trim(),
      description: this.questionForm.get('description')?.value.trim(),
      tag: this.questionForm.get('tag')?.value,
    };

    this.questionService.createQuestion(createQuestionDto).subscribe({
      next: () => {
        this.router.navigate(['']);
      }
    })
  }

  back(): void {
    this.router.navigate(['']);
  }

  private minSelectedElements(group: FormGroup): void {
    const tags = group.get('tags')?.value;
    return (tags && tags.length >= 1)
      ? group.get('tags')?.setErrors(null)
      : group.get('tags')?.setErrors({ emptyTags: true });
  }
}
