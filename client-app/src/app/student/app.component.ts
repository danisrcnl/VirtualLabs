import { Component,ViewChild,OnInit,EventEmitter,Output, ElementRef, Input } from '@angular/core';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { Routes, RouterModule } from '@angular/router';

import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Course } from '../course.model';
import { MatDialog } from '@angular/material/dialog';
import { StudentService } from '../services/student.service';
import { StudentsContComponent } from './students-cont.component';
import { LoginDialogComponent } from '../auth/login-dialog.component';
import { SubjectdialogComponent2 } from './subjectdialog/subjectdialog.component';
import { SidenavService } from '../services/sidenav.service';





  @Component({
    selector: 'app-root-student',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
  })
export class AppComponentStudent implements OnInit {

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
     this.dialog.open (SubjectdialogComponent2);
   }
  
}




