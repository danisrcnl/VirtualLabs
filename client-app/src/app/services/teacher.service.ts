import { Injectable } from '@angular/core';
import { Student } from '../teacher/student.model';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, Subject} from 'rxjs';
import { Course } from '../model/course.model';
import { find, catchError, tap } from 'rxjs/operators';
import { Vms } from '../assets/vms.model';
import { Group } from '../model/group.model';
import { environment } from 'src/environments/environment';
import { User } from '../auth/user';
import { Studentreturn } from '../auth/models/studentreturn';
import { config } from 'src/app/config';
import { Proposal } from '../model/proposal.model';
import { Team } from '../model/team.model';
import { MemberStatus } from '../model/memberstatus.model';
import { of } from 'rxjs/internal/observable/of';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'/*,
    'Authorization': 'my-auth-token'*/
  })
};

@Injectable({
  providedIn: 'root'
})

export class TeacherService {

constructor(private http: HttpClient ) {
   }

 private refresh$ = new Subject <void>();

  get _refresh$ () {
    return this.refresh$;

  }

getCourseforTeacher(teacherId) {

   let params1 = new HttpParams().set('teacherId',teacherId);
    return this.http.get<any>(`${environment.apiUrlteacher}/${teacherId}/getCourses`,{params : params1});

 }


}