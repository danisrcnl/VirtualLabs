// The Error Interceptor intercepts http responses from the api to check if there were any errors.
// If there is a 401 Unauthorized response the user is automatically logged out of 
// the application, all other errors are re-thrown up to the calling service so 
// an alert with the error can be displayed on the screen

import { Component, Inject, Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthenticationService } from '../authentication.service';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';


//L'error interceptor intercetta le risposte http dall'API per verificare se ci sono stati errori


@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private authenticationService: AuthenticationService, public dialog : MatDialog) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(catchError(err => {
            if ([401, 403].indexOf(err.status) !== -1) {
                // Procede con il logout automatico se non è autorizzato (401)
                // o viene negato l'accesso alla pagina
                //this.authenticationService.logout();
               // location.reload(true);
            }

    
  
return throwError(err);
        }))
    }

    
}

