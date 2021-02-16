import { Component, OnInit, Input, ViewChild, SimpleChange, SimpleChanges} from '@angular/core';

import { Routes, RouterModule, Router } from '@angular/router';
import { PageNotFoundComponentComponent } from '../page-not-found-component/page-not-found-component.component';

import { StudentsComponent } from './students.component';
import { VmsContcomponentComponent2 } from './vms-contcomponent.component';
import {MatTableDataSource} from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import {StudentService} from 'src/app/services/student.service'
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

@Component({
  selector: 'app-students-cont',
  templateUrl: './students-cont.component.html',
  styleUrls: ['./students-cont.component.css']
})
export class StudentsContComponent implements OnInit {

  teamsinconstruction : Team[] = new Array<Team>();
  studenti: Student[] = new Array<Student>();
  compagni: Student[] = new Array<Student>();
  tabvalue : boolean;
 
  proposals : Proposal[] = new Array <Proposal>();
  enrolledstudents : Student[] = new Array<Student>();
  studenteaggiunto : Student;
  darimuovere : Student[] =new Array<Student>();
  
  dataSource = new MatTableDataSource<Student>(this.enrolledstudents);
  selection = new SelectionModel<Student>(true, []);
  courses$ : Observable <Course[]>;
  student_team : Student_Team[] = new Array<Student_Team>();
  id2: number;
  groupid : number;
  courseid : number;
  href : string ="";
  courses: Course[];
  courseName : String = "";

  //variabili per invito Team 
  
  dainvitare : Student[] = new Array<Student>();
  groupName : String = "";
  timeoutValue : number;

  //variabili per caricare la tabella con le proposte di Team
  studentid : String;
  students : Student[] = new Array<Student>(); //da qua ricavo poi nome e cognome degli studenti  
  membersStatus : MemberStatus[] = new Array<MemberStatus>(); //da qua controllo se lo studente ha accettato o ancora no la proposta 
  teams : Team[] = new Array<Team>();

  @ViewChild(StudentsComponent)
  studentsComponent: StudentsComponent
  
  constructor (private studentservice : StudentService, private teamservice : TeamService, private router: Router, private activeRoute: ActivatedRoute) {
    
    
    this.activeRoute.paramMap.subscribe(params => {


      this.href = this.router.url;
      let id = 0;
      let matricola = "s267656";
      this.studenti = [];
      this.enrolledstudents = [];
      
      
     //chiamata alla funzione 


      

      this.studentservice.getcourse().subscribe(data => {console.log (data)
        data.forEach(s => {
        
    
         s.path = '/student/' + s.path + '/students';
           
           if (s.path == this.href)
           {
          
             id = s.id;
             this.courseName = s.name;
            }
        
        })});

      this.teamservice.getTeamsById(matricola).subscribe (teams1 => {
        teams1.forEach (s => {
          this.teams.push(s);
        })
      })
              
    
        
        this.studentservice.getenrolledStudents().subscribe(receivedstudents=>{
        receivedstudents.forEach(s => {

          if(s.groupId == 0 && s.courseId==id) 
    
          { 
          
            this.enrolledstudents.push(s);
    
          }
          else {this.studenti.push(s);
                  
          this.dataSource = new MatTableDataSource<Student>(this.enrolledstudents);
          this.studentsComponent.updateFilteredOptions();
  
          }
        
         if (s.serial == matricola)
         {
          this.groupid = s.groupId;
          this.courseid = s.courseId;

         }

          
        
         });


         this.studentservice.getenrolledStudents().subscribe(receivedstudents=>{
        receivedstudents.forEach(s => {

          if (s.groupId == this.groupid && s.courseId == this.courseid)
          {
            this.compagni.push(s);
          }

        })
        
         });

         if(this.groupid!=0)
         {
           this.tabvalue = true;
         }
         else
         {
           this.tabvalue = false;
         }

        this.studentservice.getproposals().subscribe(propos => {
          propos.forEach (s => {
            this.proposals.push(s);
          })

        })
          this.dataSource = new MatTableDataSource<Student>(this.enrolledstudents);  
          
        
  })

    
})};





ngOnChanges (changes: SimpleChanges)
{
  this.teamservice.addTeam(this.courseName,this.groupName,this.dainvitare,this.timeoutValue);
}
  

  ngOnInit() {   



    this.studentservice._refresh$.subscribe(()=> {

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
    this.dataSource = new MatTableDataSource<Student>(this.enrolledstudents);
    
  }

  receivearray ($event) {
    this.darimuovere = $event;
    this.darimuovere.forEach(item => {
      let index: number = this.enrolledstudents.findIndex(d => d === item);
      console.log(this.enrolledstudents.findIndex(d => d === item));
      console.log (this.enrolledstudents);
      this.enrolledstudents.splice(index,1);
      if (!this.studenti.includes(item))
      this.studenti.push(item);});
      this.dataSource = new MatTableDataSource<Student>(this.enrolledstudents);
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
    this.teamservice.addTeam(this.courseName,this.groupName,this.dainvitare,this.timeoutValue);
  
   }
   
   //eventi per mostrare la tabella con le proposte di team 
   
   


  }

  

