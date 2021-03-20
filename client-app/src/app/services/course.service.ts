import { Injectable } from '@angular/core';
import { Student } from '../teacher/student.model';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, Subject} from 'rxjs';
import { Course } from '../model/course.model';
import { find, catchError, tap } from 'rxjs/operators';
import { Vms } from '../assets/vms.model';
import { Group } from '../model/group.model';
import { User } from '../auth/user';
import { Studentreturn } from '../auth/models/studentreturn';
import { Proposal } from '../model/proposal.model';
import { CourseDTO } from '../model/courseDTO.model';
import { StudentDTO} from '../model/studentDTO.model';
import { environment } from 'environments/environment';




const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'/*,
    'Authorization': 'my-auth-token'*/
  })
};

@Injectable({
  providedIn: 'root'
})
export class CourseService {

private _url: string = "http://localhost:4000/students";
private _url2: string = "http://localhost:4000/courses";
private _url3 : string = "http://localhost:4000/vms";
private _url4 : string = "http://localhost:4000/groups";
private _url5 : string = "http://localhost:4000/proposals";


 getAllCourses() {

  return this.http.get<CourseDTO[]>(`${environment.apiUrlcourse}/`);

 }

 getenrolledStudents(name : string) {

 let params1 = new HttpParams().set('name',name);
 return this.http.get<StudentDTO[]>(`${environment.apiUrlcourse}/${name}/enrolled`,{params : params1});
 }

 addCourse (courseDTO,teacherId) {

  // let params1 = new HttpParams().set('courseDTO',courseDTO).append('teacherId',teacherId);
  return this.http.post<any>(`${environment.apiUrlcourse}/`,{courseDTO,teacherId});
 }






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

editname (course: Course,i: string) : Observable <Course>
{
  console.log(course.name);


  const req = this.http.put<Course>(this._url2 + "/" + i,course,httpOptions)
  .pipe(
    tap(() => {
      this._refresh$.next();
    })
  );
  req.subscribe();



return;
}




createcourse(course : Course)
{

  const req = this.http.post <Course> (this._url2,course,httpOptions)
  .pipe(
    tap(() => {
      this._refresh$.next();
    })
  );
  req.subscribe();

  console.log (course);

}

deletecourse (course : Course, i: string)
{
  {
    console.log(course.name);


    const req = this.http.delete<Course>(this._url2 + "/" + i,httpOptions)
    .pipe(
      tap(() => {
        this._refresh$.next();
      })
    );
    req.subscribe();

  return;
  }

}



}
