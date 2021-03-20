import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import { StudentService } from 'src/app/services/student.service';
import { Course } from 'src/app/model/course.model';
import { MatDialog } from '@angular/material/dialog';
import { CourseService } from 'src/app/services/course.service';
import { element } from 'protractor';
import { CourseDTO } from 'src/app/model/courseDTO.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { first } from 'rxjs/operators';


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
  
  
  teacherId : string = "t000000";

  constructor(private fb: FormBuilder,private teacherService : TeacherService, private studentservice : StudentService, private courseservice : CourseService, public dialog:MatDialog) { 
 
    var newcourses = new Object();
    
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

  //this.studentservice.getcourse().subscribe(data => this.courses=data);

  this.teacherService.getCourseforTeacher(this.teacherId).subscribe(data => this.courses=data);
  

  }


checkValue(event :any)
{
  console.log(event);
}
createcourse ()
{


this.courseDTO.acronym= this.acronym;
this.courseDTO.name = this.coursename;
this.courseDTO.min = this.minstud2;
this.courseDTO.max = this.maxstud2;

if(this.enabled2)

this.courseDTO.enabled = this.enabled2;  
else
this.courseDTO.enabled = false;


this.courseservice.addCourse(this.courseDTO,this.teacherId).pipe(first())
.subscribe(data => {console.log(data)}, error => {this.error = error});
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
 }

}
