import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ToolbarComponent } from './components/toolbar/toolbar.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SearchBarComponent } from './components/search-bar/search-bar.component';
import { LoginComponent } from './components/login/login.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { OverlayModule } from '@angular/cdk/overlay';
import { MatCardModule } from '@angular/material/card';
import { QuestionListComponent } from './components/question/question-list/question-list.component';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { DateAgoPipe } from './shared/pipe/date-ago.pipe';
import { AuthenticationService } from './shared/service/authentication.service';
import { ApiService } from './shared/service/api.service';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { BackgroundComponent } from './components/background/background.component';
import { CreateQuestionComponent } from './components/question/create-question/create-question.component';
import { AuthInterceptor } from './shared/auth/auth.interceptor';
import { QuestionService } from './shared/service/question.service';
import { MatPaginatorModule } from '@angular/material/paginator';
import { QuestionDetailComponent } from './components/question/question-detail/question-detail.component';
import { UpdateQuestionComponent } from './components/question/update-question/update-question.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { CreateAnswerComponent } from './components/answer/create-answer/create-answer.component';
import { UpdateAnswerComponent } from './components/answer/update-answer/update-answer.component';
import { AnswerListComponent } from './components/answer/answer-list/answer-list.component';
import { AnswerService } from './shared/service/answer.service';
import { MatMenuModule } from '@angular/material/menu';
import { ProfileButtonComponent } from './components/profile/profile-button/profile-button.component';
import { MatDialogModule } from '@angular/material/dialog';
import { UpdateProfileComponent } from './components/profile/update-profile/update-profile.component';
import { UserService } from './shared/service/user.service';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { FooterComponent } from './components/footer/footer.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SearchService } from './shared/service/search.service';
import { MatSelectModule } from '@angular/material/select';

@NgModule({
  declarations: [
    AppComponent,
    ToolbarComponent,
    SearchBarComponent,
    LoginComponent,
    RegistrationComponent,
    QuestionListComponent,
    DateAgoPipe,
    BackgroundComponent,
    CreateQuestionComponent,
    QuestionDetailComponent,
    UpdateQuestionComponent,
    CreateAnswerComponent,
    UpdateAnswerComponent,
    AnswerListComponent,
    ProfileButtonComponent,
    UpdateProfileComponent,
    ConfirmDialogComponent,
    FooterComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatIconModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    OverlayModule,
    MatCardModule,
    MatTableModule,
    MatChipsModule,
    HttpClientModule,
    RouterModule.forRoot([]),
    MatPaginatorModule,
    MatCheckboxModule,
    MatMenuModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatSelectModule,
  ],
  providers: [
    MatSnackBar,
    ApiService,
    AuthenticationService,
    QuestionService,
    AnswerService,
    UserService,
    SearchService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
}
