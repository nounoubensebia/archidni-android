package com.archidni.archidni.Ui.Main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.archidni.archidni.Data.SharedPrefsUtils;
import com.archidni.archidni.GeoUtils;
import com.archidni.archidni.IntentUtils;
import com.archidni.archidni.LocationListener;
import com.archidni.archidni.Model.Coordinate;
import com.archidni.archidni.Model.Interests.ParkingItem;
import com.archidni.archidni.Model.Places.MainActivityPlace;
import com.archidni.archidni.Model.Places.Parking;
import com.archidni.archidni.Model.Places.PathPlace;
import com.archidni.archidni.Model.StringUtils;
import com.archidni.archidni.Model.Transport.Line;
import com.archidni.archidni.Model.Transport.Station;
import com.archidni.archidni.Model.User;
import com.archidni.archidni.TimeMonitor;
import com.archidni.archidni.Ui.Adapters.LineAdapter;
import com.archidni.archidni.Ui.Adapters.PlaceAdapter;
import com.archidni.archidni.Ui.ExchangePolesActivity;
import com.archidni.archidni.Ui.Favorites.FavoritesActivity;
import com.archidni.archidni.Ui.Line.LineActivity;
import com.archidni.archidni.Ui.Login.AccountActivity;
import com.archidni.archidni.Ui.Login.LoginActivity;
import com.archidni.archidni.Ui.Notifications.NotificationsActivity;
import com.archidni.archidni.Ui.Parking.ParkingActivity;
import com.archidni.archidni.Ui.ParkingsActivity;
import com.archidni.archidni.Ui.PathSearch.PathSearchActivity;
import com.archidni.archidni.Ui.RealtimeBus.RealTimeBusActivity;
import com.archidni.archidni.Ui.Report.ReportChooseTypeActivity;
import com.archidni.archidni.Ui.Search.SearchActivity;
import com.archidni.archidni.Ui.SearchLineStation.SearchLineStationActivity;
import com.archidni.archidni.Ui.Settings.SettingsActivity;
import com.archidni.archidni.Ui.Signup.SignupActivity;
import com.archidni.archidni.Ui.Station.StationActivity;
import com.archidni.archidni.Ui.TarifsActivity;
import com.archidni.archidni.UiUtils.ArchidniClusterItem;
import com.archidni.archidni.UiUtils.ArchidniGoogleMap;
import com.archidni.archidni.R;
import com.archidni.archidni.Model.TransportMean;
import com.archidni.archidni.UiUtils.ClusterHandler;
import com.archidni.archidni.UiUtils.DialogUtils;
import com.archidni.archidni.UiUtils.NestedScrollableHelper;
import com.archidni.archidni.UiUtils.SelectorItem;
import com.archidni.archidni.UiUtils.TransportMeansSelector;
import com.archidni.archidni.UiUtils.ViewUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainContract.View,NavigationView.OnNavigationItemSelectedListener {
    MainContract.Presenter presenter;

    @BindView(R.id.text_transport_mean_0)
    TextView transportMean0Text;
    @BindView(R.id.text_transport_mean_1)
    TextView transportMean1Text;
    @BindView(R.id.text_transport_mean_2)
    TextView transportMean2Text;
    @BindView(R.id.text_transport_mean_3)
    TextView transportMean3Text;
    @BindView(R.id.text_transport_mean_4)
    TextView transportMean4Text;
    @BindView(R.id.text_stations)
    TextView stationsText;
    @BindView(R.id.text_lines)
    TextView linesText;
    @BindView(R.id.layout_lines)
    View linesLayout;
    @BindView(R.id.layout_stations)
    View stationsLayout;
    @BindView(R.id.layout_search)
    View searchLayout;
    @BindView(R.id.fab_my_location)
    View myLocationFab;
    @BindView(R.id.fab_show_sliding_panel)
    View showSlidingUpPanelFab;
    @BindView(R.id.container)
    SlidingUpPanelLayout container;
    @BindView(R.id.layout_location)
    View locationLayout;
    @BindView(R.id.text_main)
    TextView mainText;
    @BindView(R.id.text_secondary)
    TextView secondaryText;
    @BindView(R.id.text_duration_distance)
    TextView durationDistanceText;
    @BindView(R.id.layout_get_path)
    View getPathLayout;
    @BindView(R.id.layout_drawer)
    DrawerLayout drawerLayout;
    @BindView(R.id.image_open_drawer)
    View openDrawerImage;
    @BindView(R.id.layout_search_underway)
    View searchUnderwayLayout;
    @BindView(R.id.text_parking)
    TextView parkingText;

    @BindView(R.id.image_transport_mean_icon)
    ImageView locationIcon;
    @BindView(R.id.layout_zoom_insufficient_message)
    View zoomInsufficientLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fab_retry)
    View retryFab;
    @BindView(R.id.layout_search_error)
    View errorLayout;
    @BindView(R.id.text_find_lines_stations)
    TextView findLinesStationsText;
    @BindView(R.id.layout_overlay)
    View overlayLayout;

    @BindView(R.id.text_list_type)
    TextView listTypeText;

    @BindView(R.id.layout_interests)
    View interestsLayout;
    @BindView(R.id.text_interests)
    TextView interestsText;
    @BindView(R.id.text_hospital)
    TextView hospitalText;

    TextView usernameText;

    @BindView(R.id.scrollView)
    NestedScrollView nestedScrollView;

    ArchidniGoogleMap archidniMap;
    private boolean drawerOpened;
    private LocationListener locationListener;
    private Dialog disconnectProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Mapbox.getInstance(this, "pk.eyJ1Ijoibm91bm91OTYiLCJhIjoiY2o0Z29mMXNsMDVoazMzbzI1NTJ1MmRqbCJ9.CXczOhM2eznwR0Mv6h2Pgg");
        setContentView(R.layout.activity_main);
        initViews(savedInstanceState);
        User user = User.fromJson(SharedPrefsUtils.loadString(this,
                SharedPrefsUtils.SHARED_PREFS_ENTRY_USER_OBJECT));
        locationListener.listenForLocationUpdates(new LocationListener.OnUserLocationUpdated() {
            @Override
            public void onUserLocationUpdated(Coordinate userLocation) {
                presenter.onUserLocationUpdated(userLocation);
            }
        });
        locationListener.getLastKnownUserLocation(new LocationListener.OnUserLocationUpdated() {
            @Override
            public void onUserLocationUpdated(Coordinate userLocation) {
                presenter.onFirstLocationCaptured(userLocation);
            }
        });
        presenter = new MainPresenter(this,user);



    }

    private void createClusters ()
    {
        for (SelectorItem selectorItem:SelectorItem.allItems)
        {
            archidniMap.addCluster(MainActivity.this,
                     new ClusterHandler.OnClusterItemClickListener() {
                        @Override
                        public void onClusterItemClick(ArchidniClusterItem archidniClusterItem, Marker marker) {
                            if (archidniClusterItem.getTag() instanceof Station)
                                presenter.onStationMarkerClick((Station) archidniClusterItem.getTag(),marker,archidniClusterItem);
                            if (archidniClusterItem.getTag() instanceof Parking)
                                presenter.onParkingMarkerClick((Parking) archidniClusterItem.getTag(),marker,archidniClusterItem);
                        }
                    });
        }
    }

    private void initViews(Bundle savedInstanceState)
    {
        ButterKnife.bind(this);
        locationListener = new LocationListener(this);
        MapFragment mapView = (MapFragment) getFragmentManager().findFragmentById(R.id.mapView);
        archidniMap = new ArchidniGoogleMap(this, mapView, new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                archidniMap.setMyLocationEnabled(true);
                createClusters();
                presenter.onMapReady(MainActivity.this,
                        archidniMap.getMap().getProjection().getVisibleRegion().latLngBounds,
                        archidniMap.getCenter());

                archidniMap.setOnCameraIdle(new ArchidniGoogleMap.OnCameraIdle() {

                    @Override
                    public void onCameraIdle(Coordinate coordinate, LatLngBounds latLngBounds, double zoom) {
                        presenter.onCameraMove(MainActivity.this, coordinate, (float) zoom, latLngBounds);
                    }
                });

                archidniMap.setOnMapShortClickListener(new ArchidniGoogleMap.OnMapShortClickListener() {
                    @Override
                    public void onMapShortClick(Coordinate coordinate) {
                        presenter.onMapShortClick();
                    }
                });
            }
        },
                new ArchidniGoogleMap.OnMapLoaded() {
                    @Override
                    public void onMapLoaded(Coordinate coordinate, LatLngBounds latLngBounds, double zoom) {
                        presenter.onMapLoaded(coordinate,latLngBounds,zoom);
                    }
                });

        transportMean0Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleTransportMean(0);
            }
        });
        transportMean1Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleTransportMean(1);
            }
        });
        transportMean2Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleTransportMean(2);
            }
        });
        transportMean3Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleTransportMean(3);
            }
        });
        transportMean4Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleTransportMean(4);
            }
        });
        parkingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleTransportMean(5);
            }
        });
        hospitalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleTransportMean(6);
            }
        });
        stationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleStationsLines(MainPresenter.STATIONS_SELECTED);
            }
        });


        interestsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleStationsLines(MainPresenter.INTERESTS_SELECTED);
            }
        });
        linesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.toggleStationsLines(MainPresenter.LINES_SELECTED);
            }
        });
        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onSearchClicked();
            }
        });


        myLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onMyLocationFabClick();
            }
        });
        showSlidingUpPanelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onShowSlidingPanelFabClick();
            }
        });
        getPathLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onSearchPathClick();
            }
        });
        openDrawerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
                container.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                drawerOpened = true;
                hideOverlayLayout();
                slideOutSearchText();
            }
        });
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerOpened = false;
                container.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                getPathLayout.setVisibility(View.VISIBLE);
                myLocationFab.setVisibility(View.VISIBLE);
                showSlidingUpPanelFab.setVisibility(View.VISIBLE);
                showOverlayLayout();
                slideInSearchText();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        container.setScrollableViewHelper(new NestedScrollableHelper());
        container.setScrollableView(nestedScrollView);
        nestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        retryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onRetryClicked(MainActivity.this,archidniMap.getCenter());
            }
        });
        findLinesStationsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onLinesStationsFindClick();
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        usernameText = (TextView) headerView.findViewById(R.id.text_username);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop(this.getApplicationContext());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void updateMeansSelectionLayout(TransportMeansSelector transportMeansSelector) {
        if (transportMeansSelector.isItemSelected(0))
        {
            ViewUtils.changeTextViewState(this,transportMean0Text,
                    TransportMean.allTransportMeans.get(0).getIconEnabled(),
                    R.color.color_transport_mean_selected_0
                    ,ViewUtils.DIRECTION_UP);
        }
        else
        {
            ViewUtils.changeTextViewState(this,transportMean0Text,
                    TransportMean.allTransportMeans.get(0).getIconDisabled(),
                    R.color.color_transport_mean_not_selected
                    ,ViewUtils.DIRECTION_UP);
        }
        if (transportMeansSelector.isItemSelected(1))
        {
            ViewUtils.changeTextViewState(this,transportMean1Text,
                    TransportMean.allTransportMeans.get(1).getIconEnabled(),
                    R.color.color_transport_mean_selected_1
                    ,ViewUtils.DIRECTION_UP);
        }
        else
        {
            ViewUtils.changeTextViewState(this,transportMean1Text,
                    TransportMean.allTransportMeans.get(1).getIconDisabled(),
                    R.color.color_transport_mean_not_selected
                    ,ViewUtils.DIRECTION_UP);
        }
        if (transportMeansSelector.isItemSelected(2))
        {
            ViewUtils.changeTextViewState(this,transportMean2Text,
                    TransportMean.allTransportMeans.get(2).getIconEnabled(),
                    R.color.color_transport_mean_selected_2
                    ,ViewUtils.DIRECTION_UP);
        }
        else
        {
            ViewUtils.changeTextViewState(this,transportMean2Text,
                    TransportMean.allTransportMeans.get(2).getIconDisabled(),
                    R.color.color_transport_mean_not_selected
                    ,ViewUtils.DIRECTION_UP);
        }
        if (transportMeansSelector.isItemSelected(3))
        {
            ViewUtils.changeTextViewState(this,transportMean3Text,
                    TransportMean.allTransportMeans.get(3).getIconEnabled(),
                    R.color.color_transport_mean_selected_3
                    ,ViewUtils.DIRECTION_UP);
        }
        else
        {
            ViewUtils.changeTextViewState(this,transportMean3Text,
                    TransportMean.allTransportMeans.get(3).getIconDisabled(),
                    R.color.color_transport_mean_not_selected
                    ,ViewUtils.DIRECTION_UP);
        }

        if (transportMeansSelector.isItemSelected(4))
        {
            ViewUtils.changeTextViewState(this,transportMean4Text,
                    TransportMean.allTransportMeans.get(4).getIconEnabled(),
                    R.color.color_transport_mean_selected_4
                    ,ViewUtils.DIRECTION_UP);
        }
        else
        {
            ViewUtils.changeTextViewState(this,transportMean4Text,
                    TransportMean.allTransportMeans.get(4).getIconDisabled(),
                    R.color.color_transport_mean_not_selected
                    ,ViewUtils.DIRECTION_UP);
        }
        if (transportMeansSelector.isItemSelected(5))
        {
            ViewUtils.changeTextViewState(this,parkingText,
                    SelectorItem.allItems.get(5).getIconEnabled(),
                    SelectorItem.allItems.get(5).getColor()
                    ,ViewUtils.DIRECTION_UP);
        }
        else
        {
            ViewUtils.changeTextViewState(this,parkingText,
                    SelectorItem.allItems.get(5).getIconDisabled(),
                    R.color.color_transport_mean_not_selected
                    ,ViewUtils.DIRECTION_UP);
        }
        if (transportMeansSelector.isItemSelected(6))
        {
            ViewUtils.changeTextViewState(this,hospitalText,
                    SelectorItem.allItems.get(6).getIconEnabled(),
                    SelectorItem.allItems.get(6).getColor()
                    ,ViewUtils.DIRECTION_UP);
        }
        else
        {
            ViewUtils.changeTextViewState(this,hospitalText,
                    SelectorItem.allItems.get(6).getIconDisabled(),
                    R.color.color_transport_mean_not_selected
                    ,ViewUtils.DIRECTION_UP);
        }


    }

    @Override
    public void updateStationsLinesLayout(int selectedItem) {
        if (selectedItem==MainPresenter.STATIONS_SELECTED)
        {
            ViewUtils.changeTextViewState(this,
                    stationsText,
                    R.drawable.ic_station_enabled,
                    R.color.colorGreen,
                    ViewUtils.DIRECTION_RIGHT);
            listTypeText.setText("Stations à proximité du centre de la carte :");
        }
        else
        {
            ViewUtils.changeTextViewState(this,
                    stationsText,
                    R.drawable.ic_station_disabled,
                    R.color.black,
                    ViewUtils.DIRECTION_RIGHT);
        }

        if (selectedItem == MainPresenter.LINES_SELECTED)
        {
            ViewUtils.changeTextViewState(this,
                    linesText,
                    R.drawable.ic_line_enabled,
                    R.color.colorGreen,
                    ViewUtils.DIRECTION_RIGHT);
            listTypeText.setText("Lignes à proximité du centre de la carte :");
        }
        else
        {
            ViewUtils.changeTextViewState(this,
                    linesText,
                    R.drawable.ic_line_disabled,
                    R.color.black,
                    ViewUtils.DIRECTION_RIGHT);
        }

        if (selectedItem == MainPresenter.INTERESTS_SELECTED)
        {
            ViewUtils.changeTextViewState(this,
                    interestsText,
                    R.drawable.ic_star_enabled_24dp,
                    R.color.colorGreen,
                    ViewUtils.DIRECTION_RIGHT);
            listTypeText.setText("Lieux d'intérêt à proximité du centre de la carte :");
        }
        else
        {
            ViewUtils.changeTextViewState(this,
                    interestsText,
                    R.drawable.ic_star_disabled_24dp,
                    R.color.black,
                    ViewUtils.DIRECTION_RIGHT);
        }


    }

    @Override
    public void startSearchActivity(PathPlace userLocation) {
        Intent intent = new Intent(this, SearchActivity.class);
        String json = new Gson().toJson(userLocation);
        intent.putExtra(IntentUtils.LOCATION,json);
        intent.putExtra(IntentUtils.SearchIntents.EXTRA_REQUEST_TYPE,
                IntentUtils.SearchIntents.TYPE_LOOK_ONLY_FOR_DESTINATION);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showLocationLayout(MainActivityPlace mapPlace, Marker oldSelectedMarker,
                                   MainActivityPlace oldMapPlace,Marker marker,Coordinate userLocation) {
        container.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        mainText.setText(mapPlace.getName());
        secondaryText.setText(mapPlace.getDescription());
        slideOutSearchText();
        if (userLocation!=null)
        {
            durationDistanceText.setText(StringUtils
                    .getTextFromDistance(GeoUtils.distance(userLocation,
                            mapPlace.getCoordinate()))+", "+
                    StringUtils.getTextFromDuration(GeoUtils.getOnFootDuration(userLocation,
                            mapPlace.getCoordinate()))+" de marche");

        }
        else
        {
            durationDistanceText.setText("");
        }

        if (oldSelectedMarker!=null)
        {
            archidniMap.changeMarkerIcon(oldSelectedMarker,oldMapPlace.getMarkerResource());
        }

        locationLayout.setVisibility(View.VISIBLE);
        showSlidingUpPanelFab.setVisibility(View.GONE);
        myLocationFab.setVisibility(View.GONE);
        container.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                container.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        },250);
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onPlaceClick();
            }
        });
        locationIcon.setImageDrawable(ContextCompat.getDrawable(this,mapPlace.getMarkerResource()));
        getPathLayout.setBackgroundColor(ContextCompat.getColor(this,mapPlace.getColor()));
        archidniMap.changeMarkerIcon(marker,mapPlace.getSelectedMarkerResource());
    }

    @Override
    public void hideLocationLayout(Marker marker,ArchidniClusterItem archidniClusterItem) {
        container.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        myLocationFab.setVisibility(View.VISIBLE);
        showSlidingUpPanelFab.setVisibility(View.VISIBLE);
        locationLayout.setVisibility(View.GONE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                slideInSearchText();
            }
        },250);
        MainActivityPlace mapPlace = (MainActivityPlace) archidniClusterItem.getTag();
        archidniMap.changeMarkerIcon(marker,mapPlace.getMarkerResource());
    }

    @Override
    public void startPathSearchActivity(PathPlace origin, PathPlace destination) {
        /*TODO check if null*/
        Intent intent = new Intent(this, PathSearchActivity.class);
        if (origin!=null)
            intent.putExtra(IntentUtils.PATH_SEARCH_ORIGIN,origin.toJson());
        else
            intent.putExtra(IntentUtils.PATH_SEARCH_ORIGIN,new Gson().toJson(null));
        intent.putExtra(IntentUtils.PATH_SEARCH_DESTINATION,destination.toJson());
        startActivity(intent);
    }

    @Override
    public void showLinesLoadingLayout() {
        searchUnderwayLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLinesLoadingLayout() {
        searchUnderwayLayout.setVisibility(View.GONE);
    }

    @Override
    public void startParkingActivity(Parking parking) {
        Intent intent = new Intent(this, ParkingActivity.class);
        intent.putExtra(IntentUtils.PARKING,parking.toJson());
        startActivity(intent);
    }


    @Override
    public void showPlacesOnMap(ArrayList<? extends MainActivityPlace> places,TransportMeansSelector transportMeansSelector) {
        TimeMonitor timeMonitor = TimeMonitor.initTimeMonitor();
        for(MainActivityPlace place:places)
        {
            if (place instanceof Station)
            {
                Station station = (Station) place;
                archidniMap.prepareClusterItem(station.getCoordinate(),
                        station.getTransportMean().getMarkerIcon(),
                        station.getTransportMean().getId(),station);
                //archidniMap.addMarker(station.getCoordinate(),station.getMarkerDrawable());
            }
            else
            {
                Parking parking = (Parking) place;
                archidniMap.prepareClusterItem(parking.getCoordinate(),
                        R.drawable.marker_parking,5,parking);
                //archidniMap.addMarker(parking.getCoordinate(),R.drawable.marker_parking);
            }
        }
        Log.i("Adding items time",timeMonitor.getElapsedTime()+"");
        archidniMap.initClusters();
        archidniMap.renderClusters();
        archidniMap.addPreparedAnnotations();
    }

    @Override
    public void updatePlacesOnMap(ArrayList<? extends MainActivityPlace> places, TransportMeansSelector newTransportMeansSelector,
                                  TransportMeansSelector oldTransportMeanSelector) {
        for (SelectorItem selectorItem:SelectorItem.allItems)
        {
            if (!newTransportMeansSelector.isItemSelected(selectorItem.getId()))
            {
                archidniMap.removeAllClusterItems(selectorItem.getId());
            }
        }

        for(MainActivityPlace place:places)
        {
            if (place instanceof Station)
            {
                Station station = (Station) place;
                if (!oldTransportMeanSelector.isItemSelected(station.getTransportMean().getId()))
                archidniMap.prepareClusterItem(station.getCoordinate(),
                        station.getTransportMean().getMarkerIcon(),
                        station.getTransportMean().getId(),station);
                //archidniMap.addMarker(station.getCoordinate(),station.getMarkerDrawable());
            }
            else
            {
                Parking parking = (Parking) place;
                if (!oldTransportMeanSelector.isItemSelected(ParkingItem.PARKING_ID))
                archidniMap.prepareClusterItem(parking.getCoordinate(),
                        R.drawable.marker_parking,5,parking);
                //archidniMap.addMarker(parking.getCoordinate(),R.drawable.marker_parking);
            }
        }
        archidniMap.renderClusters();
        //archidniMap.addPreparedAnnotations();
    }

    @Override
    public void showLinesOnList(ArrayList<Line> lines) {
        if (recyclerView.getAdapter()!=null && recyclerView.getAdapter() instanceof LineAdapter)
        {
            LineAdapter lineAdapter = (LineAdapter) recyclerView.getAdapter();
            lineAdapter.updateItems(lines);
        }
        else
        {
            LineAdapter lineAdapter = new LineAdapter(this, lines, new LineAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Line line) {
                    presenter.onLineItemClicked(line);
                }
            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(lineAdapter);
        }
    }



    @Override
    public void showZoomInsufficientLayout() {
        zoomInsufficientLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideZoomInsufficientLayout() {
        zoomInsufficientLayout.setVisibility(View.GONE);
    }

    @Override
    public void showSearchErrorLayout() {
        errorLayout.setVisibility(View.VISIBLE);
        retryFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchErrorLayout() {
        errorLayout.setVisibility(View.GONE);
        retryFab.setVisibility(View.GONE);
    }

    @Override
    public void startStationActivity(Station station) {
        Intent intent = new Intent(this, StationActivity.class);
        intent.putExtra(IntentUtils.STATION_STATION,station.toJson());
        startActivity(intent);
    }

    @Override
    public void startLineActivity(Line line) {
        Intent intent = new Intent(this, LineActivity.class);
        intent.putExtra(IntentUtils.LINE_LINE,line.toJson());
        startActivity(intent);
    }

    @Override
    public void startLinesStationsActivity() {
        Intent intent = new Intent(this, SearchLineStationActivity.class);
        startActivity(intent);
    }

    @Override
    public void showDrawerLayout(User user) {
        usernameText.setText(user.getFirstName()+" "+user.getLastName());
    }

    @Override
    public void logoutUser() {
        /*SharedPrefsUtils.disconnectUser(this);
        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);
        finish();*/
    }

    @Override
    public void hideOverlayLayout() {
        overlayLayout.setVisibility(View.GONE);
        getPathLayout.setVisibility(View.GONE);
    }

    @Override
    public void showOverlayLayout()
    {
        overlayLayout.setVisibility(View.VISIBLE);
        getPathLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void startFavoritesActivity() {
        Intent intent = new Intent(this, FavoritesActivity.class);
        startActivity(intent);
    }

    @Override
    public void showPlacesOnList(ArrayList<? extends MainActivityPlace> places, Coordinate userCoordinate) {
        recyclerView.setNestedScrollingEnabled(false);
        if (recyclerView.getAdapter()!=null && recyclerView.getAdapter() instanceof PlaceAdapter)
        {
            PlaceAdapter placeAdapter = (PlaceAdapter) recyclerView.getAdapter();
            placeAdapter.updateItems(places,userCoordinate);
        }
        else
        {
            PlaceAdapter placeAdapter = new PlaceAdapter(this, places, userCoordinate,
                    new PlaceAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(MainActivityPlace mainListPlace) {
                            if (mainListPlace instanceof Station)
                                presenter.onStationItemClick((Station)mainListPlace);
                            if (mainListPlace instanceof Parking)
                                presenter.onParkingClick((Parking)mainListPlace);
                        }
                    });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(placeAdapter);
        }
    }

    @Override
    public void showDisconnectDialog() {
        Dialog dialog = DialogUtils.buildDialog(this, "Deconnexion", "\n" +
                "Êtes-vous sûr(e) de vouloir vous déconnecter ? ", "Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.onLogoutDialogClick();
            }
        });
        dialog.show();
    }

    @Override
    public void showDisconnectProgressDialog()
    {
        disconnectProgressDialog = DialogUtils.buildProgressDialog("Veuillez patientez",this);
        disconnectProgressDialog.show();
    }

    @Override
    public void hideDisconnectProgressDialog() {
        disconnectProgressDialog.hide();
    }

    @Override
    public void disconnectUser() {
        SharedPrefsUtils.disconnectUser(this);
        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showDisconnectionError() {
        DialogUtils.buildInfoDialog(this,"Erreur",getString(R.string.error_happened)).show();
    }

    @Override
    public void animateCameraToLocation(Coordinate coordinate) {
        if (coordinate!=null)
            archidniMap.animateCamera(coordinate,15,250);
    }

    @Override
    public void moveCameraToLocation(Coordinate coordinate) {
        archidniMap.moveCamera(coordinate,15);
    }


    @Override
    public void showSlidingPanel() {
        container.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @Override
    public void setUserLocationEnabled(boolean enable) {
        archidniMap.setMyLocationEnabled(enable);
    }

    @Override
    public void onBackPressed() {
        if (container.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED)
        {
            container.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else
        {
            if (drawerOpened)
            {
                drawerLayout.closeDrawer(Gravity.START);
            }
            else
            {
                if (locationLayout.getVisibility()==View.VISIBLE)
                {
                    presenter.onMapShortClick();
                }
                else
                {
                    super.onBackPressed();
                }
            }
        }
    }

    private void slideInSearchText() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_top);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        searchLayout.startAnimation(animation);
    }

    private void slideOutSearchText() {
        searchLayout.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_out_top);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        searchLayout.startAnimation(animation);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.item_my_account:
                Intent intent10 = new Intent(this, AccountActivity.class);
                //startActivity(intent10);
                break;
            case R.id.item_my_favorites_lines :presenter.onFavoritesClick();
            break;
            case R.id.item_my_settings : Intent intent = new Intent(this,
                    SettingsActivity.class);
                    //startActivity(intent);
                break;
            case R.id.item_tarifs : Intent intent1 = new Intent(this, TarifsActivity.class);
                startActivity(intent1);
                break;
            case R.id.item_parkings : Intent intent3 = new Intent(this, ParkingsActivity.class);
                startActivity(intent3);
                break;
            case R.id.item_poles :
                Intent intent2 = new Intent(this, ExchangePolesActivity.class);
                startActivity(intent2);
                break;
            case R.id.item_notifications:
                Intent intent4 = new Intent(this, NotificationsActivity.class);
                startActivity(intent4);
                break;
            case R.id.item_report:
                Intent intent5 = new Intent(this, ReportChooseTypeActivity.class);
                startActivity(intent5);
                break;
            case R.id.item_disconnect:
                //Intent intent6 = new Intent(this, LoginActivity.class);
                //SharedPrefsUtils.disconnectUser(this);
                //startActivity(intent6);
                presenter.onDisconnectClick();
                break;
            case R.id.item_realtime_bus:
                Intent intent7 = new Intent(this, RealTimeBusActivity.class);
                startActivity(intent7);
                break;
        }
        return true;
    }

    @Override
    public Coordinate getMapCenter() {
        return archidniMap.getCenter();
    }
}
