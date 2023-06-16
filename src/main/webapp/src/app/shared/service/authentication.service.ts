import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiService } from './api.service';
import { AuthenticationRequest } from '../../model/dto/user/auth/authentication-request';
import { AuthenticationResponse } from '../../model/dto/user/auth/authentication-response';
import { CreateUserDto } from '../../model/dto/user/create-user.dto';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private authUrl: string = '/api/v1/auth';

  constructor(private apiService: ApiService) {
  }

  authenticate(model: AuthenticationRequest): Observable<AuthenticationResponse> {
    return this.apiService.post<AuthenticationResponse>(`${this.authUrl}/authenticate`, model);
  }

  register(model: CreateUserDto): Observable<AuthenticationResponse> {
    return this.apiService.post<AuthenticationResponse>(`${this.authUrl}/register`, model);
  }
}
