import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StudentDTO } from 'app/model/studentDTO.model';
import { AssignmentService } from 'app/services/assignment.service';
import { StudentService } from 'app/services/student.service';
import { from, Observable, of } from 'rxjs';
import {Assignment} from '../../model/assignment.model';
import {Paper} from '../../model/paper.model';
import {PaperStatus} from '../../model/paperStatus.model';
import {PaperStatusTime} from '../../model/paperStatusTime.model';
import {AssignmentWithPapers, PaperWithHistory} from '../../model/assignmentsupport.model'
import { AuthService } from 'app/auth/authservices/auth.service';
import { User } from 'app/auth/user';

@Component({
  selector: 'app-elaboraticontteacher',
  templateUrl: './elaboraticontteacher.component.html',
  styleUrls: ['./elaboraticontteacher.component.css']
})
export class ElaboraticontteacherComponent implements OnInit {

  constructor (
    private route: ActivatedRoute, 
    private router : Router, 
    private assignmentService: AssignmentService, 
    private studentService: StudentService,
    private authService : AuthService
    ) 
    { }

  
  firstParam : string = "";
  public hreff : string ="";
  public href :string ="";
  public href2 : string ="";
  public href3 : string ="";
  public subject : string ="";
  private courseName: String;
  public errBlock : boolean;
  public errorText : String;
  public creationDate : Date = new Date;
  public expiryDate : Date = new Date;
  public creator: String;
  public content : String;
  public currentUser : Observable <User>
  public currentTeacher : String;
  public assignpaper : AssignmentWithPapers = new AssignmentWithPapers;
  public id : number;

  assignment : Assignment = new Assignment;
  assignmentWithPapers: AssignmentWithPapers[] = [];
  assignmentWithPapers$: Observable<AssignmentWithPapers[]> = of(this.assignmentWithPapers);
  obs_creators$: Observable<StudentDTO>[] = [];

  ngOnInit() {

      this.currentUser = this.authService.currentUser;

      this.currentUser.subscribe(data => {this.currentTeacher = data.username.split("@")[0].substring(1,7);})


      this.firstParam = this.route.snapshot.queryParamMap.get('name');  
      this.route.params.subscribe (routeParams => {
      this.hreff = this.router.url;
      this.subject = this.hreff.substring(0,this.hreff.lastIndexOf('?'));
      this.hreff = this.hreff.substring(0,this.hreff.lastIndexOf('/'));
      this.href = this.subject; console.log(this.href);
      this.href2 = this.hreff + '/students';
      this.href3 = this.hreff + '/vms';
      this.courseName = this.route.snapshot.queryParamMap.get('name');
    });

      this.assignmentService.getCourseAssignments(this.courseName).subscribe(assignments => {

        assignments.forEach(assignment => {

          
          var element: AssignmentWithPapers = new AssignmentWithPapers();
          element.papersWithHistory = [];
          element.assignment = assignment;

          this.assignmentService.getAssignmentPapers(assignment.id).subscribe(papers => {

            papers.forEach(paper => {
              var paperWithHistory: PaperWithHistory = new PaperWithHistory();
              paperWithHistory.paper = paper;

              this.assignmentService.getPaperHistory(paper.id).subscribe(history =>{
                paperWithHistory.history = history;
                

                this.studentService.getOne(paper.creator).subscribe(student => {
                  console.log("!!!");
                  paperWithHistory.creator = student;
                  element.papersWithHistory.push(paperWithHistory);
                },
                )
              });
              
            });

            this.assignmentWithPapers.push(element);

          })

        })
        console.log(this.assignmentWithPapers);
        console.log(this.obs_creators$);

      })

}


receiveconsegna($event)
{

  var months = [
    'Gennaio',
    'Febbraio',
    'Marzo' ,
    'Aprile' ,
    'Maggio' ,
    'Giugno' ,
    'Luglio' ,
    'Agosto' ,
    'Settembre' ,
    'Ottobre' ,
    'Novembre',
    'Dicembre'
  ]
  console.log($event);


  let month = months.indexOf($event.mese);
  console.log(month);
  this.assignment.content = $event.content;
  this.assignment.creator = this.currentTeacher;
  this.creationDate.getTime();
  this.expiryDate.setDate($event.giorno);
  this.expiryDate.setMonth(month);
  this.expiryDate.setFullYear($event.anno);
  this.assignment.expiryDate = this.expiryDate;
  

  console.log(this.assignment);
 
  this.assignmentService.addAssignmentToCourse(this.assignment,this.courseName).subscribe(data => 
    
    {

      
      this.assignmentService.getCourseAssignments(this.courseName).subscribe(assignments => {

        assignments.forEach(assignment => {

            this.id = assignment.id;
        })


        this.assignmentService.getOne(this.id).subscribe (assignment => {

          this.assignpaper.assignment = assignment;
          this.assignpaper.papersWithHistory = null;
          this.assignmentWithPapers.push(this.assignpaper);
          this.assignmentWithPapers$ = of(this.assignmentWithPapers);
        })

      })
     
    });

}



}