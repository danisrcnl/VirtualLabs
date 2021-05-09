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


   confirm (teamId,studentId) 
   {
      
      return this.http.get<any>(`${environment.apiUrlnotification}/confirm/${teamId}/${studentId}`);
   }


   reject (teamId,studentId) 
   {
      return this.http.get<any>(`${environment.apiUrlnotification}/reject/${teamId}/${studentId}`);
   }

  }