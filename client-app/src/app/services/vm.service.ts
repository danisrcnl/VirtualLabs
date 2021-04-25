import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../auth/user';
import { Vms } from 'app/model/vms.model';
import { environment } from 'environments/environment';
import { vmModelDTO } from 'app/model/vmModelDTO.model';



@Injectable({ providedIn: 'root' })
export class VmService {
    constructor(private http: HttpClient) { }


    addVm(courseName,teamName,vmDTO : Vms,creator) {

        return this.http.post<any>(`${environment.apiUrlvms}/${courseName}/${teamName}`,{vmDTO,creator});
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


    getVmsForTeam (teamId)
    {
        return this.http.get<Vms[]>(`${environment.apiUrlvms}/teams/${teamId}`);
    }
}