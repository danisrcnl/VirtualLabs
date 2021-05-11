import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { config } from 'app/config';
import { User } from '../user';


@Injectable({ providedIn: 'root' })
export class AuthService {
    private currentUserSubject: BehaviorSubject<User>;
    public currentUser: Observable<User>;

    constructor(private http: HttpClient) {
        this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('currentUser')));
        this.currentUser = this.currentUserSubject.asObservable();
    }

    public get currentUserValue(): User {
        return this.currentUserSubject.value;
    }

    login(username, password) {
        return this.http.post<any>(`${config.apiUrl}/auth/signin`, { username, password })
            .pipe(map(user => {
                if (user && user.token) {
                   
                // store user details and jwt token in local storage to keep user logged in between page refreshes
                localStorage.setItem('currentUser', JSON.stringify(user));
          
                this.currentUserSubject.next(user);
            }
                return user;
            }));
    }

    logout() {
        // remove user from local storage and set current user to null
        
        localStorage.removeItem('currentUser');

        this.currentUserSubject.next(null);

    }

    signup(firstName,lastName,id,email,password) {

        return this.http.post<any>(`${config.apiUrl}/auth/signup`, {id,firstName,lastName,email,password}).pipe(catchError(this.handleError));
    }


    info() {

    return this.http.get<any>(`${config.apiUrl}/me`);

    }

    
    handleError(err) {
 
  if(err instanceof HttpErrorResponse) {

    return throwError(err.error.message);

  } else {

    return throwError(err.error.message);
    

  }
}


}
