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
import {MatDialogModule} from '@angular/material/dialog';
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
    AppComponentTeacher
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
  
  ],

  providers: [
     { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
        { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    
    StudentService],
  bootstrap: [AppComponent],
  entryComponents : [LoginDialogComponent]
})
export class AppModule { }



