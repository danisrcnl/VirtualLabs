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
import { Studentreturn } from './auth/models/studentreturn';
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
  studs$ : Observable <Studentreturn[]>;
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

  
   /*if (this.authService.currentUserValue) {
            this.router.navigate(['/']);
        }

*/
  




/*
    this.authService.currentUser.subscribe ( x => {this.currentUser = x;
      this.studentId = this.currentUser.username.split("@")[0].substring(1,7);
  console.log("Hey");
  this.studentservice.getOne(this.studentId).subscribe(
    s => {
      this.currentStudent = s;
    },
    error => {
      console.log("errore");
    }
  );
    
    
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



  this.currentUser = this.authService.currentUser;

  this.currentUser.subscribe(data => {console.log(data)
  
   

   this.studentId = data.username.split("@")[0].substring(1,7);

   if(data.username.startsWith("s")){
   this.studentservice.getOne(this.studentId).subscribe(
    s => {
      this.currentStudent = s;
    },
    error => {
      console.log("errore");
    }
  );}

  if(data.username.startsWith("d")){
   this.teacherService.getOne(this.studentId).subscribe(
    s => {
      this.currentStudent = s;
    },
    error => {
      console.log("errore");
    }
  );}


  });

    

    this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
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


   openDialog() {
    const dialogRef =  this.dialog.open (LoginDialogComponent , { 
       
      data: {username : this.username, password: this.password}
      
       });

      

       dialogRef.afterClosed().subscribe (data => {
         console.log(data);

         if(data != undefined)
         {
           
           this.authService.login (data.username,data.password).subscribe
           (
        data => { console.log(data);
        this.authService.info().subscribe(
          data1 => {
            console.log(data1);
            console.log(this.isTeacher);
            this.isTeacher= data1.isTeacher;
            if (this.isTeacher == false)
        {
          this.router.navigate([this.returnUrl + 'student'], {queryParams: {user: data.username}});
          this.dialog.closeAll();
        }
        else{ 
          
         this.router.navigate([this.returnUrl + 'teacher'], {queryParams: {user: data.username}});
         this.dialog.closeAll();
      }}
        )
      
          this.currentUser = this.authService.currentUser;
   
  console.log(this.currentUser);
  

  this.currentUser.subscribe(data => {console.log(data)
  
   

   this.studentId = data.username.split("@")[0].substring(1,7);

   if(data.username.startsWith("s")){
   this.studentservice.getOne(this.studentId).subscribe(
    s => {
      this.currentStudent = s;
    },
    error => {
      console.log("errore");
    }
  );}

  if(data.username.startsWith("d")){
   this.teacherService.getOne(this.studentId).subscribe(
    s => {
      this.currentStudent = s;
    },
    error => {
      console.log("errore");
    }
  );}


  });
      
      })
         }


       })



   }


   openmodDialog() {
     this.dialog.open (SubjectdialogComponent);
   }

   logout()
    {
        this.isLogin = true;
        this.authService.logout();
        this.router.navigate (["/"]);
        console.log(this.currentUser);
    }

}




