import { Component, OnInit, Input, Output, EventEmitter, SimpleChange, SimpleChanges } from '@angular/core';
import { Student } from './student.model';
import {MatTableDataSource} from '@angular/material/table';
import {MatTableModule} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {FormBuilder, FormControl,FormGroup,Validators } from '@angular/forms';
import {Observable, zip } from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule,ReactiveFormsModule} from '@angular/forms';
import {MatAutocompleteModule} from '@angular/material/autocomplete'
import {MatInputModule} from '@angular/material/input';
import { SummaryResolver } from '@angular/compiler';
import {MatTabsModule} from '@angular/material/tabs';
import { Routes, RouterModule, Router, ActivatedRoute } from '@angular/router';
import { StudentsContComponent } from './students-cont.component';
import { PageNotFoundComponentComponent } from '../page-not-found-component/page-not-found-component.component';
import { Proposal } from '../model/proposal.model';
import { Team } from '../model/team.model';
import { MemberStatus } from '../model/memberstatus.model';
import { StudentDTO } from '../model/studentDTO.model';
import { Console } from 'console';
import { User } from 'app/auth/user';


  @Component({
    selector: 'app-students',
    templateUrl: './students.component.html',
    styleUrls: ['./students.component.css'],
    inputs :['tablevalue']
  })
export class StudentsComponent implements OnInit {
     
    
    @Output() addstudentEvent = new EventEmitter<StudentDTO>();
    
    @Output() removestudentsEvent = new EventEmitter<StudentDTO[]>();

    @Output() invitestudentEvent = new EventEmitter<String[]>();

    @Output() confirmrequestEventmatricola = new EventEmitter<String>();

    @Output() confirmrequestEventteamid = new EventEmitter<number>();

    @Output() groupName = new EventEmitter <String>();

    @Output() timeoutValue = new EventEmitter <Number>();

    compagnidigruppo$ : Observable<StudentDTO[]>;
    enrolledstudents : StudentDTO[];
    compagni : StudentDTO[];
    tablevalue$ : Observable<boolean>;
    compagniDTO : StudentDTO[];
    groupid : number;
    teams$ : Observable <Team[]>;
    teams : Team[];
    displayedColumns: string[] = ['select','name','firstname','serial'];
    displayedColumnsProposals: string[] = ['teamName', 'members'];
    headers = ['id','name','firstName'];
    headers1 = ['Serial','Name','Firstname'];
        
    selection = new SelectionModel<StudentDTO>(true, []);
    students : StudentDTO [];
    studentsIds : String[] = []; 
    membersStatus : MemberStatus[] = new Array<MemberStatus>();
    
    public dataSource2 = new MatTableDataSource<StudentDTO>(this.CompagniDTO);
   
    mycontrol = new FormControl();
    filteredOptions = new Observable<StudentDTO[]>();
    myForm : FormGroup;
    timeout : number;
    teamName : string ="";
    paramname : string = "";
    firstParam : string ="";
    public groupname : string = "";
    public href :string ="";
    public href2 : string ="";
    public href3 : string ="";
    public hreff : string ="";
    public subject : string ="";
    public value : boolean;
    public value1 : boolean;
    public tableclasses = { "hide" : false, "show" : false  }
    public compagniclass = { "hide" : false, "show2" : false }
    public groupclasses = { "hide" :true }

    private tablevalue : boolean;

    public get _enrolledstudents(): StudentDTO[] {
      return this.enrolledstudents;
    }

    public get _compagni(): StudentDTO[] {
      return this.compagni;
    }
    @Input('enrolledstudents')
    set enrolledStudents (students : StudentDTO[])
    {
        this.enrolledstudents = students;        
 
    }

    @Input('compagnidigruppo$')
    set Compagnidigruppo (comp : Observable<StudentDTO[]>)
    {
      this.compagnidigruppo$ = comp;
    }

    @Input('membersStatus')
    set membersstatus (memberss : MemberStatus[])
    {
      this.membersStatus = memberss;
    }

    @Input('tablevalue')
    set tableValue (val : boolean)
    {
      this.tablevalue = val;
    }

    set Groupclasses (val : boolean)
    {
      this.groupclasses.hide = val;
    }

  
    @Input('tablevalue$')
    set tableValue$ (val: Observable<boolean>)
    {
      this.tablevalue$ = val;
    }

    @Input ('compagniDTO')
    set CompagniDTO (students : StudentDTO[])
    {
      this.compagniDTO = students;
    }


    @Input ('teamName')
    set Groupid (name : string)
    {
      this.teamName = name;
    }
    
    @Input ('teams$')
    set Teams$ (val: Observable<Team[]>)
    {
      this.teams$ = val;
    }


    @Input ('teams')
    set Teams (val : Team[])
    {
      this.teams = val;
    }

   ngOnChanges (changes: SimpleChanges) {

    /*
    this.tableclasses.hide = changes.tableValue.currentValue;
    this.tableclasses.show = !changes.tableValue.currentValue;;
    if (changes.tableValue.currentValue)
    {
      this.compagniclass.hide = false;
      this.compagniclass.show2 = true;
    }
    else{
      this.compagniclass.hide = true;
      this.compagniclass.show2 = false;
    }
*/
  
   }

    public get _tablevalue(): boolean {
      return this.tablevalue;
    }

    
    @Input ('dataSource')
    dataSource = new MatTableDataSource<StudentDTO>(this.enrolledStudents);

    
    @Input('studenti') 
    studenti : StudentDTO [];
    
    @Input('currentStudent')
    currentStudent : StudentDTO;
  
    
    
constructor (private router : Router, private activeRoute: ActivatedRoute, private fb: FormBuilder) {
}




    ngOnInit() {
      this.myForm = this.fb.group({
        timeout: '',
        groupname: ''
      })
      

const queryParams = this.activeRoute.snapshot.queryParams
const routeParams = this.activeRoute.snapshot.params;

this.activeRoute.queryParams.subscribe (queryParams => {

this.teams$.subscribe(data => {
})




this.firstParam = this.activeRoute.snapshot.queryParamMap.get('name');


this.hreff = this.router.url;
  this.subject = this.hreff.substring(0,this.hreff.lastIndexOf('?'));
  this.hreff = this.hreff.substring(0,this.hreff.lastIndexOf('/'));
  this.href = this.subject; console.log(this.href);
   this.href2 = this.hreff + '/vms';
   this.href3 = this.hreff + '/elaborati';
   console.log(this.href2);
   
;

  
     this.enrolledstudents = Object.assign(this.enrolledstudents);
     this.dataSource = new MatTableDataSource<StudentDTO> (this.enrolledstudents);
     this.compagniDTO = Object.assign (this.compagniDTO);
     console.log(this.compagniDTO);
     this.dataSource2 = new MatTableDataSource<StudentDTO> (this.compagniDTO);
      this.filteredOptions = this.mycontrol.valueChanges.pipe(
        startWith(''),
        map (studenti => this._filter(studenti)));   

        });
    
      }

     
    isAllSelected() {
      
      const numSelected = this.selection.selected.length;
      const numRows = this.dataSource.data.length;
      return numSelected === numRows;
    }
  
    /** Selects all rows if they are not all selected; otherwise clear selection. */
    masterToggle() {
      this.isAllSelected() ?
          this.selection.clear() :
          this.dataSource.data.forEach(row => this.selection.select(row));
    }
    removeSelectedRows() {
      
      this.removestudentsEvent.emit(this.selection.selected);
      this.selection = new SelectionModel<StudentDTO>(true, []);
      
      this.filteredOptions = this.mycontrol.valueChanges.pipe(
        startWith(''),
        map (studenti => this._filter(studenti)));    

      
    }
  
  
  
  
  
  private _filter(studente: string): StudentDTO [] {
  const filterValue = studente.toLowerCase();
  
  return this.studenti.filter(option => this.displayFn(option).toLowerCase().includes(filterValue));
  
  }
  
  
    displayFn(studente: StudentDTO): string {
      return studente ? studente.name + ' ' + studente.firstName + ' '+ '(' + studente.id + ')': undefined;
    }
  
  studenteselezionato : StudentDTO;
  
  saveobject(event) {
    const selectedValue = event.option.value;
    console.log(selectedValue);
    
    this.studenteselezionato = Object.assign(selectedValue);
    
  console.log(this.enrolledstudents);
  
  }

  addstudent() {
  
    this.addstudentEvent.emit(this.studenteselezionato);
    console.log(this.studenti);

  }

  updateFilteredOptions() {
    this.filteredOptions = this.mycontrol.valueChanges.pipe(
      startWith(''),
      map(value => typeof value === 'string' ? value : this.displayFn(value)),
      map(displayedValue => this._filter(displayedValue))
    );
  }

  sendinvite() {
      
   
   this.students = this.selection.selected; 
   this.students.push(this.currentStudent);
   this.selection.clear();
   this.timeoutValue.emit(this.timeout);
   this.groupName.emit(this.groupname);
   
   this.students.forEach(x => {

   this.studentsIds.push(x.id.toString());
   })
   console.log(this.students);
   console.log(this.studentsIds);
   this.invitestudentEvent.emit(this.studentsIds);

  }

  accept(matricola,teamid)
  {
      console.log(matricola,teamid);
      this.confirmrequestEventmatricola.emit(matricola);
      this.confirmrequestEventteamid.emit(teamid);
  }

  reject (matricola,teamid)
  {
    
  }

  
}
