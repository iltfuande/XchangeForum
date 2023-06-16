import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from '../../shared/service/authentication.service';
import { CreateUserDto } from '../../model/dto/user/create-user.dto';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { PostAuthenticationService } from '../../shared/auth/post-authentication.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss'],
})
export class RegistrationComponent implements OnInit {

  registrationForm: FormGroup;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(private fb: FormBuilder,
              private authenticationService: AuthenticationService,
              private postAuthenticationService: PostAuthenticationService,
              private snackBar: MatSnackBar,
              private router: Router) {
  }

  ngOnInit() {
    this.registrationForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      nickname: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
      password: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(100)]],
      confirmPassword: ['', Validators.required],
    }, {
      validator: this.checkPasswords,
    });
  }

  register(): void {
    const createUserDto: CreateUserDto = {
      firstName: this.registrationForm.get('firstName')?.value.trim(),
      lastName: this.registrationForm.get('lastName')?.value.trim(),
      nickname: this.registrationForm.get('nickname')?.value.trim(),
      email: this.registrationForm.get('email')?.value.trim(),
      password: this.registrationForm.get('password')?.value.trim(),
    };

    this.authenticationService.register(createUserDto).subscribe({
      next: (response) => {
        this.postAuthenticationService.login(response);
        this.router.navigate(['']);
      }, error: (err) => {
        if (err === '400 Email is already registered') {
          this.snackBar.open('Пошта вже існує', 'Закрити', { duration: 5000 });
        } else if (err === '400 Nickname is already registered') {
          this.snackBar.open('Псевдонім вже існує', 'Закрити', { duration: 5000 });
        }
      },
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
    const passwordField = document.querySelector('input[name="password"]');
    if (passwordField) {
      passwordField.setAttribute('type', this.showPassword ? 'text' : 'password');
    }
  }

  toggleConfirmPassword(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
    const passwordField = document.querySelector('input[name="confirmPassword"]');
    if (passwordField) {
      passwordField.setAttribute('type', this.showConfirmPassword ? 'text' : 'password');
    }
  }

  private checkPasswords(group: FormGroup): void {
    let password = group.get('password')?.value;
    let confirmPassword = group.get('confirmPassword')?.value;

    return password === confirmPassword
      ? group.get('confirmPassword')?.setErrors(null)
      : group.get('confirmPassword')?.setErrors({ notSame: true });
  }
}
