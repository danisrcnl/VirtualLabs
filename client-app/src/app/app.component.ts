import { Component,ViewChild,OnInit,EventEmitter,Output, ElementRef, Input } from '@angular/core';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { Routes, RouterModule } from '@angular/router';
import { TeacherComponent } from './teacher/teacher.component';
import { VmsContcomponentComponent } from './teacher/vms-contcomponent.component';
import { StudentsContComponent } from './student/students-cont.component';
import {MatDialog} from '@angular/material/dialog';
import { LoginDialogComponent } from './auth/login-dialog.component';
import { Course } from './model/course.model';
import { SubjectdialogComponent } from './teacher/subjectdialog/subjectdialog.component';
import { StudentService } from './services/student.service';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Student } from './teacher/student.model';
import { User } from './auth/user';
import { Studentreturn } from './auth/models/studentreturn';
import { AppComponentStudent } from './student/app.component';
import { SidenavService } from './services/sidenav.service';




  @Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
  })
export class AppComponent implements OnInit {

  courses$ : Observable <Course[]>;
  studs$ : Observable <Studentreturn[]>;
  courses : Course[] = new Array<Course>();
  private _url2: string = "http://localhost:3000/courses";
  
@ViewChild ('sidenav') public sidenav: MatSidenav;


constructor (public dialog:MatDialog, private studentservice: StudentService, private sidenavService: SidenavService ) {
}


  
  title = 'VirtualLabs';

  routesfromservice : Routes[];

  
  
  ngOnInit(){

    
    this.studentservice._refresh$.subscribe(()=> {

      this.courses$ = this.studentservice.getcourse();
    });
  

    this.studentservice._refresh$.subscribe(()=>{
      this.studs$ = this.studentservice.getAll();
     
    });

    this.studs$ = this.studentservice.getAll();

    

    this.courses$ = this.studentservice.getcourse();
  
  }



  toggleForMenuClick() {


 this.sidenavService.toggle();

}

onActivate (componentRef)
{
  componentRef.toggleForMenuClick();
}


  students() {

console.log(this.studs$);
    
  }

   openDialog() {
     this.dialog.open (LoginDialogComponent , { panelClass: 'custom-modalbox' });
   }


   openmodDialog() {
     this.dialog.open (SubjectdialogComponent);
   }
  
}




