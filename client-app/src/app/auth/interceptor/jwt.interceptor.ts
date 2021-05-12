import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
import { AuthenticationService } from '../authentication.service';
import { AuthService } from '../authservices/auth.service';


@Injectable()
export class JwtInterceptor implements HttpInterceptor {
    constructor(private authService : AuthService) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
      
        //Il metodo aggiunge un header di autenticazione al jwt, che verrà allegato 
        //ad ogni richiesta se l'utente è loggato
        const currentUser = this.authService.currentUserValue;
        const isLoggedIn = currentUser && currentUser.token;
        const isApiUrl = request.url.startsWith(environment.apiUrl);
        if (isLoggedIn && isApiUrl) {
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${currentUser.token}`
                }
            });
          
        }else{
       
        }
        return next.handle(request);
    }
}