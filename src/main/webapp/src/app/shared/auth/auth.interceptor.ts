import { Injectable } from '@angular/core';
import { HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { PostAuthenticationService } from './post-authentication.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  private isNotLogging: boolean = false;
  private token: any;

  constructor(private postAuthenticationService: PostAuthenticationService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    this.postAuthenticationService.isLoggedIn.subscribe(isLoggedIn => {
        this.isNotLogging = !isLoggedIn;
      },
    ).unsubscribe();

    if (this.isNotLogging) {
      return next.handle(req);
    }
    this.postAuthenticationService.token.subscribe(token => this.token = token);
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${this.token}`),
    });
    return next.handle(authReq);
  }
}
