import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSidenav } from '@angular/material/sidenav';
import { Routes } from '@angular/router';
import { Observable } from 'rxjs';
import { LoginDialogComponent } from '../auth/login-dialog.component';
import { Course } from '../course.model';
import { SidenavService } from '../services/sidenav.service';
import { StudentService } from '../services/student.service';
import { StudentsContComponent } from '../student/students-cont.component';
import { VmsContcomponentComponent2 } from '../student/vms-contcomponent.component';
import { SubjectdialogComponent } from './subjectdialog/subjectdialog.component';


  @Component({
    selector: 'app-root-teacher',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
  })
export class AppComponentTeacher implements OnInit {
  
 courses$ : Observable <Course[]>;
  courses : Course[] = new Array<Course>();
  private _url2: string = "http://localhost:3000/courses";
  
constructor (public dialog:MatDialog, private studentservice: StudentService, private sidenavService: SidenavService) {

}


  
  title = 'Progetto';

  @ViewChild ('sidenav') public sidenav : MatSidenav;

  routesfromservice : Routes[];


  
  ngOnInit(){

    
    this.studentservice._refresh$.subscribe(()=> {

      this.courses$ = this.studentservice.getcourse();
    });
  
    this.courses$ = this.studentservice.getcourse();

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

