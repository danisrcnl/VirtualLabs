import { Injectable } from '@angular/core';
import { Student } from '../teacher/student.model';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, Subject} from 'rxjs';
import { Course } from '../model/course.model';
import { find, catchError, tap } from 'rxjs/operators';
import { Vms } from '../model/vms.model';
import { Group } from '../model/group.model';
import { environment } from 'environments/environment';
import { User } from '../auth/user';
import { Studentreturn } from '../auth/models/studentreturn';
import { config } from 'app/config';
import { Proposal } from '../model/proposal.model';
import { CourseDTO } from '../model/courseDTO.model';
import { StudentDTO } from 'app/model/studentDTO.model';




const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'/*,
    'Authorization': 'my-auth-token'*/
  })
};

@Injectable({
  providedIn: 'root'
})
export class StudentService {

private _url: string = "http://localhost:4000/students";
private _url2: string = "http://localhost:4000/courses";
private _url3 : string = "http://localhost:4000/vms";
private _url4 : string = "http://localhost:4000/groups";
private _url5 : string = "http://localhost:4000/proposals";


courses$ : Observable<Course[]>;
private courseSubject :Subject<Course[]>;

  constructor(private http: HttpClient ) {

    this.courseSubject = new Subject<Course[]>();
    this.courses$ = this.courseSubject.asObservable();
    this.courses$ = this.http.get<Course[]>(this._url2);
   }

  private refresh$ = new Subject <void>();

  get _refresh$ () {
    return this.refresh$;

  }

  getAll() {
    return this.http.get<Studentreturn[]>(`${environment.apiUrlstudent}`);
  }



  replacebyname (courses : Course[], name : string, newname: string)
  {
    for (var i in courses) {

     if (courses[i].name == name)

        courses[i].name == newname;

    }
  }




  public create(s: Student): Observable<Student> {
    return this.http.post<Student>(this._url, s, httpOptions);
  }


getOne (studentId) : Observable <StudentDTO>
{
  return this.http.get<any>(`${environment.apiUrlstudent}/${studentId}`);
}


createproposalteam(groupname,students,timeout) {
return this.http.post<any>(`${config.apiUrl}/proposeteam`, {groupname,students,timeout})

}

getStudentCourses (studentId) : Observable <CourseDTO[]>{

  let params1 = new HttpParams().set('studentId',studentId);
  return this.http.get<any>(`${environment.apiUrlstudent}/${studentId}/getCourses`);
 }


getStudentTeams (studentId)
{
  return this.http.get<any>(`${environment.apiUrlstudent}/${studentId}/getTeams`);
}

getStudentCourseTeam (studentId,courseId)
{
  return this.http.get<any>(`${environment.apiUrlstudent}/${studentId}/${courseId}/getTeam`);
}


getVmsTeam (teamId)
{
  return this.http.get<any>(`${environment.apiUrlvms}/teams/${teamId}`);
}

getproposals() : Observable <Proposal[]>
{
return this.http.get<Proposal[]>(`${config.apiUrl}/getproposals`);
}

getproposalsprovisional() : Observable <Proposal[]>
{
  return this.http.get<Proposal[]>(this._url5);
}

getcourse() : Observable <Course[]>
{
  return this.http.get<Course[]>(this._url2);
}



getenrolledStudents() : Observable <Student[]>
{

  return this.http.get<Student[]>(this._url);

}


getstudents(): Observable <Student[]>
{

  return this.http.get<Student[]>(this._url);
}

getvms() : Observable <Vms[]>
{

  return this.http.get<Vms[]>(this._url3);
}



getgroups() : Observable<Group[]>
{
  return this.http.get<Group[]>(this._url4);
}



}
