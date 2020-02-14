package com.example.alarmmanagerproject.edit;

public interface EditContract {
    interface View  {
        void onTimeClick();
        void onDateClick();
        void onSubmit();
        void onPrioritySelected();
    }
}
