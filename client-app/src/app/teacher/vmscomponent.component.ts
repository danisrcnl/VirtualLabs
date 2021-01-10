import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Group } from '../group.model';
import { StudentService } from '../services/student.service';
import { Vms } from '../vms.model';
import { LimitDialogComponent } from './limit-dialog.component';


@Component({
  selector: 'app-vmscomponent',
  templateUrl: './vmscomponent.component.html',
  styleUrls: ['./vmscomponent.component.css']
})
export class VmscomponentComponent implements OnInit {
  href : string ="";
  vms : Vms[] = [];
  groups : Group[] = [];
  constructor(public dialog: MatDialog, private studentservice: StudentService,private router: Router, private activeRoute: ActivatedRoute) 
  
  {
    let id = 0;
   

      this.href = this.router.url;
      this.studentservice.getcourse().subscribe(data => {console.log (data)
        data.forEach(s => {
    
         s.path = '/' + s.path + '/vms';
     
           if (s.path == this.href)
           {
         
             id = s.id;
            
            }
    
        })});



    this.studentservice.getvms().subscribe(data1 => {console.log(data1)
    data1.forEach (s1 => {

 // console.log(s1.courseId);
  //console.log(id);

      if(s1.courseId == id)
      {
       this.vms.push(s1);
      }

    })
    
    })

    this.studentservice.getgroups().subscribe(data1 => {console.log(data1)
      data1.forEach (s1 => {
  
        if(s1.courseId == id)
        {
         this.groups.push(s1);
        }
  
      })
      
      })

  console.log(this.groups);
  console.log (this.vms);

  }

  ngOnInit(){


  }

  openlimitdialog()
  {
   
    this.dialog.open (LimitDialogComponent, { height: '300px',
    width: '400px',
    data : {
      dataKey: this.vms
  
    }
  
    });

  }

}
