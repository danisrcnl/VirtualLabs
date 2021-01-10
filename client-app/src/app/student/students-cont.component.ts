import { Component, OnInit, Input, ViewChild, SimpleChange, SimpleChanges} from '@angular/core';

import { Routes, RouterModule, Router } from '@angular/router';
import { PageNotFoundComponentComponent } from '../page-not-found-component/page-not-found-component.component';

import { StudentsComponent } from './students.component';
import { VmsContcomponentComponent2 } from './vms-contcomponent.component';
import {MatTableDataSource} from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import {StudentService} from 'src/app/services/student.service'
import {FormControl, Validators} from '@angular/forms';
import { Course } from '../course.model';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { Student } from './student.model';
import { Proposal } from '../proposal.model';

@Component({
  selector: 'app-students-cont',
  templateUrl: './students-cont.component.html',
  styleUrls: ['./students-cont.component.css']
})
export class StudentsContComponent implements OnInit {

  studenti: Student[] = new Array<Student>();
  compagni: Student[] = new Array<Student>();
  tabvalue : boolean;
  groupName : String = "";
  timeoutValue : number;
  proposals : Proposal[] = new Array <Proposal>();
  enrolledstudents : Student[] = new Array<Student>();
  studenteaggiunto : Student;
  darimuovere : Student[] =new Array<Student>();
  dainvitare : Student[] =new Array<Student>();
  dataSource = new MatTableDataSource<Student>(this.enrolledstudents);
  selection = new SelectionModel<Student>(true, []);
  courses$ : Observable <Course[]>;
  
  id2: number;
  groupid : number;
  courseid : number;
  href : string ="";
  courses: Course[];
  cacca : "cccc";
  @ViewChild(StudentsComponent)
  studentsComponent: StudentsComponent
  
  constructor (private studentservice : StudentService, private router: Router, private activeRoute: ActivatedRoute) {
    
    
    this.activeRoute.paramMap.subscribe(params => {


      this.href = this.router.url;
      let id = 0;
      let matricola = "s267656";
      this.studenti = [];
      this.enrolledstudents = [];
      
      


      this.studentservice.getcourse().subscribe(data => {console.log (data)
        data.forEach(s => {
        
    
         s.path = '/student/' + s.path + '/students';
           console.log (s.path);
          console.log(this.href);
           if (s.path == this.href)
           {
          
             id = s.id;
            
            }
    
        })});


    
      
    
        
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

         console.log(this.tabvalue);




    
          this.dataSource = new MatTableDataSource<Student>(this.enrolledstudents);
    
  })
      this.studenti = [];
      this.enrolledstudents = [];
})};



ngOnChanges (changes: SimpleChanges)
{
  
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

   receiveinvitation($event)
   {
  this.dainvitare = $event;
  console.log(this.dainvitare);
  
   }
   
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

  }

  

