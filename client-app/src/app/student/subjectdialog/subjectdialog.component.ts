import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import { StudentService } from 'src/app/services/student.service';
import { Course } from 'src/app/course.model';
import { MatDialog } from '@angular/material/dialog';


@Component({
  selector: 'app-subjectdialog',
  templateUrl: './subjectdialog.component.html',
  styleUrls: ['./subjectdialog.component.css']
})
export class SubjectdialogComponent2 implements OnInit {

  myForm : FormGroup;
  newname :string;
  coursename :string;
  selectedcourse :Course;
  courses : Course[] = new Array<Course>();


  constructor(private fb: FormBuilder,private studentservice : StudentService, public dialog:MatDialog) {


    var newcourses = new Object();
     }


  
  ngOnInit() {

   this.myForm = this.fb.group({
     selcourse :'',
     newname :'',
     coursename:'',
   })

   this.myForm.valueChanges.subscribe(console.log);

  this.studentservice.getcourse().subscribe(data => this.courses=data);


  }


modifyname() 
{
  console.log(this.newname);
  var str = this.newname;
  str = str.replace(/\s+/g, '-').toLowerCase();
  str = 'teacher/course' + '/' + str;

  
  for (var i in this.courses) {

    if (this.courses[i].name == this.selectedcourse.name)
    {
      this.courses[i].name = this.newname;
      this.courses[i].path = str;
      this.studentservice.editname(this.courses[i],i);
     
    }
  }
  
  
}

createcourse ()
{

var a = <Course> {};

 a.name = this.coursename;
 a.path = this.coursename;
 var str = a.path;
str = str.replace(/\s+/g, '-').toLowerCase(); console.log(str);
a.path = 'teacher/course' + '/' + str;
 this.studentservice.createcourse(a);

}


 delete()
 {
  for (var i in this.courses) {

    if (this.courses[i].name == this.selectedcourse.name)
    {
      this.courses[i].name = this.newname;
      this.studentservice.deletecourse(this.courses[i],i);
     
    }
  }
 }


 close (){
   
   this.dialog.closeAll();
 }

}