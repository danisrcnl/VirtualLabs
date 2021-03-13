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
export class VmsContcomponentComponent implements OnInit {

  constructor(private studentservice : StudentService, private router : Router) { 

    this.hreff = router.url;
    this.subject = this.hreff.substring(this.hreff.lastIndexOf('/')+1 );
    console.log(this.subject);
    this.href = '/teacher/course/'+ this.subject;
     this.href2 = this.href ;
  }

   
   vms : Vms[];
   groups : Group[];
   selectedegroups : Group[];
   public hreff : string ="";
   public href :string ="";
   public href2 : string ="";
   public subject : string ="";

   
  
   ngOnInit() {

    this.studentservice.getvms().subscribe(data => this.vms = data);
    this.studentservice.getgroups().subscribe (s => this.groups = s);
 
 
   
    
  }
}
