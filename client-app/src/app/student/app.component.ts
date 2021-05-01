import { Component,ViewChild,OnInit,EventEmitter,Output, ElementRef, Input } from '@angular/core';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { Routes, RouterModule, Router, ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Course } from '../model/course.model';
import { MatDialog } from '@angular/material/dialog';
import { StudentService } from '../services/student.service';
import { StudentsContComponent } from './students-cont.component';
import { LoginDialogComponent } from '../auth/login-dialog.component';
import { SidenavService } from '../services/sidenav.service';
import { CourseService } from '../services/course.service';
import { CourseDTO } from '../model/courseDTO.model';
import { map } from 'rxjs/operators';
import { AuthService } from '../auth/authservices/auth.service';
import { User } from '../auth/user';





  @Component({
    selector: 'app-root-student',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
  })
export class AppComponentStudent implements OnInit {

  courses$ : Observable <CourseDTO[]>;
  courses : CourseDTO[] = new Array<CourseDTO>();
  paths : Map<CourseDTO,String> = new Map<any,any>();
  temp : string;
  currentUser : User;
  studentId : string;
  
  
constructor (private route: ActivatedRoute,private authService : AuthService, private courseService: CourseService, public dialog:MatDialog, private studentservice: StudentService, private sidenavService: SidenavService, private router: Router) {

this.authService.currentUser.subscribe (x => this.currentUser = x);

}


  
  title = 'Progetto';

  @ViewChild ('sidenav') public sidenav : MatSidenav;

  routesfromservice : Routes[];


  
  ngOnInit(){

  



   this.studentId = this.currentUser.username.split("@")[0].substring(1,7);
    
   
    
    this.studentservice._refresh$.subscribe(()=> {

      this.courses$ = this.studentservice.getStudentCourses(this.studentId);
      //this.courses$ = this.courseService.getAllCourses();
    });
  
    this.courses$ = this.studentservice.getStudentCourses(this.studentId);
    //this.courses$ = this.courseService.getAllCourses();

    this.courses$.subscribe( coursess => {

      coursess.forEach (c => {

        this.courses.push(c);
      })
for (let i=0 ;i< this.courses.length; i++)
    {
      
      this.courses.forEach (c => {

        this.temp = c.name.toLowerCase().split(' ').join('-');
      ;
        c.path = this.temp;
      })

    }
    })

    

    

    this.sidenavService.setSidenav(this.sidenav);
  }

  ngAfterViewInit(): void {
    this.sidenavService.setSidenav(this.sidenav);
  }

 
   openDialog() {
     this.dialog.open (LoginDialogComponent, { height: '400px',
     width: '600px',});
   }

  
}




