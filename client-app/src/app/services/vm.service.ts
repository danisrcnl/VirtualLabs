import { Component, Inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpEvent, HttpHandler, HttpRequest } from '@angular/common/http';
import { User } from '../auth/user';
import { Vms } from 'app/model/vms.model';
import { environment } from 'environments/environment';
import { vmModelDTO } from 'app/model/vmModelDTO.model';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SubjectdialogComponent } from 'app/teacher/subjectdialog/subjectdialog.component';





@Injectable({ providedIn: 'root' })
export class VmService {
    constructor(private http: HttpClient, private dialog: MatDialog) {}


    addVm(courseName,teamName,vmDTO : Vms,creator) {

        return this.http.post<any>(`${environment.apiUrlvms}/${courseName}/${teamName}`,{vmDTO,creator})
        .pipe(catchError(this.handleError));
    }
 
    editVm(vm) {
      return this.http.post<any>(`${environment.apiUrlvms}/setResources`,vm).pipe(catchError(this.handleError));
    }
    
    getVmsByCourse (courseName) {

        return this.http.get<Vms[]>(`${environment.apiUrlvms}/courses/${courseName}`);
    }

    setVmModel (vmModel:vmModelDTO,courseName)
    {
        return this.http.post<any>(`${environment.apiUrlvms}/courses/${courseName}/setVmModel`,vmModel).pipe(catchError(this.handleError));
    }

    getVmModelforCourse (courseName)
    {
         return this.http.get<vmModelDTO>(`${environment.apiUrlvms}/courses/${courseName}/getVmModelOfCourse`);
    
        }


    getVmsForTeam (teamId) : Observable<Vms[]>
    {
        return this.http.get<any>(`${environment.apiUrlvms}/teams/${teamId}`);
    }


    changeState(vmId,command)
    {
        return this.http.get<any>(`${environment.apiUrlvms}/${vmId}/changeState/${command}`);
    }

    handleError(err) {
 
     if(err instanceof HttpErrorResponse) {

         return throwError(err.error.message);

  } else {

         return throwError(err.error.message);
    

  }
}
    
}

@Component({
  selector: 'your-dialog',
  template: '{{ data.name }}',
})
export class ErrorDialog {
 
  constructor(@Inject(MAT_DIALOG_DATA) public data: {name: string}) { }
}
