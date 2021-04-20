import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../model/vms.model';
import { VmService } from 'app/services/vm.service';
import { Observable } from 'rxjs';
import { AuthService } from 'app/auth/authservices/auth.service';
import { User } from 'app/auth/user';
import { CourseService } from 'app/services/course.service';
import { vmModelDTO } from 'app/model/vmModelDTO.model';
import { Team } from 'app/model/team.model';
import { StudentDTO } from 'app/model/studentDTO.model';

@Component({
  selector: 'app-vms-contcomponent',
  templateUrl: './vms-contcomponent.component.html',
  styleUrls: ['./vms-contcomponent.component.css']
})
export class VmsContcomponentComponent2 implements OnInit {

currentUser : User;
team$ : Observable <Team[]>;
teams : Team[] ;
team : Team;

  constructor(private route: ActivatedRoute,private authService : AuthService, private studentservice : StudentService, private router : Router,private vmService: VmService ) {

  this.authService.currentUser.subscribe ( x => {this.currentUser = x;
    console.log(this.currentUser);
      this.studentId = this.currentUser.username.split("@")[0].substring(1,7);
  
  this.studentservice.getOne(this.studentId).subscribe(
    s => {
      this.currentStudent = s;

this.route.queryParams.subscribe(params => { this.courseId = params.name
      
      
      
       this.courseId.replace('%20', " ");
       console.log (this.courseId);
       
      
       this.studentservice.getStudentCourseTeam(this.currentStudent.id,this.courseId).subscribe(
          data => {
          this.teams = data;
          this.team = this.teams[0];
       
         this.vmService.getVmsForTeam(this.team.id).subscribe ( vmss => {

      vmss.forEach ( v => {

        this.vmsperteam.push(v);
      })

    })

      }
     


    )
      
      });


    },
    error => {
      console.log("errore");
    }




  )


     
     
 
  
  
  
  
  
  ;
    
    }
    
    
    
    
    )
    
  
    
    
    ;


   }

   vmsperteam : Vms[] = new Array<Vms>();
   vms$ : Observable <Vms[]>;
   studentId : string;
   groups : Group[];
   courseId :string;
   teamId : Number;
   
   selectedegroups : Group[];
   public href :string ="";
   public href2 : string ="";
    public hreff : string ="";
        public subject : string ="";
   
   currentStudent : StudentDTO;
    firstParam : string ="";
    vmModel : vmModelDTO;
  
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

   
 
 
     
    
}
    receivevmModel($event)
    {
      this.vmModel = $event;
      console.log(this.vmModel);
    }
    
  
}
