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
import { Observable } from 'rxjs';
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

@Component({
  selector: 'app-students-cont',
  templateUrl: './students-cont.component.html',
  styleUrls: ['./students-cont.component.css']
})
export class StudentsContComponent implements OnInit {

  teamsinconstruction : Team[] = new Array<Team>();
  studenti: StudentDTO[] = new Array<StudentDTO>();
  compagni: StudentDTO[] = new Array<StudentDTO>();
  tabvalue : boolean;
 
  proposals : Proposal[] = new Array <Proposal>();
  enrolledstudents : StudentDTO[] = new Array<StudentDTO>();
  studenteaggiunto : StudentDTO;
  darimuovere : StudentDTO[] =new Array<StudentDTO>();
  
  dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
  selection = new SelectionModel<Student>(true, []);
  courses$ : Observable <Course[]>;
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

  //variabili per caricare la tabella con i componenti del gruppo 
  compagnicorso : MemberStatus[] = new Array <MemberStatus>();
  compagniDTO : StudentDTO[] = new Array<StudentDTO>();

  //variabili per caricare la tabella con le proposte di Team
  studentid : string;
  students : Student[] = new Array<Student>(); //da qua ricavo poi nome e cognome degli studenti  
  membersStatus : MemberStatus[] = new Array<MemberStatus>(); //da qua controllo se lo studente ha accettato o ancora no la proposta 
  tempmember : MemberStatus;
  teams : Team[] = new Array<Team>();
  courseId :string;
  teams$ : Observable <Team[]>;
  teams2 : Team[] = new Array<Team>();
  teamName : String = "";
  creator$ : Observable <StudentDTO>;
  creator : StudentDTO;
  currentStudent$ : Observable<StudentDTO>;
  currentStudent : StudentDTO;
  tempstudent : StudentDTO;
  @ViewChild(StudentsComponent)
  studentsComponent: StudentsComponent
  
  constructor (private courseService : CourseService, private route: ActivatedRoute, private studentservice : StudentService, private teamservice : TeamService, private router: Router, private activeRoute: ActivatedRoute, private authService: AuthService) {
    

    //prendo l'username dell'user loggato 

    

    this.authService.currentUser.subscribe ( x => {this.currentUser = x;
      this.studentId = this.currentUser.username.split("@")[0].substring(1,7);
  
  this.studentservice.getOne(this.studentId).subscribe(
    s => {
      this.currentStudent = s;
    },
    error => {
      console.log("errore");
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
       console.log (this.courseId);
       });

      

    });
      
      
     

      /*this.studentservice.getcourse().subscribe(data => {console.log (data)
        data.forEach(s => {
        
    
         s.path = '/student/' + s.path + '/students';
           
           if (s.path == this.href)
           {
          
             id = s.id;
             this.courseName = s.name;
            }
        
        })});
*/
    
              
    
        
        this.courseService.getAvailableStudents(this.courseId).subscribe(receivedstudents=>{
        receivedstudents.forEach(s => {

          
            if (s.id!=this.studentId) 
            this.enrolledstudents.push(s);
    
                  
          this.dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
          this.studentsComponent.updateFilteredOptions();
  
          }
          
        ) 
        
         });

         console.log(this.enrolledstudents);
      
    

         

         console.log(this.compagni);

         
        //prendo i team dello studente 

        this.teams$ = this.studentservice.getStudentCourseTeam(this.studentId,this.courseId);

        this.teams$.subscribe (teamss => {

          teamss.forEach ( t => {

            if(t.status==1){
            this.teams2.push(t);
            this.teamName = t.name;
            }
            else
            {
              this.teamsinconstruction.push(t);
            }
          })

          
          if(this.teams2.length > 0)
            
               //se lo studente fa parte già di un gruppo setta tabvalue a true e mostra la tabella con il suo gruppo 
         {
           this.tabvalue = true;
           console.log("length maggiore 0");
           this.teamservice.getMembers(this.courseId,this.teamName).subscribe(members => {
         
                 this.compagnicorso = members;

                 console.log(this.compagnicorso);

                 this.compagnicorso.forEach(
                  c => {
                  
                  this.studentservice.getOne(c.studentId).subscribe(
                   data => {this.compagniDTO.push(data)}
                 );
           })

           
           
          }) }

            
           
         
         else{
         
           {this.tabvalue = false;
           }
          
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
                  this.tempmember.nome = s.name;
                  this.tempmember.cognome = s.firstName;
                 // this.membersStatus.push(this.tempmember);
                }
                
                  
                
              }
            )

           })

           console.log(this.membersStatus);
          })
        }
        
         }
        
        
        
        
      }); 
        
        console.log(this.teams2.length);

    
          

           console.log(this.tabvalue);

        this.studentservice.getproposals().subscribe(propos => {
          propos.forEach (s => {
            this.proposals.push(s);
          })

        })
          //this.dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);  
          


  };




ngOnChanges (changes: SimpleChanges)
{

  //this.teamservice.addTeam(this.courseName,this.groupName,this.dainvitare,this.timeoutValue,this.studentId);
}
  

  ngOnInit() {   

    this.authService.currentUser.subscribe(u => {
      this.studentid = u.username;

    })

  
    

//console.log (this.studentid);
this.studentid = this.studentid.substring(0,7);
//console.log(this.studentid);




console.log(this.compagni);

    this.courseService._refresh$.subscribe(()=> {

      this.courses$ = this.studentservice.getcourse();
    });
  
    this.courses$ = this.studentservice.getcourse();


   



  }

  



    
  
  




  receivestudent($event) {
    this.studenteaggiunto = $event;
    if (!this.enrolledstudents.includes(this.studenteaggiunto)){
    this.enrolledstudents.push(this.studenteaggiunto);
    console.log(this.enrolledstudents);
    console.log(this.studenti);
    this.enrolledstudents = Object.assign( this.enrolledstudents);
    this.studenti.forEach(item => {
      let index: number = this.studenti.findIndex(d => d === item);
      this.studenti.splice(index,1)});}
      console.log(this.studenti);
    this.dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
    
  }

  receivearray ($event) {
    this.darimuovere = $event;
    this.darimuovere.forEach(item => {
      let index: number = this.enrolledstudents.findIndex(d => d === item);
      console.log (this.enrolledstudents);
      this.enrolledstudents.splice(index,1);
      if (!this.studenti.includes(item))
      this.studenti.push(item);});
      this.dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
  }




   //eventi per invito team 

   groupname ($event)
   {
    this.groupName = $event;
    console.log (this.groupName);
   }

   timeout($event)
   {
    this.timeoutValue = $event;
    console.log(this.timeoutValue);
   }


   receiveinvitation($event)
   {
    
    this.dainvitare = $event;
   
         this.teamservice.addTeam(this.courseId,this.groupName,this.dainvitare,this.timeoutValue,this.studentId)
        .subscribe(data => {console.log(data)}, error => {this.error =error});
      


   }
   
   //eventi per mostrare la tabella con le proposte di team 
   
   


  }

  

