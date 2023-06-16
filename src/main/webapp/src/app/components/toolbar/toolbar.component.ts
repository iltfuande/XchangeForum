import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { PostAuthenticationService } from '../../shared/auth/post-authentication.service';
import { MatDialog } from '@angular/material/dialog';
import { ProfileButtonComponent } from '../profile/profile-button/profile-button.component';
import { UserDto } from '../../model/dto/user/user.dto';
import { ProfileDialogData } from '../../model/dto/profile-dialog-data';

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.scss'],
})
export class ToolbarComponent {

  isLoggedIn$ = this.postAuthenticationService.isLoggedIn;
  currentUser$ = this.postAuthenticationService.currentUser;
  user: UserDto | undefined;

  constructor(private router: Router,
              private dialog: MatDialog,
              private postAuthenticationService: PostAuthenticationService) {
  }

  onHome(): void {
    this.router.navigate(['/']);
  }

  showProfileInfo(): void {
    this.currentUser$.subscribe(data => {
      this.user = data;
    });
    const profileDialogData: ProfileDialogData = {
      title: 'Обліковий запис',
      user: this.user!,
      editText: 'Змінити',
      deleteText: 'Видалити',
    };

    this.dialog.open(ProfileButtonComponent, {
      panelClass: 'dialog-class',
      data: profileDialogData,
    });
  }

  logout(): void {
    this.postAuthenticationService.logout();
  }

  ask(): void {
    this.router.navigate(['/question/add']);
  }
}

