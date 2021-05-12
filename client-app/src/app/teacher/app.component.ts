import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
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

export interface DialogData {
  acronym : string;
  maxstud : number;
  minstud : number;
  coursename :string;
  enabled : boolean;
}

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

  acronym : string;
  maxstud : number;
  minstud : number;
  enabled: boolean;
  coursename :string;
  
constructor (public dialog:MatDialog,private authService: AuthService,private teacherService : TeacherService ,private courseService: CourseService, private sidenavService: SidenavService,
   public dialogRef : MatDialogRef<SubjectdialogComponent>, @Inject(MAT_DIALOG_DATA) public data : DialogData) {

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



   //Apro lo specchietto per modificare/cancellare/creare il corso 
   openmodDialog() {
     const dialogRef = this.dialog.open (SubjectdialogComponent, {
     data : {acronym : this.acronym, maxstud : this.maxstud, minstud : this.minstud, coursename : this.coursename, enabled: this.enabled}
        
     });

    
  
   dialogRef.afterClosed().subscribe(data => {

   

   if(data != undefined)
   {
      this.courses$ = this.teacherService.getCourseforTeacher(this.idteacher);
   }
   
   
   this.dialog.closeAll();
   this.courses$ = this.teacherService.getCourseforTeacher(this.idteacher);

  },
  error => {this.courses$ = this.teacherService.getCourseforTeacher(this.idteacher);}
  
  
  )

  


   }

  
  
}

