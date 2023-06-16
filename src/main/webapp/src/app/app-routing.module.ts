import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { CreateQuestionComponent } from './components/question/create-question/create-question.component';
import { QuestionListComponent } from './components/question/question-list/question-list.component';
import { QuestionDetailComponent } from './components/question/question-detail/question-detail.component';
import { UpdateQuestionComponent } from './components/question/update-question/update-question.component';
import { AuthGuard } from './shared/auth/auth.guard';
import { UpdateAnswerComponent } from './components/answer/update-answer/update-answer.component';

const routes: Routes = [
  { path: '', component: QuestionListComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'question/add', component: CreateQuestionComponent },
  { path: 'question/:questionId', component: QuestionDetailComponent },
  { path: 'question/:questionId/update', component: UpdateQuestionComponent, canActivate: [AuthGuard] },
  { path: 'question/:questionId/answer/:answerId/update', component: UpdateAnswerComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {
}
