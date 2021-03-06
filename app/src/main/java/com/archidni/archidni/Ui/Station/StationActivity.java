package com.archidni.archidni.Ui.Station;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.archidni.archidni.IntentUtils;
import com.archidni.archidni.Model.Coordinate;
import com.archidni.archidni.Model.Places.MainActivityPlace;
import com.archidni.archidni.Model.Places.Parking;
import com.archidni.archidni.Model.Places.PathPlace;
import com.archidni.archidni.Model.StringUtils;
import com.archidni.archidni.Model.Transport.Line;
import com.archidni.archidni.Model.Transport.Schedule.TrainSchedule;
import com.archidni.archidni.Model.Transport.Station;
import com.archidni.archidni.Model.TransportMean;
import com.archidni.archidni.R;
import com.archidni.archidni.Ui.Adapters.LineAdapter;
import com.archidni.archidni.Ui.Adapters.PlaceAdapter;
import com.archidni.archidni.Ui.Adapters.TrainTripAdapter;
import com.archidni.archidni.Ui.Adapters.TramwayMetroTripAdapter;
import com.archidni.archidni.Ui.Line.LineActivity;
import com.archidni.archidni.Ui.Line.Schedule.TrainTrip.TrainTripActivity;
import com.archidni.archidni.Ui.Parking.ParkingActivity;
import com.archidni.archidni.Ui.PathSearch.PathSearchActivity;
import com.archidni.archidni.UiUtils.ArchidniGoogleMap;
import com.archidni.archidni.UiUtils.ViewUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StationActivity extends AppCompatActivity implements StationContract.View {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.text_name)
    TextView mainText;
    @BindView(R.id.text_coordinate)
    TextView coordinateText;
    @BindView(R.id.layout_path)
    View pathLayout;
    @BindView(R.id.layout_lines)
    View linesLayout;
    @BindView(R.id.layout_times)
    View timesLayout;
    @BindView(R.id.text_times)
    TextView timesText;
    @BindView(R.id.text_lines)
    TextView linesText;
    @BindView(R.id.layout_trip_options)
    View tripOptionsLayout;
    @BindView(R.id.text_time)
    TextView timeText;
    @BindView(R.id.text_date)
    TextView dateText;
    @BindView(R.id.layout_nearby)
    View nearbyLayout;
    @BindView(R.id.text_nearby)
    TextView nearbyText;
    @BindView(R.id.text_search_error)
    TextView errorText;
    @BindView(R.id.button_retry)
    Button retryButton;

    ArchidniGoogleMap archidniMap;
    StationContract.Presenter presenter;
    private FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Station station = Station.fromJson(getIntent().getExtras()
                .getString(IntentUtils.STATION_STATION));
        presenter = new StationPresenter(station,this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        initViews(savedInstanceState);
        presenter.onCreate(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null)
                    presenter.onUserLocationCaptured(new Coordinate(location.getLatitude(),
                            location.getLongitude()));
            }
        });
    }

    private void initViews (Bundle savedInstanceState)
    {
        ButterKnife.bind(this);
        MapFragment mapView = (MapFragment) getFragmentManager().findFragmentById(R.id.mapView);
        archidniMap = new ArchidniGoogleMap(this, mapView, new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                presenter.onMapReady();
                archidniMap.getMap().getUiSettings().setAllGesturesEnabled(false);
                archidniMap.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return true;
                    }
                });
            }
        });
        pathLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onGetPathClicked();
            }
        });
        linesLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    linesLayout.performClick();
                }
            }
        });

        linesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleSelectedItem(StationPresenter.LINES);
            }
        });
        timesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleSelectedItem(StationPresenter.TRIPS);
            }
        });
        nearbyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleSelectedItem(StationPresenter.NEARBY_PLACES);
            }
        });

        timesLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    timesLayout.performClick();
                }
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onRetryClick(StationActivity.this);
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onTimeUpdateClick();
            }
        });
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onDateUpdateClick();
            }
        });

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void setTheme(Station station) {
        setTheme(station.getTransportMean().getTheme());
    }

    @Override
    public void showLinesLoadingBar() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLinesOnList(final ArrayList<Line> lines) {

        tripOptionsLayout.setVisibility(View.GONE);
        LineAdapter lineAdapter = new LineAdapter(this, lines, new LineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Line line) {
                presenter.onLineItemClick(line);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(lineAdapter);
        progressBar.setVisibility(View.GONE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setVisibility(View.VISIBLE);
            }
        },250);
    }

    @Override
    public void showTripsOnList(Station station,ArrayList<Line>lines,long departureTime,
                                long departureDate) {
        if (station.getTransportMean().getId()==TransportMean.ID_TRAIN)
        {
            TrainTripAdapter trainTripAdapter = new TrainTripAdapter(this, departureTime,
                    departureDate, station, lines, new TrainTripAdapter.OnTrainScheduleClick() {
                @Override
                public void onTrainScheduleClick(TrainSchedule trainSchedule) {
                    presenter.onTrainScheduleClick(trainSchedule);
                }
            });

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(trainTripAdapter);
            recyclerView.setNestedScrollingEnabled(false);
            tripOptionsLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            TramwayMetroTripAdapter tramwayMetroAdapter = new TramwayMetroTripAdapter(this,lines,
                    departureDate);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(tramwayMetroAdapter);
            tripOptionsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void startTrainTripActivity(TrainSchedule trainSchedule) {
        Intent intent = new Intent(this, TrainTripActivity.class);
        intent.putExtra(IntentUtils.SCHEDULE,new Gson().toJson(trainSchedule));
        startActivity(intent);
    }

    @Override
    public void showErrorLayout() {
        errorText.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideErrorLayout() {
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
    }

    @Override
    public void showProgressLayout() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressLayout() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void startPlaceActivity(MainActivityPlace place) {
        Parking parking = (Parking) place;
        Intent intent = new Intent(this, ParkingActivity.class);
        intent.putExtra(IntentUtils.PARKING,parking.toJson());
        startActivity(intent);
    }

    @Override
    public void showNearbyPlacesOnList(Station station, ArrayList<MainActivityPlace> mainActivityPlaces) {
        tripOptionsLayout.setVisibility(View.GONE);
        PlaceAdapter placeAdapter = new PlaceAdapter(this, mainActivityPlaces, station.getCoordinate()
                , new PlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MainActivityPlace mainListPlace) {
                presenter.onPlaceClick(mainListPlace);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(placeAdapter);
    }

    @Override
    public void showTimeDialog(long departureTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(departureTime*1000);
        TimePickerDialog dialogFragment = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        presenter.updateTime(hourOfDay * 3600 + minute * 60);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        dialogFragment.show();
    }

    @Override
    public void showDateDialog(long departureDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(departureDate*1000);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, dayOfMonth, 0, 0);
                        presenter.updateDate(c.getTimeInMillis() / 1000);
                    }
                }, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void updateTime(long newTime) {
        timeText.setText(StringUtils.getTimeString(newTime));
    }

    @Override
    public void updateDate(long newDate) {
        dateText.setText(StringUtils.getDateString(newDate));
    }

    @Override
    public void startPathSearchActivity(PathPlace origin, PathPlace destination) {
        Intent intent = new Intent(this, PathSearchActivity.class);
        if (origin!=null)
            intent.putExtra(IntentUtils.PATH_SEARCH_ORIGIN,origin.toJson());
        else
            intent.putExtra(IntentUtils.PATH_SEARCH_ORIGIN,new Gson().toJson(null));
        intent.putExtra(IntentUtils.PATH_SEARCH_DESTINATION,destination.toJson());
        startActivity(intent);
    }

    public void hideTitleText(final Station station) {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(station.getName());
                    pathLayout.setVisibility(View.GONE);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                    pathLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void showStationOnActivity(Station station) {
        mainText.setText(station.getName());
        coordinateText.setText(StringUtils.getLocationString(station.getCoordinate()));
        ViewUtils.changeTextViewState(this,coordinateText,station.getTransportMean()
                .getCoordinateDrawable(),ViewUtils.DIRECTION_LEFT);
        pathLayout.setBackgroundColor(ContextCompat.getColor(this,
                station.getTransportMean().getColor()));
        hideTitleText(station);
    }

    @Override
    public void showStationOnMap(Station station) {
        archidniMap.addMarker(station.getCoordinate(),station.getTransportMean().getMarkerIcon());
        archidniMap.moveCamera(station.getCoordinate(),15);
    }

    @Override
    public void updateLinesTripsLayout(int selectedItem,Station station) {

        switch (selectedItem)
        {
            case StationPresenter.LINES : ViewUtils.changeTextViewState(this,linesText,
                    station.getTransportMean().getLinesSelectedDrawable()
                    ,station.getTransportMean().getColor(),ViewUtils.DIRECTION_RIGHT);
                ViewUtils.changeTextViewState(this,timesText,
                        R.drawable.ic_time_disabled,R.color.black
                        ,ViewUtils.DIRECTION_RIGHT);
                ViewUtils.changeTextViewState(this,nearbyText,R.drawable.ic_near_me_black_24dp,
                        R.color.black,ViewUtils.DIRECTION_RIGHT);
                break;
            case StationPresenter.TRIPS:

                    ViewUtils.changeTextViewState(this,linesText,
                            R.drawable.ic_line_disabled
                            ,R.color.black,ViewUtils.DIRECTION_RIGHT);
                    ViewUtils.changeTextViewState(this,timesText,station.getTransportMean()
                                    .getTimesSelectedDrawable(),station.getTransportMean().getColor(),
                            ViewUtils.DIRECTION_RIGHT);
                    ViewUtils.changeTextViewState(this,nearbyText,R.drawable.ic_near_me_black_24dp,
                            R.color.black,ViewUtils.DIRECTION_RIGHT);
                    break;
                    case StationPresenter.NEARBY_PLACES:
                        ViewUtils.changeTextViewState(this,linesText,
                                R.drawable.ic_line_disabled
                                ,R.color.black,ViewUtils.DIRECTION_RIGHT);
                        ViewUtils.changeTextViewState(this,timesText,
                                R.drawable.ic_time_disabled,R.color.black
                                ,ViewUtils.DIRECTION_RIGHT);
                        ViewUtils.changeTextViewState(this,nearbyText,station.getTransportMean().getNearbyDrawable(),
                                station.getTransportMean().getColor(),ViewUtils.DIRECTION_RIGHT);

        }
    }

    @Override
    public void startLineActivity(Line line) {
        Intent intent = new Intent(this, LineActivity.class);
        intent.putExtra(IntentUtils.LINE_LINE,line.toJson());
        startActivity(intent);
    }

    @Override
    public void hideTimeText() {
        timeText.setVisibility(View.GONE);
    }

    @Override
    public void startStationActivity(Station station) {
        Intent intent = new Intent(this,StationActivity.class);
        intent.putExtra(IntentUtils.STATION_STATION,station.toJson());
        startActivity(intent);
    }

}
