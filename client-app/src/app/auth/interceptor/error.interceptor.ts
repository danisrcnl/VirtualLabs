// The Error Interceptor intercepts http responses from the api to check if there were any errors.
// If there is a 401 Unauthorized response the user is automatically logged out of 
// the application, all other errors are re-thrown up to the calling service so 
// an alert with the error can be displayed on the screen

import { Component, Inject, Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthenticationService } from '../authentication.service';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';



@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private authenticationService: AuthenticationService, public dialog : MatDialog) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(catchError(err => {
            if ([401, 403].indexOf(err.status) !== -1) {
                // auto logout if 401 Unauthorized or 403 Forbidden response returned from api
                this.authenticationService.logout();
                location.reload(true);
            }

             if ([409].indexOf(err.status) !== -1) {
                // auto logout if 401 Unauthorized or 403 Forbidden response returned from api
               // window.alert(err.error.message);

              let dialogRef = this.dialog.open(YourDialog, {
                            data: { name: err.error.message },
                                });
                
;            }

            const error = err.error.message || err.statusText;
            return throwError(error);
        }))
    }

    
}


@Component({
  selector: 'your-dialog',
  template: '{{ data.name }}',
})
export class YourDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: {name: string}) { }
}