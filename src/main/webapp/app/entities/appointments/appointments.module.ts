import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MeetmonkSharedModule } from 'app/shared/shared.module';
import { AppointmentsComponent } from './appointments.component';
import { AppointmentsDetailComponent } from './appointments-detail.component';
import { AppointmentsUpdateComponent } from './appointments-update.component';
import { AppointmentsDeleteDialogComponent } from './appointments-delete-dialog.component';
import { appointmentsRoute } from './appointments.route';

@NgModule({
  imports: [MeetmonkSharedModule, RouterModule.forChild(appointmentsRoute)],
  declarations: [AppointmentsComponent, AppointmentsDetailComponent, AppointmentsUpdateComponent, AppointmentsDeleteDialogComponent],
  entryComponents: [AppointmentsDeleteDialogComponent]
})
export class MeetmonkAppointmentsModule {}
