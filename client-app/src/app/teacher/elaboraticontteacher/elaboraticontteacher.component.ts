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

@Component({
  selector: 'app-elaboraticontteacher',
  templateUrl: './elaboraticontteacher.component.html',
  styleUrls: ['./elaboraticontteacher.component.css']
})
export class ElaboraticontteacherComponent implements OnInit {

  constructor(private route: ActivatedRoute, private router : Router, private assignmentService: AssignmentService, private studentService: StudentService) { }

  
  firstParam : string = "";
  public hreff : string ="";
  public href :string ="";
  public href2 : string ="";
  public href3 : string ="";
  public subject : string ="";
  private courseName: String;

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
                })
              });
              
            });

            this.assignmentWithPapers.push(element);

          })

        })
        console.log(this.assignmentWithPapers);
        console.log(this.obs_creators$);

      })


    


    

  

}

}