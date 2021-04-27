import { Component, Inject, OnInit } from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
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
  selectedcourse :Course;
  courses : Course[] = new Array<Course>();
  enabled : boolean;
  acronym : string;
  maxstud2 : number;
  minstud2 : number;
  enabled2 : boolean;
  courseDTO : CourseDTO = new CourseDTO();
  courses$ : Observable <CourseDTO[]>;
  currentUser : User;

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
     selcourse :'',
     maxstud :'',
     minstud : '',
     coursename:'',
     enabled: false,
     maxstud2 : '',
     minstud2:'',
     enabled2: '',
     acronym:'',
     
   })

   this.myForm.valueChanges.subscribe(console.log);

    this.teacherId = this.currentUser.username.split("@")[0].substring(1,7);

  //this.studentservice.getcourse().subscribe(data => this.courses=data);

  this.courses$ = this.teacherService.getCourseforTeacher(this.teacherId);

  this.teacherService.getCourseforTeacher(this.teacherId).subscribe(data => this.courses=data);
  

  }


checkValue(event :any)
{
  console.log(event);
}
createcourse ()
{


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
  
  data => {console.log(data);
  this.courses$ = this.teacherService.getCourseforTeacher(this.teacherId);

  this.dialog.closeAll();
  
}



, error => {this.error = error});


console.log(this.courseDTO);

}


 delete()
 {
  for (var i in this.courses) {

    if (this.courses[i].name == this.selectedcourse.name)
    {
      this.courses[i].name = this.newname;
      this.courseservice.deletecourse(this.courses[i],i);
     
    }
  }

 }


 check() {

/* if (this.isChecked)
 {
   console.log ("Checked");
 }
 else 
 console.log ("Not checked");
*/

console.log (this.enabled);
 

 }


 close (){
   
   this.dialog.closeAll();

   console.log(this.data);
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
