package android.mad.assignment;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.google.android.gms.location.FusedLocationProviderClient;

public class NotificationService extends JobService {

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }



}
