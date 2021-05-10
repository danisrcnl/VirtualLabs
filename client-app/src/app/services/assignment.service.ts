import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import {Assignment} from '../model/assignment.model';
import {Paper} from '../model/paper.model';
import {PaperStatus} from '../model/paperStatus.model';
import {PaperStatusTime} from '../model/paperStatusTime.model';
import { Observable, BehaviorSubject, Subject, throwError} from 'rxjs';
import { environment } from 'environments/environment';
import { catchError } from 'rxjs/operators';

const httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'/*,
      'Authorization': 'my-auth-token'*/
    })
  };
  
  @Injectable({
    providedIn: 'root'
  })
  export class AssignmentService {

    constructor (private http: HttpClient) {}


  

    get () {
        return this.http.get<Assignment>(`${environment.apiUrlassignments}/`);
    }

    addAssignmentToCourse (assignment: Assignment, courseName: String) {
        return this.http.post<void>(`${environment.apiUrlassignments}/${courseName}`, assignment);
    }

    setAssignmentContent (assignmentId: number, formData: FormData) {
        var file = formData;
        console.log(formData);
        console.log(formData.get("file"))
        return this.http.post<FormData>(`${environment.apiUrlassignments}/${assignmentId}/setContent`, file).pipe(catchError(this.handleError));
    }

    setPaperContent (paperId: number, image: File) {
        return this.http.post<void>(`${environment.apiUrlassignments}/paper/${paperId}`, image);
    }

    getOne (id: number) {
        return this.http.get<Assignment>(`${environment.apiUrlassignments}/${id}`);
    }

    getPaper (id: number) {
        return this.http.get<Paper>(`${environment.apiUrlassignments}/paper/${id}`);
    }

    getPaperCreator (id: number) {
        return this.http.get<String>(`${environment.apiUrlassignments}/paper/${id}/creator`);
    }

    getCourseAssignments (courseName: String) {
        return this.http.get<Assignment[]>(`${environment.apiUrlassignments}/${courseName}/getAssignments`);
    }

    addPaper (paper: Paper, courseName: String, assignmentId: number, studentId: String) {
        return this.http.post<Paper>(`${environment.apiUrlassignments}/${courseName}/${assignmentId}/${studentId}/addPaper`, paper);
    }

    getAssignmentPapers (assignmentId: number) {
        return this.http.get<Paper[]>(`${environment.apiUrlassignments}/${assignmentId}/getPapers`);
    }

    getStudentPapers (courseName: String, studentId: String) {
        return this.http.get<Paper[]>(`${environment.apiUrlassignments}/${courseName}/${studentId}/getPapers`);
    }

    lockPaper (paperId: number) {
        return this.http.get<Paper>(`${environment.apiUrlassignments}/paper/${paperId}/lock`);
    }

    readPaper (paperId: number) {
        return this.http.get<Paper>(`${environment.apiUrlassignments}/paper/${paperId}/read`);
    }

    reviewPaper (paperId: number, content: String) {
        return this.http.post<Paper>(`${environment.apiUrlassignments}/paper/${paperId}/review`, content);
    }

    deliverPaper (paperId: number) {
        return this.http.get<Paper>(`${environment.apiUrlassignments}/paper/${paperId}/deliver`);
    }

    ratePaper (paperId: number, mark: number) {
        return this.http.get<Paper>(`${environment.apiUrlassignments}/paper/${paperId}/rate/${mark}`);
    }

    setContent (paperId: number, formData: FormData) {
        var file = formData;
        return this.http.post<any>(`${environment.apiUrlassignments}/paper/${paperId}/setContent`, file).pipe(catchError(this.handleError));
    }

    getPaperHistory (paperId: number) {
        return this.http.get<PaperStatusTime[]>(`${environment.apiUrlassignments}/paper/${paperId}/getHistory`);
    }

    getPaperStudent (assignmentId: number, studentId: String) {
        return this.http.get<Paper>(`${environment.apiUrlassignments}/${assignmentId}/${studentId}/getPapersStudent`);
    }


    handleError(err) {
 
  if(err instanceof HttpErrorResponse) {

    console.log(err.error.message);
    return throwError(err.error.message);

  } else {

    return throwError(err.error.message);
    

  }
}

    




  }