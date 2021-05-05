import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { RouterModule} from '@angular/router';
import { AppRoutingModule, routes } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule} from '@angular/material/toolbar'
import { Component,ViewChild} from '@angular/core';
import { MatIconModule} from '@angular/material/icon';
import { MatSidenavModule} from '@angular/material/sidenav';
import { MatListModule} from '@angular/material/list';
import { MatTabsModule} from '@angular/material/tabs';
import { MatTableModule} from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule} from '@angular/material/form-field';
import { MatAutocompleteModule} from '@angular/material/autocomplete';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import { MatInputModule} from '@angular/material/input';
import { MatSelectModule} from '@angular/material/select';
import { StudentsComponent } from './student/students.component';
import { TeacherContComponent } from './teacher/teacher-cont.component';
import { HomeComponentComponent } from './home-component/home-component.component';
import { PageNotFoundComponentComponent } from './page-not-found-component/page-not-found-component.component';
import { VmsContcomponentComponent } from './teacher/vms-contcomponent.component';
import { StudentService } from './services/student.service';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import {MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA, MAT_DIALOG_DEFAULT_OPTIONS} from '@angular/material/dialog';
import {LoginDialogComponent} from './auth/login-dialog.component';
import { AuthModule } from './auth/auth.module';
import { RegisterComponent } from './auth/register/register.component';
import { SubjectdialogComponent } from './teacher/subjectdialog/subjectdialog.component';
import { StudentsContComponent } from './student/students-cont.component';
import {TeacherComponent} from './teacher/teacher.component';
import {VmsContcomponentComponent2} from './student/vms-contcomponent.component';
import { AppComponentStudent } from './student/app.component';
import { AppComponentTeacher } from './teacher/app.component';
import { VmscomponentComponent2} from './student/vmscomponentstudent';
import { VmscomponentComponent } from './teacher/vmscomponent.component';
import { LimitDialogComponent } from './student/limit-dialog.component';
import { JwtInterceptor } from './auth/interceptor/jwt.interceptor';
import { ErrorInterceptor } from './auth/interceptor/error.interceptor';
import { Popup } from './student/popup/popup.component';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import {MatChipsModule} from '@angular/material/chips';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatDatepickerModule} from '@angular/material/datepicker';
import { catchError } from 'rxjs/operators';
import { ElaboraticontstudentComponent } from './student/elaboraticontstudent/elaboraticontstudent.component';
import { ElaboratistudentComponent } from './student/elaboratistudent/elaboratistudent.component';
import { ElaboraticontteacherComponent } from './teacher/elaboraticontteacher/elaboraticontteacher.component';
import { ElaboratiteacherComponent } from './teacher/elaboratiteacher/elaboratiteacher.component';
import {MatExpansionModule} from '@angular/material/expansion';
import { ConsegnadialogComponent } from './teacher/consegnadialog/consegnadialog.component';
import { ViewPaperComponent } from './view-paper/view-paper.component';
import { ErrorDialog, VmService } from './services/vm.service';
import { Vms } from './model/vms.model';
import { CreatePaperComponent } from './create-paper/create-paper.component';



@NgModule({
  declarations: [
    AppComponent,
    StudentsComponent,
    TeacherContComponent,
    HomeComponentComponent,
    PageNotFoundComponentComponent,
    VmsContcomponentComponent,
    SubjectdialogComponent,
    VmscomponentComponent2,
    VmscomponentComponent,
    VmsContcomponentComponent2,
    LimitDialogComponent,
    StudentsContComponent,
    TeacherComponent,
    AppComponentStudent,
    AppComponentTeacher,
    Popup,
    ElaboraticontstudentComponent,
    ElaboratistudentComponent,
    ElaboraticontteacherComponent,
    ElaboratiteacherComponent,
    ConsegnadialogComponent,
    ViewPaperComponent,
    ErrorDialog,
    CreatePaperComponent,
    
  
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatTabsModule,
    MatTableModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatAutocompleteModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatSelectModule,
    RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'}),
    HttpClientModule,
    MatDialogModule,
    AuthModule,
    MatCardModule,
    MatButtonModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatExpansionModule,
    MatChipsModule,
    MatDatepickerModule
  ],

  providers: [

    VmService,
     { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        { provide : MatDialogRef, useValue: {}},],
    
  bootstrap: [AppComponent],
  entryComponents : [LoginDialogComponent,ErrorDialog]
})
export class AppModule { }



