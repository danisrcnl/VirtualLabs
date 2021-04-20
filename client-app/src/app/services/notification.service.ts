import { Injectable } from "@angular/core";
import { environment } from "environments/environment";
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';


const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'/*,
    'Authorization': 'my-auth-token'*/
  })
};

@Injectable({
    providedIn: 'root'
  })

  export class NotificationService {

    constructor(private http: HttpClient ) {
   }


   confirm (studentId,teamId) 
   {
      return this.http.post<any>(`${environment.apiUrlnotification}/confirm`,{studentId,teamId});
   }


   reject (studentId,teamId) 
   {
      return this.http.post<any>(`${environment.apiUrlnotification}/reject`,{studentId,teamId});
   }

  }