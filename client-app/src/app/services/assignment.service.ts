import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import {Assignment} from '../model/assignment.model';
import {Paper} from '../model/paper.model';
import {PaperStatus} from '../model/paperStatus.model';
import {PaperStatusTime} from '../model/paperStatusTime.model';
import { Observable, BehaviorSubject, Subject} from 'rxjs';
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
  export class AssignmentService {

    constructor (private http: HttpClient) {}

    get () {
        return this.http.get<Assignment>(`${environment.apiUrlassignments}/`);
    }

    addAssignmentToCourse (assignment: Assignment, courseName: String) {
        return this.http.post<void>(`${environment.apiUrlassignments}/${courseName}`, assignment);
    }

    setAssignmentContent (assignmentId: number, image: File) {
        return this.http.post<void>(`${environment.apiUrlassignments}/${assignmentId}`, image);
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

    setContent (paperId: number, content: String) {
        return this.http.post<Paper>(`${environment.apiUrlassignments}/paper/${paperId}/lock`, content);
    }

    getPaperHistory (paperId: number) {
        return this.http.get<PaperStatusTime[]>(`${environment.apiUrlassignments}/paper/${paperId}/getHistory`);
    }

    getPaperStudent (assignmentId: number, studentId: String) {
        return this.http.get<Paper>(`${environment.apiUrlassignments}/${assignmentId}/${studentId}/getPapersStudent`);
    }



    




  }