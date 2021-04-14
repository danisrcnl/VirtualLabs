import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../assets/vms.model';

@Component({
  selector: 'app-vms-contcomponent',
  templateUrl: './vms-contcomponent.component.html',
  styleUrls: ['./vms-contcomponent.component.css']
})
export class VmsContcomponentComponent implements OnInit {

  constructor(private studentservice : StudentService, private route: ActivatedRoute, private router : Router) { 

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
   firstParam : string = "";

   
  
   ngOnInit() {

      this.firstParam = this.route.snapshot.queryParamMap.get('name');

this.route.params.subscribe (routeParams => {
this.hreff = this.router.url;
  this.subject = this.hreff.substring(0,this.hreff.lastIndexOf('?'));
  this.hreff = this.hreff.substring(0,this.hreff.lastIndexOf('/'));
  this.href = this.subject; console.log(this.href);
   this.href2 = this.hreff + '/students';
   console.log(this.href2);
;
});

    this.studentservice.getvms().subscribe(data => this.vms = data);
    this.studentservice.getgroups().subscribe (s => this.groups = s);
 
 
   
    
  }
}
