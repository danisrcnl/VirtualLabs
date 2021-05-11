import { Injectable } from '@angular/core';
import { Student } from '../teacher/student.model';
import { HttpClient, HttpParams, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, Subject, throwError} from 'rxjs';
import { Course } from '../model/course.model';
import { find, catchError, tap } from 'rxjs/operators';
import { Vms } from '../model/vms.model';
import { Group } from '../model/group.model';
import { environment } from 'environments/environment';
import { User } from '../auth/user';
import { Studentreturn } from '../auth/models/studentreturn';
import { config } from 'app/config';
import { Proposal } from '../model/proposal.model';
import { Team } from '../model/team.model';
import { MemberStatus } from '../model/memberstatus.model';
import { of } from 'rxjs/internal/observable/of';
import { StudentDTO } from 'app/model/studentDTO.model';
import { UsedResources } from 'app/model/UsedResources.model';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'/*,
    'Authorization': 'my-auth-token'*/
  })
};

@Injectable({
  providedIn: 'root'
})


export class TeamService {


    constructor(private http: HttpClient ) {
   }

 addTeam(courseName,teamName,memberIds,hours,creator) {

   return this.http.post<any>(`${environment.apiUrlteam}/${courseName}/add`,{courseName,teamName,memberIds,hours,creator}).pipe(catchError(this.handleError));
 }


getMembers(courseName,teamName)
 
 {
     return this.http.get<any>(`${environment.apiUrlteam}/${courseName}/${teamName}/membersStatus`);
 }



 getMembersStatus(courseName :string, teamName :string) : Observable <MemberStatus[]>
 
 {
     let params1 = new HttpParams().set('coursename',courseName).append('teamname',teamName);
     return this.http.get<any>(`${environment.apiUrlteam}/getMembersStatus`, {params : params1});
 }

 getUsedResources (courseName: string, teamName: String): Observable<UsedResources> {
    return this.http.get<any>(`${environment.apiUrlteam}/${courseName}/${teamName}/getUsedResources`);
 }


  handleError(err) {
 
  if(err instanceof HttpErrorResponse) {

    return throwError(err.error.message);

  } else {

    return throwError(err.error.message);
    

  }
}


}