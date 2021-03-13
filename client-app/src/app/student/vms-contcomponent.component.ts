import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../assets/vms.model';

@Component({
  selector: 'app-vms-contcomponent',
  templateUrl: './vms-contcomponent.component.html',
  styleUrls: ['./vms-contcomponent.component.css']
})
export class VmsContcomponentComponent2 implements OnInit {

  constructor(private studentservice : StudentService, private router : Router) { }

  vms : Vms[];
   groups : Group[];
   selectedegroups : Group[];
   public href :string ="";
   public href2 : string ="";

   
  
   ngOnInit() {

    this.studentservice.getvms().subscribe(data => this.vms = data);
    this.studentservice.getgroups().subscribe (s => this.groups = s);
 
 

    this.href = this.router.url;
    this.href2 = this.href;
    
  }
}
