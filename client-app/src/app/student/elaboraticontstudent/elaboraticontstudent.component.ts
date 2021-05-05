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
import { environment } from 'environments/environment';

@Component({
  selector: 'app-elaboraticontstudent',
  templateUrl: './elaboraticontstudent.component.html',
  styleUrls: ['./elaboraticontstudent.component.css']
})
export class ElaboraticontstudentComponent implements OnInit {

  constructor(private authService: AuthService, private route: ActivatedRoute, private router : Router, private assignmentService: AssignmentService, private studentService: StudentService) { }


  firstParam : string = "";
  public hreff : string ="";
  public href :string ="";
  public href2 : string ="";
  public href3 : string ="";
  public subject : string ="";
  private courseName: String;
  public papervalue : Paper;

  assignmentWithPapers: AssignmentWithPapers[] = [];
  assignmentWithPapers$: Observable<AssignmentWithPapers[]> = of(this.assignmentWithPapers);
  obs_creators$: Observable<StudentDTO>[] = [];

  ngOnInit() {



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

  this.authService.info().subscribe(info => {

    var idtmp: String[] = info.username.split('@', 1);
    console.log(idtmp + ", " + idtmp[0].length)
    var id: String = "";
    for (let i = 1; i<idtmp[0].length; i++)
      id += idtmp[0][i];

    this.assignmentService.getCourseAssignments(this.courseName).subscribe(assignments => {
      

      assignments.forEach(assignment => {
  
        console.log(assignment.id);

        var element: AssignmentWithPapers = new AssignmentWithPapers();
        element.papersWithHistory = [];
        element.assignment = assignment;

        this.assignmentService.getPaperStudent(assignment.id, id).subscribe(paper => {
          console.log(paper)
          
          var paperWithHistory: PaperWithHistory = new PaperWithHistory();
          paperWithHistory.paper = paper;
          console.log(paper.id)
  
          this.assignmentService.getPaperHistory(paper.id).subscribe(history => {
            console.log("history")
            paperWithHistory.history = history;
            
              
  
            this.studentService.getOne(paper.creator).subscribe(student => {
              paperWithHistory.creator = student;
              element.papersWithHistory.push(paperWithHistory);
            })
          
            
          });
  
          this.assignmentWithPapers.push(element);
  
        })
  
      })
      
  
    })

  })

  









}

}
