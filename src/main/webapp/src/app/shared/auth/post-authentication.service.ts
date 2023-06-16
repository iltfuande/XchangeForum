import { Injectable } from '@angular/core';
import { UserDto } from '../../model/dto/user/user.dto';
import { AuthenticationResponse } from '../../model/dto/user/auth/authentication-response';
import { Role } from '../../model/enum/role.enum';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PostAuthenticationService {

  private _isLoggedIn = new BehaviorSubject<boolean>(false);
  private _currentUserId = new BehaviorSubject<number | undefined>(undefined);
  private _currentUser = new BehaviorSubject<UserDto | undefined>(undefined);
  private _token = new BehaviorSubject<string | undefined>(undefined);
  private _tokenTimeExpiration = new BehaviorSubject<Date | undefined>(undefined);
  private _role = new BehaviorSubject<Role | undefined>(undefined);

  get isLoggedIn(): Observable<boolean> {
    return this._isLoggedIn.asObservable();
  }

  get token(): Observable<string | undefined> {
    return this._token.asObservable();
  }

  get currentUser(): Observable<UserDto | undefined> {
    return this._currentUser.asObservable();
  }

  constructor() {
    this.loginFromMemory();
  }

  isModerator() {
    return Role.Moderator === this._role.value;
  }

  isUser() {
    return Role.User === this._role.value;
  }

  isViewer() {
    return this._role.value !== undefined;
  }

  isSameCurrentUserId(userId: number) {
    return this._currentUserId.value === userId;
  }

  login(authResult: AuthenticationResponse): boolean {
    if (!authResult || !authResult.token) return false;

    localStorage.setItem('user', JSON.stringify(authResult));
    this._isLoggedIn.next(true);
    this._currentUser.next(authResult.userDto);
    this._currentUserId.next(authResult.userDto.id);
    this._token.next(authResult.token);
    this._tokenTimeExpiration.next(new Date(authResult.tokenTimeExpiration));
    this._role.next(authResult.role);
    return true;
  }

  logout(): void {
    localStorage.removeItem('user');
    this._isLoggedIn.next(false);
    this._currentUser.next(undefined);
    this._currentUserId.next(undefined);
    this._token.next(undefined);
    this._tokenTimeExpiration.next(undefined);
    this._role.next(undefined);
  }

  updateUserData(userDto: UserDto): void {
    const authResponse = localStorage.getItem('user');
    if (authResponse) {
      this.currentUser.subscribe(user => {
          if (user != userDto) {
            const user = JSON.parse(authResponse);
            user.userDto = userDto;
            localStorage.setItem('user', JSON.stringify(user));
            this._currentUser.next(userDto);
          }
        },
      );
    }
  }

  private loginFromMemory() {
    const userData = localStorage.getItem('user');

    if (userData) {
      const user = JSON.parse(userData);

      this._isLoggedIn.next(true);
      this._currentUser.next(user.userDto);
      this._currentUserId.next(user.userDto.id);
      this._token.next(user.token);
      this._tokenTimeExpiration.next(new Date(user.tokenTimeExpiration));
      this._role.next(user.role);
    }
  }
}
