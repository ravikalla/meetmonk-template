import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IAppointments, Appointments } from 'app/shared/model/appointments.model';
import { AppointmentsService } from './appointments.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-appointments-update',
  templateUrl: './appointments-update.component.html'
})
export class AppointmentsUpdateComponent implements OnInit {
  isSaving = false;

  users: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    subject: [],
    description: [],
    date: [null, [Validators.required]],
    duration: [null, [Validators.required]],
    notes: [],
    users: [null, Validators.required]
  });

  constructor(
    protected appointmentsService: AppointmentsService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appointments }) => {
      this.updateForm(appointments);

      this.userService
        .query()
        .pipe(
          map((res: HttpResponse<IUser[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: IUser[]) => (this.users = resBody));
    });
  }

  updateForm(appointments: IAppointments): void {
    this.editForm.patchValue({
      id: appointments.id,
      subject: appointments.subject,
      description: appointments.description,
      date: appointments.date != null ? appointments.date.format(DATE_TIME_FORMAT) : null,
      duration: appointments.duration,
      notes: appointments.notes,
      users: appointments.users
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appointments = this.createFromForm();
    if (appointments.id !== undefined) {
      this.subscribeToSaveResponse(this.appointmentsService.update(appointments));
    } else {
      this.subscribeToSaveResponse(this.appointmentsService.create(appointments));
    }
  }

  private createFromForm(): IAppointments {
    return {
      ...new Appointments(),
      id: this.editForm.get(['id'])!.value,
      subject: this.editForm.get(['subject'])!.value,
      description: this.editForm.get(['description'])!.value,
      date: this.editForm.get(['date'])!.value != null ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      duration: this.editForm.get(['duration'])!.value,
      notes: this.editForm.get(['notes'])!.value,
      users: this.editForm.get(['users'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppointments>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IUser): any {
    return item.id;
  }

  getSelected(selectedVals: IUser[], option: IUser): IUser {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
