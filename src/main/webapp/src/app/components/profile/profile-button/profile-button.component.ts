import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { UserDto } from '../../../model/dto/user/user.dto';
import { ToolbarComponent } from '../../toolbar/toolbar.component';
import { UpdateProfileComponent } from '../update-profile/update-profile.component';
import { UpdateUserDto } from '../../../model/dto/user/update-user.dto';
import { UserService } from '../../../shared/service/user.service';
import { PostAuthenticationService } from '../../../shared/auth/post-authentication.service';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { ConfirmDialogData } from '../../../model/dto/confirm-dialog-data';
import { ProfileDialogData } from '../../../model/dto/profile-dialog-data';
import { ModerateUserDto } from '../../../model/dto/user/moderate-user.dto';

@Component({
  selector: 'app-profile-button',
  templateUrl: './profile-button.component.html',
  styleUrls: ['./profile-button.component.scss'],
})
export class ProfileButtonComponent {

  constructor(@Inject(MAT_DIALOG_DATA) public profileDialogData: ProfileDialogData,
              private userService: UserService,
              private dialog: MatDialog,
              private dialogRef: MatDialogRef<ToolbarComponent>,
              private postAuthentication: PostAuthenticationService,
  ) {
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  editProfile(): void {
    this.dialog.open(UpdateProfileComponent, {
      panelClass: 'dialog-class',
      data: this.profileDialogData.user,
    }).afterClosed().subscribe(data => {
      if (data) {
        const updateDto: UpdateUserDto = {};

        if (data.firstName && data.firstName !== this.profileDialogData.user.firstName) {
          updateDto.firstName = data.firstName;
        }

        if (data.lastName && data.lastName !== this.profileDialogData.user.lastName) {
          updateDto.lastName = data.lastName;
        }

        if (data.password && data.password !== '') {
          updateDto.password = data.password;
        }

        if (this.postAuthentication.isModerator()) {
          const moderateDto: ModerateUserDto = updateDto;

          if (Object.keys(moderateDto).length > 0) {
            this.userService.moderateUser(updateDto, this.profileDialogData.user.id).subscribe();
          }
        } else if (Object.keys(updateDto).length > 0) {
          this.userService.updateUser(updateDto, this.profileDialogData.user.id).subscribe(updatedUser => {
            this.postAuthentication.updateUserData(updatedUser);
            this.profileDialogData.user = updatedUser;
          });
        }
        this.closeDialog();
      }
    });
  }

  deleteProfile(): void {
    const confirmDialogData: ConfirmDialogData = {
      title: 'Видалення облікового запису',
      message: 'Ви підтверджуєте видалення облікового запису?',
      confirmText: 'Так',
      cancelText: 'Ні',
    };
    this.dialog.open(ConfirmDialogComponent, {
      data: confirmDialogData,
    }).afterClosed().subscribe(data => {
      if (data) {
        this.userService.deleteUser(this.profileDialogData.user.id).subscribe(() => {
          this.closeDialog();
          this.postAuthentication.logout();
        });
      }
    });
  }

  banUser(): void {
    const confirmDialogData: ConfirmDialogData = {
      title: 'Забанити користувача',
      message: 'Ви дійсно хочете заблокувати користувача?',
      confirmText: 'Так',
      cancelText: 'Ні',
    };
    this.dialog.open(ConfirmDialogComponent, {
      data: confirmDialogData,
    }).afterClosed().subscribe(data => {
      if (data) {
        const moderateDto: ModerateUserDto = { banned: true };
        this.userService.moderateUser(moderateDto, this.profileDialogData.user.id).subscribe();
      }
      this.closeDialog();
    });
  }

  unbanUser(): void {
    const confirmDialogData: ConfirmDialogData = {
      title: 'Розбанити користувача',
      message: 'Ви дійсно хочете розблокувати користувача?',
      confirmText: 'Так',
      cancelText: 'Ні',
    };
    this.dialog.open(ConfirmDialogComponent, {
      data: confirmDialogData,
    }).afterClosed().subscribe(data => {
      if (data) {
        const moderateDto: ModerateUserDto = { banned: false };
        this.userService.moderateUser(moderateDto, this.profileDialogData.user.id).subscribe();
      }
      this.closeDialog();
    });
  }
}
