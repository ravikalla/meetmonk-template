import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAppointments } from 'app/shared/model/appointments.model';
import { AppointmentsService } from './appointments.service';

@Component({
  templateUrl: './appointments-delete-dialog.component.html'
})
export class AppointmentsDeleteDialogComponent {
  appointments?: IAppointments;

  constructor(
    protected appointmentsService: AppointmentsService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.appointmentsService.delete(id).subscribe(() => {
      this.eventManager.broadcast('appointmentsListModification');
      this.activeModal.close();
    });
  }
}
