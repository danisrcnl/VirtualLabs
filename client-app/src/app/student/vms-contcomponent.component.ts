import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../model/group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../model/vms.model';
import { VmService } from 'app/services/vm.service';
import { from, Observable, of } from 'rxjs';
import { AuthService } from 'app/auth/authservices/auth.service';
import { User } from 'app/auth/user';
import { CourseService } from 'app/services/course.service';
import { vmModelDTO } from 'app/model/vmModelDTO.model';
import { Team } from 'app/model/team.model';
import { StudentDTO } from 'app/model/studentDTO.model';
import { vmStatus } from 'app/model/vmStatus.model';
import { catchError } from 'rxjs/operators';
import { UsedResources } from 'app/model/UsedResources.model';
import { TeamService } from 'app/services/team.service';

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
roles : String[] = new Array<String>();
roles$ : Observable<String[]>;
usedResources : UsedResources;

constructor(
  private route: ActivatedRoute,
  private authService : AuthService, 
  private studentservice : StudentService, 
  private router : Router,
  private vmService: VmService,
  private teamService : TeamService ) {

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
             this.teams.forEach(t => 
                 {
                   if (t.status == 1) {
                       this.team = t;

                        this.vmsperteam$ = this.vmService.getVmsForTeam(t.id);
                       
                          this.vmsperteam$.subscribe(data => {
                             
                              data.forEach(t => {
                                
                                console.log(t);
                       })

                          })}})
                      
                          
                     
                     this.authService.info().subscribe(data => 
                      
                      {
                        data.roles.forEach( r => {
                            this.roles.push(r);
                        })
                      }

                      )
                      console.log(this.roles);
                      this.roles$ = of(this.roles);
                     
                      this.teamService.getUsedResources(this.courseId,this.team.name).subscribe(data => {this.usedResources = data})

                   })  });},


          error => {
            console.log("errore");
                   }

    );});}

   vmsperteam$ : Observable <Vms[]>;
   vm : Vms;
   vms$ : Observable <Vms[]>;
   studentId : string;
   groups : Group[];
   courseId :string;
   teamId : Number;
   
   selectedegroups : Group[];
   public href :string ="";
   public href2 : string ="";
    public href3 : string ="";
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
   this.href3 = this.hreff + '/elaborati';
   console.log(this.href2);
;
});

   
  this.vmService.getVmModelforCourse(this.firstParam).subscribe(data => {

    this.vmModel = data;
    console.log(this.vmModel);
   })

 
     
    
}
    receivevmModel($event)
    {
      this.vmModel = $event;
      console.log(this.vmModel);
    }
    
  
    receivevm($event)
    {
      this.vm = $event;
      this.vm.vmStatus = vmStatus.OFF;
      console.log(this.vm);
      this.vmService.addVm(this.firstParam,this.team.name,this.vm,this.currentStudent.id).subscribe(
        data1 => {console.log(data1)
         this.updatevms();
         this.authService.info().subscribe(data => 
                      
                      {
                        data.roles.forEach( r => {
                            if(r.includes("VM_"+data1))
                            this.roles.push(r);
                            console.log("VM_"+data1);
                        })
                      }

                      )
                      console.log(this.roles);
                      this.roles$ = of(this.roles);
        },
        
     
      );
      
    }

    updatevms()
    {

        this.studentservice.getStudentCourseTeam(this.currentStudent.id,this.courseId).subscribe(
          data => {
          this.teams = data;
            
              this.teams.forEach(t => 
                 {if (t.status == 1) {
                   this.team = t;

                     this.vmsperteam$ = this.vmService.getVmsForTeam(t.id);
                     this.vmsperteam$.subscribe(data => {
                       data.forEach(t => {
                         console.log(t);
                       })
                     }) 
                     
                      }
                           })

                    })
    }


    changestatevm($event)
    {
      this.vmService.changeState($event.vmId,$event.command).subscribe(data => {console.log(data)
      this.updatevms();
      
      });
      
    }
}
