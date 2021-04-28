import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpHandler, HttpRequest } from '@angular/common/http';
import { User } from '../auth/user';
import { Vms } from 'app/model/vms.model';
import { environment } from 'environments/environment';
import { vmModelDTO } from 'app/model/vmModelDTO.model';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';




@Injectable({ providedIn: 'root' })
export class VmService {
    constructor(private http: HttpClient) { }


    addVm(courseName,teamName,vmDTO : Vms,creator) {

        return this.http.post<any>(`${environment.apiUrlvms}/${courseName}/${teamName}`,{vmDTO,creator})
        .pipe(catchError(err=>{
        throw 'error in source. Details: ' + err;
    }));
    }

 
    getVmsByCourse (courseName) {

        return this.http.get<Vms[]>(`${environment.apiUrlvms}/courses/${courseName}`);
    }

    setVmModel (vmModel:vmModelDTO,courseName)
    {
        return this.http.post<any>(`${environment.apiUrlvms}/courses/${courseName}/setVmModel`,vmModel);
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
}