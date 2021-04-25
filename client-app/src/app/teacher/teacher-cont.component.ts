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
import { TeamService } from 'app/services/team.service';
import { AuthService } from 'app/auth/authservices/auth.service';
import { CourseService } from 'app/services/course.service';
import { StudentDTO } from 'app/model/studentDTO.model';

@Component({
  selector: 'teacher-cont',
  templateUrl: './teacher-cont.component.html',
  styleUrls: ['./teacher-cont.component.css']
})
export class TeacherContComponent implements OnInit {

  studenti: StudentDTO[] = new Array<StudentDTO>();
  enrolledstudents : StudentDTO[] = new Array<StudentDTO>();
  studenteaggiunto : StudentDTO;
  darimuovere : StudentDTO[] =new Array<StudentDTO>();
  dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
  selection = new SelectionModel<StudentDTO>(true, []);
  courseId :string;
  id2: number;
  href : string ="";
  courses: Course[];
  
  @ViewChild(TeacherComponent)
  studentsComponent: TeacherComponent
  
  constructor (private courseService : CourseService, private route: ActivatedRoute, private studentservice : StudentService, private teamservice : TeamService, private router: Router, private activeRoute: ActivatedRoute, private authService: AuthService) 
    
    {
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
      
    
      this.courseService.getenrolledStudents(this.courseId).subscribe(receivedstudents=>{
        receivedstudents.forEach(s => {

          
          
            this.enrolledstudents.push(s);
    
                  
          this.dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
          this.studentsComponent.updateFilteredOptions();
  
          }
          
        ) 
        
         });
  
        };
  ngOnInit() {   
    }

  


    
  
  




  receivestudent($event) {
    this.studenteaggiunto = $event;
    if (!this.enrolledstudents.includes(this.studenteaggiunto)){
    this.enrolledstudents.push(this.studenteaggiunto);
    this.enrolledstudents = Object.assign( this.enrolledstudents);
    this.studenti.forEach(item => {
      let index: number = this.studenti.findIndex(d => d === item);
      this.studenti.splice(index,1)});}
    
      this.studentsComponent.updateFilteredOptions();
      this.studentsComponent.selection.clear();
      this.studentsComponent.studenteselezionato = null;
    
    this.dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);
    
  }

  receivearray ($event) {
    this.darimuovere = $event;
    this.darimuovere.forEach(item => {
      let index: number = this.enrolledstudents.findIndex(d => d === item);
      //console.log(this.enrolledstudents.findIndex(d => d === item));
      //console.log (this.enrolledstudents);
      this.enrolledstudents.splice(index,1);
      if (!this.studenti.includes(item))
      this.studenti.push(item);});
      //console.log(this.studenti);
      //console.log(this.enrolledstudents);
      this.dataSource = new MatTableDataSource<StudentDTO>(this.enrolledstudents);

    
  }



    
  }

  

