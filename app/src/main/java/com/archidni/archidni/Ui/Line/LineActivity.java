package com.archidni.archidni.Ui.Line;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.archidni.archidni.IntentUtils;
import com.archidni.archidni.Model.Coordinate;
import com.archidni.archidni.Model.Transport.Line;
import com.archidni.archidni.Model.Transport.Station;
import com.archidni.archidni.R;
import com.archidni.archidni.Ui.Adapters.StationAdapter;
import com.archidni.archidni.Ui.Adapters.StationInsideLineAdapter;
import com.archidni.archidni.Ui.Station.StationActivity;
import com.archidni.archidni.UiUtils.ArchidniMap;
import com.archidni.archidni.UiUtils.ViewUtils;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LineActivity extends AppCompatActivity implements LineContract.View {

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.text_name)
    TextView nameText;
    @BindView(R.id.list_station)
    ListView listView;

    private Menu mMenu;

    ArchidniMap archidniMap;
    LineContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Line line = Line.fromJson(getIntent().getExtras().getString(IntentUtils.LINE_LINE));
        presenter = new LinePresenter(line,this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        initViews(savedInstanceState);
        presenter.onCreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        presenter.loadMenu();
        return super.onCreateOptionsMenu(this.mMenu);
    }

    private void initViews (Bundle bundle)
    {
        ButterKnife.bind(this);
        archidniMap = new ArchidniMap(mapView, bundle, new ArchidniMap.OnMapReadyCallback() {
            @Override
            public void onMapReady() {
                presenter.onMapReady();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void showLineOnMap(Line line) {
        animateCameraToFitLine(line);
        archidniMap.preparePolyline(this,line.getPolyline(),line.getTransportMean().getColor());
        for (Station station:line.getStations())
        {
            archidniMap.prepareMarker(station.getCoordinate(),line.getTransportMean()
                    .getCircleDrawable(),0.5f,0.5f);
        }
        archidniMap.addPreparedAnnotations();
    }

    private void animateCameraToFitLine (Line mLine)
    {
        ArrayList<Coordinate> bounds = new ArrayList<>();
        for (Station station: mLine.getStations())
        {
            bounds.add(station.getCoordinate());
        }
        float padding = ViewUtils.dpToPx(this,8);
        archidniMap.animateCameraToBounds(bounds,(int)padding,1000);
    }

    @Override
    public void showLineOnActivity(final Line line) {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(line.getName());
        getSupportActionBar().setElevation(0);
        nameText.setText(line.getName());
        StationInsideLineAdapter stationInsideLineAdapter = new StationInsideLineAdapter(this,line.getStations(),null);
        listView.setAdapter(stationInsideLineAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onStationClicked(line.getStations().get(position));
            }
        });
        listView.setDividerHeight(0);
        ViewUtils.justifyListViewHeightBasedOnChildren(listView);
    }

    @Override
    public void setTheme(Line line) {
        setTheme(line.getTransportMean().getTheme());
    }

    @Override
    public void showSelectedStation(Station station) {
        archidniMap.animateCamera(station.getCoordinate(),16,100);
        StationInsideLineAdapter stationInsideLineAdapter = (StationInsideLineAdapter) listView.getAdapter();
        stationInsideLineAdapter.selectStation(station);
        invalidateOptionsMenu();
    }

    @Override
    public void deselectStation(Line line) {
        animateCameraToFitLine(line);
        StationInsideLineAdapter stationInsideLineAdapter = (StationInsideLineAdapter) listView.getAdapter();
        stationInsideLineAdapter.selectStation(null);
        invalidateOptionsMenu();
    }

    @Override
    public void inflateTripMenu() {
        inflateMenu();
        MenuItem item = mMenu.findItem(R.id.item_info_station);
        item.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_info_station:
                presenter.onStationDetailsClicked();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void inflateStationMenu() {
        inflateMenu();
        MenuItem item = mMenu.findItem(R.id.item_info_station);
        item.setVisible(true);
    }

    private void inflateMenu ()
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trip, mMenu);
    }

    @Override
    public void startStationActivity(Station station) {
        Intent intent = new Intent(this, StationActivity.class);
        intent.putExtra(IntentUtils.STATION_STATION,station.toJson());
        startActivity(intent);
    }
}