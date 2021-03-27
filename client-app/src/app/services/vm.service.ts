import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../auth/user';
import { Vms } from 'app/assets/vms.model';
import { environment } from 'environments/environment';



@Injectable({ providedIn: 'root' })
export class VmService {
    constructor(private http: HttpClient) { }


    getVmsByCourse (courseName) {

        return this.http.get<Vms[]>(`${environment.apiUrlvms}/courses/${courseName}`);
    }
}