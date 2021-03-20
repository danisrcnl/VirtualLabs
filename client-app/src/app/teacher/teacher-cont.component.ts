import { Component, OnInit, Input, ViewChild} from '@angular/core';
import {Student} from './student.model'
import { Routes, RouterModule, Router } from '@angular/router';
import { PageNotFoundComponentComponent } from '../page-not-found-component/page-not-found-component.component';

import { VmsContcomponentComponent } from './vms-contcomponent.component';
import {MatTableDataSource} from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import {StudentService} from 'app/services/student.service'
import {FormControl, Validators} from '@angular/forms';
import { Course } from '../model/course.model';
import { ActivatedRoute } from '@angular/router';
import { TeacherComponent } from './teacher.component';

@Component({
  selector: 'teacher-cont',
  templateUrl: './teacher-cont.component.html',
  styleUrls: ['./teacher-cont.component.css']
})
export class TeacherContComponent implements OnInit {

  studenti: Student[] = new Array<Student>();
  enrolledstudents : Student[] = new Array<Student>();
  studenteaggiunto : Student;
  darimuovere : Student[] =new Array<Student>();
  dataSource = new MatTableDataSource<Student>(this.enrolledstudents);
  selection = new SelectionModel<Student>(true, []);
  
  id2: number;
  href : string ="";
  courses: Course[];
  
  @ViewChild(TeacherComponent)
  studentsComponent: TeacherComponent
  
  constructor (private studentservice : StudentService, private router: Router, private activeRoute: ActivatedRoute) {
    
    
    this.activeRoute.paramMap.subscribe(params => {



      this.href = this.router.url;
      let id = 0;
      this.studenti = [];
      this.enrolledstudents = [];
      
      this.studentservice.getcourse().subscribe(data => {console.log (data)
        data.forEach(s => {
    
         s.path = '/teacher/' + s.path + '/students';
         
           if (s.path == this.href)
           {
          
             id = s.id;
            
            }
    
        })});
    
      
    
        
        this.studentservice.getenrolledStudents().subscribe(receivedstudents=>{console.log(receivedstudents)
        receivedstudents.forEach(s => {
          if(s.courseId == id) 
    
          { 
            this.enrolledstudents.push(s);
    
          }
          else {this.studenti.push(s);
        
          this.dataSource = new MatTableDataSource<Student>(this.enrolledstudents);
    
          this.studentsComponent.updateFilteredOptions();
        
    
          }});
    
          this.dataSource = new MatTableDataSource<Student>(this.enrolledstudents);
    
  })
      this.studenti = [];
      this.enrolledstudents = [];
})};

  

  ngOnInit() {   
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



    
  }

  

