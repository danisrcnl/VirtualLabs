import { Component,ViewChild,OnInit,EventEmitter,Output, ElementRef, Input } from '@angular/core';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { Routes, RouterModule, Router } from '@angular/router';
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
import { AuthService } from './auth/authservices/auth.service';
import { StudentDTO } from './model/studentDTO.model';
import {TeacherService} from './services/teacher.service';




  @Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
  })
export class AppComponent implements OnInit {

  courses$ : Observable <Course[]>;
  studs$ : Observable <Studentreturn[]>;
  courses : Course[] = new Array<Course>();
  currentUser : User;
  studentId : String;
  currentStudent : StudentDTO;
  name: String;
  isLogin : boolean = true;
  private _url2: string = "http://localhost:3000/courses";
  
@ViewChild ('sidenav') public sidenav: MatSidenav;


constructor (public dialog:MatDialog, private teacherService:TeacherService, private studentservice: StudentService, private sidenavService: SidenavService, private authService: AuthService, private router: Router) {

  this.authService.currentUser.subscribe(x => {this.currentUser =x});

  /*
    let splitted = this.currentUser.username.split("@", 1);
    let splittedString: String = new String(splitted);
    let control = splitted[0][0];
    
    // splitted[1] conterrà l'id dell'utente, control contiene 'd' o 's'
    if (control == "s") {
      splitted = splittedString.split("s", 2);
      this.studentservice.getOne(splitted[1]).subscribe(x => {
        this.name = x.firstName;
      })
    } else if (control == "d") {
      splitted = splittedString.split("d", 2);
      this.teacherService.getOne(splitted[1]).subscribe(x => {
        this.name = x.firstName;
      })
    }
  */
}



  
  title = 'VirtualLabs';

  routesfromservice : Routes[];

  
  
  ngOnInit(){

    if(this.currentUser)
    {
      console.log("loggato");
      this.isLogin = false;
    }
    else
    console.log ("sloggato");

    
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

   logout()
    {
        this.isLogin = true;
        this.authService.logout();
        this.router.navigate (["/"]);
    }

}




