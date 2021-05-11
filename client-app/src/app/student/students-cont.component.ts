import { Component, OnInit, Input, ViewChild, SimpleChange, SimpleChanges} from '@angular/core';

import { Routes, RouterModule, Router } from '@angular/router';
import { PageNotFoundComponentComponent } from '../page-not-found-component/page-not-found-component.component';

import { StudentsComponent } from './students.component';
import { VmsContcomponentComponent2 } from './vms-contcomponent.component';
import {MatTableDataSource} from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import {StudentService} from 'app/services/student.service'
import {FormControl, Validators} from '@angular/forms';
import { Course } from '../model/course.model';
import { ActivatedRoute } from '@angular/router';
import { from, Observable, of } from 'rxjs';
import { Student } from './student.model';
import { Proposal } from '../model/proposal.model';
import { Team } from '../model/team.model';
import { Student_Team } from '../model/student_team.model';
import { MemberStatus } from '../model/memberstatus.model';
import { TeamService } from '../services/team.service';
import { CourseService } from '../services/course.service';
import { StudentDTO } from '../model/studentDTO.model';
import { AuthService } from '../auth/authservices/auth.service';
import { User } from '../auth/user';
import { first } from 'rxjs/operators';
import { ThrowStmt } from '@angular/compiler';
import { NotificationService } from 'app/services/notification.service';
import { MatDialog } from '@angular/material/dialog';
import { Popup } from './popup/popup.component';

 export interface DialogPopup{

  animal:string;
  name: string;
 }


@Component({
  selector: 'app-students-cont',
  templateUrl: './students-cont.component.html',
  styleUrls: ['./students-cont.component.css']
})
export class StudentsContComponent implements OnInit {

  teamsinconstruction : Team[] = new Array<Team>();
  teamsinconstruction$ : Observable<Team[]>;
  studenti: StudentDTO[] = new Array<StudentDTO>();
  compagni: StudentDTO[] = new Array<StudentDTO>();
  tabvalue : boolean = false;
  tabvalue$ : Observable<boolean>;
 
  proposals : Proposal[] = new Array <Proposal>();
  enrolledstudents : StudentDTO[] = new Array<StudentDTO>();
  studenteaggiunto : StudentDTO;
  darimuovere : StudentDTO[] =new Array<StudentDTO>();
  
  dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
  selection = new SelectionModel<Student>(true, []);
  courses$ : Observable <Course[]>;
  membersarray : MemberStatus[] = new Array<MemberStatus>();
  student_team : Student_Team[] = new Array<Student_Team>();
  id2: number;
  groupid : number;
  courseid : number;
  href : string ="";
  courses: Course[];
  courseName : String = "";
  
  //variabili dell'User
  currentUser : User;
  studentId : String;
  //variabili per invito Team 
  
  dainvitare : StudentDTO[] = new Array<StudentDTO>();
  groupName : String = "";
  timeoutValue : number;
  error : '';

  //variabili per la conferma del Team
  matricola : String;

  //variabili per caricare la tabella con i componenti del gruppo 
  compagnicorso : MemberStatus[] = new Array <MemberStatus>();
  compagniDTO : StudentDTO[] = new Array<StudentDTO>();
  compagnidigruppo$ : Observable<MemberStatus[]>;


  //variabili per caricare la tabella con le proposte di Team
  studentid : string;
  students : Student[] = new Array<Student>(); //da qua ricavo poi nome e cognome degli studenti  
  membersStatus : MemberStatus[] = new Array<MemberStatus>();
  membersStatus$ : Observable<MemberStatus[]>;
  //da qua controllo se lo studente ha accettato o ancora no la proposta 
  tempmember : MemberStatus;
  teams : Team[] = new Array<Team>();
  count : number = 0;
  courseId :string;
  teams$ : Observable <Team[]>;
  teams2 : Team[] = new Array<Team>();
  teamName : String = "";
  ms : MemberStatus = new MemberStatus();
  creator$ : Observable <StudentDTO>;
  creator : StudentDTO;
  currentStudent$ : Observable<StudentDTO>;
  currentStudent : StudentDTO;
  tempstudent : StudentDTO;
  @ViewChild(StudentsComponent)
  studentsComponent: StudentsComponent
  minstud : any;
  maxstud : any;
  
  name: string;



  constructor (private courseService : CourseService,private notificationService: NotificationService, private route: ActivatedRoute, 
    private studentservice : StudentService, private teamservice : TeamService, private router: Router,
     private activeRoute: ActivatedRoute, private authService: AuthService,public dialog: MatDialog) {
    

    //prendo l'username dell'user loggato 
this.route.queryParams.subscribe(data => {
    
  this.teams = [];
  this.courseId = "";
  this.teams2 = [];
  this.students = [];
  this.membersStatus = [];
  this.compagnicorso = [];
  this.compagniDTO = [];
  this.dainvitare = [];
  this.groupName = "";
  this.membersarray = [];
  this.teamsinconstruction=[];
  this.tabvalue = false;
  this.compagni = [];







    this.authService.currentUser.subscribe ( x => {

      this.currentUser = x;
      this.studentId = this.currentUser.username.split("@")[0].substring(1,7);
  
        this.studentservice.getOne(this.studentId).subscribe(
         s => {
           this.currentStudent = s;
              },
         error => {
        
                  }
        );
    
        });

    
    
  
    
    this.activeRoute.paramMap.subscribe(params => {


      this.href = this.router.url;
      let id = 0;
      
      this.studenti = [];
      this.enrolledstudents = [];
        
      
     //chiamata alla funzione 

       this.route.queryParams.subscribe(params => { this.courseId = params.name
       this.courseId.replace('%20', " ");
       
       });

      

      });
    
              
    
        
        this.courseService.getAvailableStudents(this.courseId).subscribe(receivedstudents=>{

            this.courseService.getOne(this.courseId).subscribe(data => {

              this.maxstud = data.max;
              this.minstud = data.min;

           receivedstudents.forEach(s => {

          
            if (s.id!=this.studentId) 
             this.enrolledstudents.push(s);
         
                 this.dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
                 this.studentsComponent.updateFilteredOptions();
  
          }) 
        
        });
         });
 
        //prendo i team dello studente 

      
        
        this.teamsinconstruction$ = this.studentservice.getStudentCourseTeam(this.studentId,this.courseId);
           this.teamsinconstruction$.subscribe (data => {
                
               })

        
        this.updateteamstatus();

              

});
  };




ngOnChanges (changes: SimpleChanges)
{


}
  

  ngOnInit() {   

    this.authService.currentUser.subscribe(u => {
    
      this.studentid = u.username;

    })

  
  
this.studentid = this.studentid.substring(0,7);


    this.courseService._refresh$.subscribe(()=> {

      this.courses$ = this.studentservice.getcourse();
    });
  
    this.courses$ = this.studentservice.getcourse();


  }

  



    
  

  receivestudent($event) {
    this.studenteaggiunto = $event;
    if (!this.enrolledstudents.includes(this.studenteaggiunto)){
    this.enrolledstudents.push(this.studenteaggiunto);

    this.enrolledstudents = Object.assign( this.enrolledstudents);
    this.studenti.forEach(item => {
      let index: number = this.studenti.findIndex(d => d === item);
      this.studenti.splice(index,1)});}
  
    this.dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
    
  }

  receivearray ($event) {
    this.darimuovere = $event;
    this.darimuovere.forEach(item => {
      let index: number = this.enrolledstudents.findIndex(d => d === item);

      this.enrolledstudents.splice(index,1);
      if (!this.studenti.includes(item))
      this.studenti.push(item);});
      this.dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
  }


  receiveconfirmmatricola($event) {

    this.matricola = $event;
    
  }

  receiveconfirmteamid ($event)
  {
    
    let teamid = $event;
    let acceptedteam;
    
   this.openDialog("Attendi..", );

    this.notificationService.confirm(teamid,this.matricola).subscribe(
      
      data => { 

        this.closeDialog();
        this.openDialog2("La proposta è stata accettata!");
    
       var teamm : Team;
    var teamarray : Team[] = new Array<Team>();
       this.studentservice.getStudentCourseTeam(this.studentId,this.courseId).
        subscribe (teamss => {
          teamss.forEach ( t => {

            if(t.id==teamid){ 
        
           teamarray.push(t);}
            if(t.status==1){
            this.teamName = t.name;
            this.tabvalue = true;
            this.tabvalue$ = of(this.tabvalue);
            this.compagnidigruppo$ = this.teamservice.getMembers(this.courseId,this.teamName);
            }
            
          })
  
     
    let membstat  : MemberStatus[] = new Array<MemberStatus>();

     this.teamsinconstruction$ = of (teamarray);
   
           this.teamservice.getMembers(this.courseId,teamarray[0].name).subscribe(members => {

           
               members.forEach (m => {
                
                this.tempmember = m;
                this.tempmember.teamid = teamid;
              
                membstat.push(this.tempmember);
                this.tempmember = null;
             
              
           })
          
           membstat.forEach(m => {

            this.studentservice.getOne(m.studentId).subscribe(
              s=> {
                let indexitem = membstat.find(e => e.studentId == s.id);
                if (indexitem)
                {
                  this.tempmember = membstat.find(e => e.studentId == s.id);
                  this.tempmember.firstName = s.firstName;
                  this.tempmember.lastName = s.name;
      
                }  
              }
            )

           })
          this.compagnidigruppo$ = of(membstat);
    
          })
     
 


  }
 
  )
        
   }
   ,

  error => {
     this.closeDialog();
     this.openDialog2("La proposta è stata accettata!!");

    var teamm : Team;
    var teamarray : Team[] = new Array<Team>();
       this.studentservice.getStudentCourseTeam(this.studentId,this.courseId).
        subscribe (teamss => {
          teamss.forEach ( t => {

            if(t.id==teamid){ 
           
           teamarray.push(t);}
            if(t.status==1){
            this.teamName = t.name;
            this.tabvalue = true;
            this.tabvalue$ = of(this.tabvalue);
            this.compagnidigruppo$ = this.teamservice.getMembers(this.courseId,this.teamName);
            }
            
          })
  
     
    let membstat  : MemberStatus[] = new Array<MemberStatus>();

     this.teamsinconstruction$ = of (teamarray);
   
           this.teamservice.getMembers(this.courseId,teamarray[0].name).subscribe(members => {

           
               members.forEach (m => {
                
                this.tempmember = m;
                this.tempmember.teamid = teamid;
              
                membstat.push(this.tempmember);
                this.tempmember = null;
             
              
           })
          
           membstat.forEach(m => {

            this.studentservice.getOne(m.studentId).subscribe(
              s=> {
                let indexitem = membstat.find(e => e.studentId == s.id);
                if (indexitem)
                {
                  this.tempmember = membstat.find(e => e.studentId == s.id);
                  this.tempmember.firstName = s.firstName;
                  this.tempmember.lastName = s.name;
      
                }  
              }
            )

           })
          this.compagnidigruppo$ = of(membstat);
        
      
          })
     
 


  }
 
  )
    

  })

}


receiverejectmatricola($event) {

 this.matricola = $event;

}


receiverejectteamid($event) {
    
    let teamid = $event;
   
    this.notificationService.reject(teamid,this.matricola).subscribe(
      
      data => {
    
   }
   ,

  error => {
    
    this.teamsinconstruction$ = this.studentservice.getStudentCourseTeam(this.studentId,this.courseId);
  })

}




   //eventi per invito team 

   groupname ($event)
   {
    this.groupName = $event;
    
   }

   timeout($event)
   {
    this.timeoutValue = $event;
  
   }
   
    openDialog(message) {
    const dialogRef = this.dialog.open(Popup, { disableClose : true,
      width: '250px',
      data: {name: message}
    });

    dialogRef.afterClosed().subscribe(result => {

      
    });
  }

   openDialog2(message) {
    const dialogRef = this.dialog.open(Popup, {
      width: '250px',
      data: {name: message}
    });

    dialogRef.afterClosed().subscribe(result => {

      
    });
  }


  closeDialog() {
    this.dialog.closeAll();
  }

   receiveinvitation($event)
   {
     let control = false;

       this.dainvitare = $event;

      if (this.dainvitare.length -1 > this.maxstud)
      {
        control = true;
        this.openDialog2("Numero di studenti invitato troppo alto", );
        
      }

       else if (this.dainvitare.length -1 < this.minstud)
      {
        control = true;
        this.openDialog2("Invitare almeno " + this.minstud + "studenti", );
        
      }


   
   

        if (control == false) {
          
      this.openDialog("Creazione team in corso, attendere");
         this.teamservice.addTeam(this.courseId,this.groupName,this.dainvitare,this.timeoutValue,this.studentId)
        .subscribe(data => {
             
             this.closeDialog();
             this.openDialog2("La proposta per il team è stata inviata");
        
             this.teamsinconstruction$ = this.studentservice.getStudentCourseTeam(this.studentId,this.courseId);
             this.membersStatus$ = this.teamservice.getMembers(this.courseId,data.name);

             this.membersStatus$.subscribe(data1 => {
               
               data1.forEach(t => {
                  
                if(t.teamid==null){
                  this.ms = t;
                  this.ms.teamid = data.id;
                  this.membersStatus.push(this.ms);
                 
               }})
               
             
              
              this.compagnidigruppo$ = of(this.membersStatus);
               }
              )
            
            },
        (error) => (
           this.dainvitare = [],
          this.closeDialog(), this.openDialog2(error))
        
    
        )
        }

        this.dainvitare = [];
        

   }
   
   //eventi per mostrare la tabella con le proposte di team 
   updateteamstatus()
   {
     this.teams$ = this.studentservice.getStudentCourseTeam(this.studentId,this.courseId);
        this.teams$.subscribe (teamss => {

          teamss.forEach ( t => {

            if(t.status==1){
              this.teams2.push(t);
              this.teamName = t.name;
              this.tabvalue = true;
              this.tabvalue$ = of(this.tabvalue);
            }
            else
            {
              this.tabvalue$ = of(this.tabvalue); 
              this.teamsinconstruction.push(t);
              
            }
          })

          
          if(this.teams2.length > 0){
//se lo studente fa parte già di un gruppo setta tabvalue a true e mostra la tabella con il suo gruppo 
          
           this.teamsinconstruction = this.teams2;
           this.tabvalue = true;
        
          }
          
            this.tabvalue$ = of(this.tabvalue); 
        
          
           for (let i=0; i<this.teamsinconstruction.length; i++){
             
            
           this.teamservice.getMembers(this.courseId,this.teamsinconstruction[i].name).subscribe(members => {

           
               members.forEach (m => {
                
                this.tempmember = m;
                this.tempmember.teamid = this.teamsinconstruction[i].id;
                this.membersStatus.push(this.tempmember);
                this.tempmember = null;
             
              
           })
          
           this.membersStatus.forEach(m => {

            this.studentservice.getOne(m.studentId).subscribe(
              s=> {
                let indexitem = this.membersStatus.find(e => e.studentId == s.id);
                if (indexitem)
                {
                  this.tempmember = this.membersStatus.find(e => e.studentId == s.id);
                  this.tempmember.firstName = s.firstName;
                  this.tempmember.lastName = s.name;
              
                }  
              }
            )

           })
          this.compagnidigruppo$ = of(this.membersStatus);

          })
        }
      
         
        
        
        
        
      }); 
   }
   

   updateacceptedstatus(teamid,matricola,compagnidigruppo : Array<MemberStatus>)

   {
    let compà = new Array<MemberStatus>();
    compagnidigruppo.forEach( c => {

   if(c.teamid == teamid && c.studentId==matricola)
   {
     c.hasAccepted = true;

   }
   compà.push(c);

    })

    this.compagnidigruppo$ = of(compà);
   }


  }

  

