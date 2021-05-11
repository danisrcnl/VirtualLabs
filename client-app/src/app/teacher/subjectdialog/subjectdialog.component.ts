import { Component, Inject, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { StudentService } from 'app/services/student.service';
import { Course } from 'app/model/course.model';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { CourseService } from 'app/services/course.service';
import { element } from 'protractor';
import { CourseDTO } from 'app/model/courseDTO.model';
import { TeacherService } from 'app/services/teacher.service';
import { first } from 'rxjs/operators';
import { AuthService } from 'app/auth/authservices/auth.service';
import { User } from 'app/auth/user';
import { Observable } from 'rxjs';
import { DialogData } from '../app.component';


@Component({
  selector: 'app-subjectdialog',
  templateUrl: './subjectdialog.component.html',
  styleUrls: ['./subjectdialog.component.css']
})
export class SubjectdialogComponent implements OnInit {

  error ='';
  myForm : FormGroup;
  newname :string;
  maxstud : number;
  minstud : number;
  coursename :string;
  selectedcourse :CourseDTO;
  courses : Course[] = new Array<Course>();
  enabled : boolean;
  acronym : string;
  maxstud2 : number;
  minstud2 : number;
  enabled2 : boolean;
  courseDTO : CourseDTO = new CourseDTO();
  courses$ : Observable <CourseDTO[]>;
  currentUser : User;
  submitted = false;

  modCourse: Boolean = false;
  newCourse: Boolean = false;
  isIntro: Boolean = true;
  courseSelected: Boolean = false;
  deletePressed: Boolean = false;
  
  teacherId : string ;

  constructor(private fb: FormBuilder,private teacherService : TeacherService, private studentservice : StudentService, private courseservice : CourseService, private authService : AuthService, public dialog:MatDialog,
   @Inject(MAT_DIALOG_DATA) public data : DialogData
   
    ) { 
 
    this.authService.currentUser.subscribe (x => this.currentUser = x);
    
     }


  
  ngOnInit() {

   this.myForm = this.fb.group({
     selcourse :[''],
     maxstud :['',Validators.required],
     minstud : ['',Validators.required],
     coursename:['',Validators.required],
     acronym:['',Validators.required],
     
   })

   this.myForm.valueChanges.subscribe();

    this.teacherId = this.currentUser.username.split("@")[0].substring(1,7);

  //this.studentservice.getcourse().subscribe(data => this.courses=data);

  this.courses$ = this.teacherService.getCourseforTeacher(this.teacherId);

  this.teacherService.getCourseforTeacher(this.teacherId).subscribe(data => this.courses=data);
  

  }

      get f() { return this.myForm.controls; }

checkValue(event :any)
{

}
createcourse ()
{
   this.submitted = true;

if (this.myForm.invalid)
{

  return;
}



this.courseDTO.acronym= this.data.acronym;
this.courseDTO.name = this.data.coursename;
this.courseDTO.min = this.data.minstud;
this.courseDTO.max = this.data.maxstud;


if(this.enabled2)

this.courseDTO.enabled = this.enabled2;  
else
this.courseDTO.enabled = false;


this.courseservice.addCourse(this.courseDTO,this.teacherId)
.subscribe(
  
  data => {
 // this.courses$ = this.teacherService.getCourseforTeacher(this.teacherId);

  this.dialog.closeAll();
  
}



, error => {this.error = error});




}


 delete()
 {
  let name = this.selectedcourse.name;
  this.courseservice.deleteCourse(name).subscribe(data => {
  this.dialog.closeAll();
  });
  

 }


 check() {


let name = this.selectedcourse.name;

if(this.data.minstud!=undefined && this.data.maxstud!=undefined){
this.courseservice.setMin(name,this.data.minstud).subscribe(data => {


  this.courseservice.setMax(name,this.data.maxstud).subscribe (data => {

  });
});
 }


if(this.data.enabled == true)
{
  this.courseservice.setEnabled(name,this.data.enabled).subscribe(data => {

this.courses$ = this.teacherService.getCourseforTeacher(this.teacherId);

  });
}
else
this.courses$ = this.teacherService.getCourseforTeacher(this.teacherId);
 



 }


 close (){
   
   this.dialog.closeAll();


 }


 showMod () {
   this.isIntro = false;
   this.modCourse = true;
 }

 showNew () {
   this.isIntro = false;
   this.newCourse = true;
 }

 showMods () {
   this.courseSelected = true;
 }

 selectDelete () {
   this.deletePressed = true;

 }

}
