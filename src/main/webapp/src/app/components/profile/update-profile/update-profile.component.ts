import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserDto } from '../../../model/dto/user/user.dto';

@Component({
  selector: 'app-update-profile',
  templateUrl: './update-profile.component.html',
  styleUrls: ['./update-profile.component.scss'],
})
export class UpdateProfileComponent {

  editProfileForm: FormGroup;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<UpdateProfileComponent>,
    @Inject(MAT_DIALOG_DATA) public user: UserDto,
  ) {
    this.editProfileForm = this.fb.group({
      firstName: [user.firstName, [Validators.required, Validators.minLength(2)]],
      lastName: [user.lastName, [Validators.required, Validators.minLength(2)]],
      password: ['', [Validators.minLength(5)]],
      confirmPassword: [''],
      banned: [user.banned]
    }, {
      validator: this.checkPasswords,
    });
  }

  saveChanges(): void {
    if (this.editProfileForm.valid) {
      this.dialogRef.close(this.editProfileForm.value);
    }
  }

  closeDialog(): void {
    this.dialogRef.close();
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
