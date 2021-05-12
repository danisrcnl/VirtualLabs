import { Component,ViewChild,OnInit,EventEmitter,Output, ElementRef, Input } from '@angular/core';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import { Routes, RouterModule, Router, ActivatedRoute } from '@angular/router';
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
import { AppComponentStudent } from './student/app.component';
import { SidenavService } from './services/sidenav.service';
import { AuthService } from './auth/authservices/auth.service';
import { StudentDTO } from './model/studentDTO.model';
import {TeacherService} from './services/teacher.service';


export interface DialogLogin {
 username : string;
 password : string;
}


  @Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
  })
export class AppComponent implements OnInit {

  courses$ : Observable <Course[]>;
  courses : Course[] = new Array<Course>();
  currentUser : Observable <User>;
  studentId : String;
  currentStudent : StudentDTO;
  name: String;
  isLogin : boolean = true;
  log: Boolean = false;


  //variabili per login
  username : string;
  password: string;
  isTeacher: boolean;
  returnUrl: string;

  private _url2: string = "http://localhost:3000/courses";
  
@ViewChild ('sidenav') public sidenav: MatSidenav;


constructor (public dialog:MatDialog,private route: ActivatedRoute, private teacherService:TeacherService, private studentservice: StudentService, private sidenavService: SidenavService, private authService: AuthService, private router: Router) {

  
}



  
  title = 'VirtualLabs';

  routesfromservice : Routes[];

  
  
  ngOnInit(){


  //Prendo l'user dal servizio di autenticazione
  this.currentUser = this.authService.currentUser;

  //Prendo l'id dello studente dall'user 
  this.currentUser.subscribe(data => {
  
   this.studentId = data.username.split("@")[0].substring(1,7);

   //Se l'username inizia con la s allora cerco lo studente nel servizio adibito allo studente
   if(data.username.startsWith("s")){
   this.studentservice.getOne(this.studentId).subscribe(
    s => {
      this.currentStudent = s;
    },
    error => {

    }
  );}

  //Se l'username inizia con la d allora cerco l'insegnante nel servizio adibito all'insegnante
  if(data.username.startsWith("d")){
   this.teacherService.getOne(this.studentId).subscribe(
    s => {
      this.currentStudent = s;
    },
    error => {
      
    }
  );}});
 

    this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
    if(this.currentUser)
    {
  
      this.isLogin = false;
    }
  
  }



  toggleForMenuClick() {


 this.sidenavService.toggle();

}

onActivate (componentRef)
{
  componentRef.toggleForMenuClick();
}


   openDialog() {
    const dialogRef =  this.dialog.open (LoginDialogComponent , { 
       
      data: {username : this.username, password: this.password}
      
       });

      

       dialogRef.afterClosed().subscribe (data => {
         

         if(data != undefined)
         {
           
           this.authService.login (data.username,data.password).subscribe
           (
        data => {
        this.authService.info().subscribe(
          data1 => {
        
            this.isTeacher= data1.isTeacher;
            //Se l'user non è teacher allora lo redirigo alla pagina /student seguita dal suo username 
            //come parametro
            if (this.isTeacher == false)
        {
          this.router.navigate([this.returnUrl + 'student'], {queryParams: {user: data.username}});
          this.dialog.closeAll();
        }

        //Altrimenti lo redirigo alla pagina /teacher seguita dal suo username come parametro della URL
           else{ 
          
         this.router.navigate([this.returnUrl + 'teacher'], {queryParams: {user: data.username}});
         this.dialog.closeAll();
      }}       )

      
      })
         }})



   }

   logout()
    {
        this.isLogin = true;
        this.authService.logout();
        this.router.navigate (["/"]);
      
    }

}




