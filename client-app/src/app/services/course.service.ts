import { Injectable } from '@angular/core';
import { Student } from '../teacher/student.model';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, Subject} from 'rxjs';
import { Course } from '../model/course.model';
import { find, catchError, tap } from 'rxjs/operators';
import { Vms } from '../model/vms.model';
import { Group } from '../model/group.model';
import { User } from '../auth/user';
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




 getAllCourses() {

  return this.http.get<CourseDTO[]>(`${environment.apiUrlcourse}/`);

 }

 getenrolledStudents(name : string) {

 let params1 = new HttpParams().set('name',name);
 return this.http.get<StudentDTO[]>(`${environment.apiUrlcourse}/${name}/enrolled`,{params : params1});
 }

 addCourse (courseDTO: CourseDTO, teacherId) {

  // let params1 = new HttpParams().set('courseDTO',courseDTO).append('teacherId',teacherId);
  courseDTO.enabled = true;
  return this.http.post<any>(`${environment.apiUrlcourse}/`,{courseDTO,teacherId});
 }

 getAvailableStudents(courseName : string) : Observable <StudentDTO[]>
 {
   return this.http.get<any>(`${environment.apiUrlcourse}/${courseName}/getAvailableStudents`);
 }

 getfreeStudents(name: string) 
 {
return this.http.get<StudentDTO[]>(`${environment.apiUrlcourse}/${name}/notEnrolled`);
 }

 enrollOne (name,studentDTO)
 {
    return this.http.post<any>(`${environment.apiUrlcourse}/${name}/enrollOne`,studentDTO);
 }
 
 deleteOne(name,studentId)
 {
   return this.http.get<any>(`${environment.apiUrlcourse}/${name}/${studentId}/evict`);
 }

 deleteCourse(name)
 {
      return this.http.get<any>(`${environment.apiUrlcourse}/${name}/delete`);
 }


 setMin (coursename,value)
 {
return this.http.get<any>(`${environment.apiUrlcourse}/${coursename}/setMin/${value}`);
 }

 setMax (coursename,value)
 {
return this.http.get<any>(`${environment.apiUrlcourse}/${coursename}/setMax/${value}`);
 }

  setEnabled (coursename,value)
 {
   value = true;
return this.http.get<any>(`${environment.apiUrlcourse}/${coursename}/setEnabled/${value}`);
 }

 
 getOne (name)
 {
   return this.http.get<any>(`${environment.apiUrlcourse}/${name}`);
 }

courses$ : Observable<Course[]>;
private courseSubject :Subject<Course[]>;

 constructor(private http: HttpClient ) {

   }

  private refresh$ = new Subject <void>();

  get _refresh$ () {
    return this.refresh$;

  }



deletecourse (course : Course, i: string)
{

}



}
