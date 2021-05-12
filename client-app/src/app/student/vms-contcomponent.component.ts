import { Component, Inject, Input, OnInit } from '@angular/core';
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
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';

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
usedResources$ : Observable <UsedResources>;

constructor(
  private route: ActivatedRoute,
  private authService : AuthService, 
  private studentservice : StudentService, 
  private router : Router,
  private vmService: VmService,
  private teamService : TeamService,
  private dialog : MatDialog ) {

  this.authService.currentUser.subscribe ( x => {this.currentUser = x;
   
    this.studentId = this.currentUser.username.split("@")[0].substring(1,7);

      
  this.studentservice.getOne(this.studentId).subscribe(
    s => {
      this.currentStudent = s;

       this.route.queryParams.subscribe(params => { 
         
         this.courseId = params.name;
         
         this.courseId.replace('%20', " ");
       
            this.studentservice.getStudentCourseTeam(this.currentStudent.id,this.courseId).subscribe(

               data => {

                 this.teams = data;
                   
                   this.teams.forEach(t => {
                   
                    if (t.status == 1) {
                    
                      this.team = t;

                      this.vmsperteam$ = this.vmService.getVmsForTeam(t.id);
                       
                      this.vmsperteam$.subscribe(data => {
                             
                        data.forEach(t => {
                                
                              
                       })
                      })
                    }
                  })
                      
                    this.authService.info().subscribe(data => {
                      
                         data.roles.forEach( r => {
                            
                          this.roles.push(r);
                        })})
                     
                      this.roles$ = of(this.roles);
                     
                      this.usedResources$ = this.teamService.getUsedResources(this.courseId,this.team.name);

                   })  });},


              error => {
           
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
   href :string ="";
   href2 : string ="";
   href3 : string ="";
   hreff : string ="";
   subject : string ="";
   
   currentStudent : StudentDTO;
   firstParam : string ="";
   vmModel : vmModelDTO;
  
   ngOnInit() {

   this.firstParam = this.route.snapshot.queryParamMap.get('name');
   this.route.params.subscribe (routeParams => {
   this.hreff = this.router.url;
   this.subject = this.hreff.substring(0,this.hreff.lastIndexOf('?'));
   this.hreff = this.hreff.substring(0,this.hreff.lastIndexOf('/'));
   this.href = this.subject; 
   this.href2 = this.hreff + '/students';
   this.href3 = this.hreff + '/elaborati';
   
;
});

   
  this.vmService.getVmModelforCourse(this.firstParam).subscribe(data => {

    this.vmModel = data;
    
   })}
    
  
    //Ricevo vm da aggiungere dal componente figlio 
    receivevm($event)
    {
      this.vm = $event;
      this.vm.vmStatus = vmStatus.OFF;
      
      this.vmService.addVm(this.firstParam,this.team.name,this.vm,this.currentStudent.id).subscribe(
        data1 => {
         this.updatevms();
         this.authService.info().subscribe(data => 
                      
                      {
                        data.roles.forEach( r => {
                            if(r.includes("VM_"+data1))
                             this.roles.push(r);     
                        })
                      }

                      )
                      
                      this.roles$ = of(this.roles);
        },

        (error) => {

                        let dialogRef = this.dialog.open(YourDialog, {
                        data: { name: error },
                                });
          

        });
      
    }

    //Ricevo vm da modificare
    receiveeditvm($event) {

     this.vmService.editVm($event).subscribe( 
       data => { this.updatevms();}
       ,
       (error) => {
         let dialogRef = this.dialog.open(YourDialog, { data: { name: error }});
       });
      
      }

    //Aggiorno la vista delle vm con i nuovi parametri 
    updatevms()
    {

        this.studentservice.getStudentCourseTeam(this.currentStudent.id,this.courseId).subscribe(
          data => {
          this.teams = data;
            
              this.teams.forEach(t =>{
                if (t.status == 1) {
                   this.team = t;

                     this.vmsperteam$ = this.vmService.getVmsForTeam(t.id);
                     this.vmsperteam$.subscribe(data => {
                       data.forEach(t => {
                         
                       })
                     }) 
                     
                   }
                 })

             this.usedResources$ = this.teamService.getUsedResources(this.courseId,this.team.name);

             })
    }


    //Cambio lo stato della vm
    changestatevm($event)
    {

      this.vmService.changeState($event.vmId,$event.command).subscribe(data => {
      this.updatevms();
      
      });
      
    }
}

@Component({
  selector: 'your-dialog',
  template: '{{ data.name }}',
})
export class YourDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: {name: string}) { }
}