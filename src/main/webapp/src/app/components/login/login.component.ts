import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from '../../shared/service/authentication.service';
import { AuthenticationRequest } from '../../model/dto/user/auth/authentication-request';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { PostAuthenticationService } from '../../shared/auth/post-authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  showPassword: boolean = false;

  constructor(private fb: FormBuilder,
              private authenticationService: AuthenticationService,
              private postAuthenticationService: PostAuthenticationService,
              private snackBar: MatSnackBar,
              private router: Router) {
  }

  ngOnInit() {
    this.loginForm = this.fb.group({
      nickname: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  login(): void {
    const authenticationRequest: AuthenticationRequest = {
      nickname: this.loginForm.get('nickname')?.value.trim(),
      password: this.loginForm.get('password')?.value,
    };

    this.authenticationService.authenticate(authenticationRequest).subscribe({
      next: (data) => {
        this.postAuthenticationService.login(data);
        this.router.navigate(['']);
      },
      error: (err => {
          if (err.status === 401 && err.message === 'Bad credentials') {
            this.snackBar.open('Псевдонім або пароль невірні', 'Закрити', { duration: 5000 });
          } else if (err.status === 401 && err.message === 'User account is locked') {
            this.snackBar.open('Ваш обліковий запис забанен', 'Закрити', { duration: 5000 });
          } else if (err.status === 401 && err.message == 'User is disabled') {
            this.snackBar.open('Ваш обліковий запис видалений', 'Закрити', { duration: 5000 });
          }
        }
      ),
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
    const passwordField = document.querySelector('input[name="password"]');
    if (passwordField) {
      passwordField.setAttribute('type', this.showPassword ? 'text' : 'password');
    }
  }
}
