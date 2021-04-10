import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSidenav } from '@angular/material/sidenav';
import { Routes } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/authservices/auth.service';
import { LoginDialogComponent } from '../auth/login-dialog.component';
import { User } from '../auth/user';
import { Course } from '../model/course.model';
import { CourseDTO } from '../model/courseDTO.model';
import { CourseService } from '../services/course.service';
import { SidenavService } from '../services/sidenav.service';
import { StudentService } from '../services/student.service';
import { TeacherService } from '../services/teacher.service';
import { StudentsContComponent } from '../student/students-cont.component';
import { VmsContcomponentComponent2 } from '../student/vms-contcomponent.component';
import { SubjectdialogComponent } from './subjectdialog/subjectdialog.component';


  @Component({
    selector: 'app-root-teacher',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
  })
export class AppComponentTeacher implements OnInit {
  
  courses$ : Observable <CourseDTO[]>;
  courses : CourseDTO[] = new Array<CourseDTO>();
  private _url2: string = "http://localhost:3000/courses";
  temp : string;
  idteacher : String;
  currentUser : User;
  
constructor (public dialog:MatDialog,private authService: AuthService,private teacherService : TeacherService ,private courseService: CourseService, private sidenavService: SidenavService) {

this.authService.currentUser.subscribe (x => this.currentUser = x);

}


  
  title = 'Progetto';

  @ViewChild ('sidenav') public sidenav : MatSidenav;

  routesfromservice : Routes[];

  
  ngOnInit(){

    this.idteacher = this.currentUser.username.split("@")[0].substring(1,7);
 
    this.teacherService._refresh$.subscribe(()=> {

      this.courses$ = this.teacherService.getCourseforTeacher(this.idteacher);
    });
  
    this.courses$ = this.teacherService.getCourseforTeacher(this.idteacher);

    this.courses$.subscribe( coursess => {

      coursess.forEach (c => {

        this.courses.push(c);
      })

for (let i=0 ;i< this.courses.length; i++)
    {
      
      this.courses.forEach (c => {

        this.temp = c.name.toLowerCase().split(' ').join('-');
        console.log (this.temp);
        c.path = this.temp;
      })

    }

    
    })

    

    console.log(this.courses);

    this.sidenavService.setSidenav(this.sidenav);
  }
  ngAfterViewInit(): void {
    this.sidenavService.setSidenav(this.sidenav);
  }

 
   openDialog() {
     this.dialog.open (LoginDialogComponent, { height: '400px',
     width: '600px',});
   }


   openmodDialog() {
     this.dialog.open (SubjectdialogComponent);
   }
  
}

