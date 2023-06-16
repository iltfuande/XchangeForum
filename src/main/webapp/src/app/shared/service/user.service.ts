import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { UpdateUserDto } from '../../model/dto/user/update-user.dto';
import { UserDto } from '../../model/dto/user/user.dto';
import { ModerateUserDto } from '../../model/dto/user/moderate-user.dto';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private userUrl: string = '/api/v1/user';

  constructor(private apiService: ApiService) {
  }

  getUserById(questionId: number): Observable<UserDto> {
    return this.apiService.get<UserDto>(`${this.userUrl}/${questionId}`);
  }

  updateUser(model: UpdateUserDto, userId: number): Observable<UserDto> {
    return this.apiService.put<UserDto>(`${this.userUrl}/${userId}`, model);
  }

  moderateUser(model: ModerateUserDto, userId: number): Observable<UserDto> {
    return this.apiService.put<UserDto>(`${this.userUrl}/${userId}/moderate`, model);
  }

  deleteUser(userId: number): Observable<string> {
    return this.apiService.delete<string>(`${this.userUrl}/${userId}`);
  }
}
