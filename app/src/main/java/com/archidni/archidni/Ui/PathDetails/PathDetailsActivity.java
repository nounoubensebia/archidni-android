package com.archidni.archidni.Ui.PathDetails;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.archidni.archidni.IntentUtils;
import com.archidni.archidni.Model.Coordinate;
import com.archidni.archidni.Model.Path.Path;
import com.archidni.archidni.Model.Path.PathInstruction;
import com.archidni.archidni.R;
import com.archidni.archidni.Ui.Adapters.PathInstructionAdapter;
import com.archidni.archidni.Ui.PathNavigation.PathNavigationActivity;
import com.archidni.archidni.UiUtils.ArchidniGoogleMap;
import com.archidni.archidni.UiUtils.ArchidniMap;
import com.archidni.archidni.UiUtils.ViewUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PathDetailsActivity extends AppCompatActivity implements PathDetailsContract.View {

    @BindView(R.id.list_instructions)
    ListView instructionsList;
    MapFragment mapView;
    @BindView(R.id.text_start_navigation)
    TextView startNavigationText;
    @BindView(R.id.text_total_duration)
    TextView durationText;

    private ArchidniGoogleMap archidniMap;

    private PathDetailsContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = new PathDetailsPresenter((Path)getIntent().getExtras()
                .getSerializable(IntentUtils.PATH),this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_details);
        initViews(savedInstanceState);
        presenter.onCreate();
    }

    private void initViews (Bundle bundle)
    {
        ButterKnife.bind(this);

        mapView = (MapFragment) getFragmentManager().findFragmentById(R.id.mapView);
        archidniMap = new ArchidniGoogleMap(this,mapView, new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                presenter.onMapReady();
            }
        });


        /*archidniMap = new ArchidniMap(mapView, bundle, new ArchidniMap.OnMapReadyCallback() {
            @Override
            public void onMapReady() {
                presenter.onMapReady();
                archidniMap.setMyLocationEnabled(true);
            }
        });*/
        startNavigationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onStartNavigationClick();
            }
        });
    }

    @Override
    public void showPathOnActivity(final Path path) {
        durationText.setText(path.getDuration()/60+" minutes");
        ArrayList<PathInstruction> pathInstructions = path.getPathInstructions();
        final PathInstructionAdapter pathInstructionAdapter = new PathInstructionAdapter(this,pathInstructions);
        pathInstructionAdapter.setOnCoordinateSelected(new PathInstructionAdapter.OnCoordinateSelected() {
            @Override
            public void onCoordinateSelected(Coordinate coordinate, int selectedItem) {
                if (selectedItem!=-1)
                {
                    archidniMap.clearMap();
                    archidniMap.animateCamera(coordinate,16,500);
                    showInstructionsAnnotations(path);
                }
                else
                {
                    archidniMap.clearMap();
                    archidniMap.animateCameraToBounds(path.getPolyline(),100,500);
                    showInstructionsAnnotations(path);
                }
                pathInstructionAdapter.selectElement(selectedItem);
            }
        });
        pathInstructionAdapter.setOnPolylineSelected(new PathInstructionAdapter.OnPolylineSelected() {
            @Override
            public void onPolylineSelected(ArrayList<Coordinate> polyline, int selectedItem) {
                if (selectedItem!=-1)
                {
                    archidniMap.clearMap();
                    archidniMap.animateCameraToBounds(polyline,50,500);
                    archidniMap.preparePolyline(PathDetailsActivity.this,polyline,R.color.black,15);
                    archidniMap.addPreparedAnnotations();
                    showInstructionsAnnotations(path);

                }
                else
                {
                    archidniMap.clearMap();
                    archidniMap.animateCameraToBounds(path.getPolyline(),100,500);
                    showInstructionsAnnotations(path);
                }
                pathInstructionAdapter.selectElement(selectedItem);
            }
        });
        instructionsList.setAdapter(pathInstructionAdapter);
        instructionsList.setDividerHeight(0);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewUtils.justifyListViewHeightBasedOnChildren(instructionsList);
            }
        },250);

    }

    private void showInstructionsAnnotations(Path path)
    {
        ArrayList<Coordinate> pathPolyline = path.getPolyline();
        archidniMap.preparePolyline(this,pathPolyline,R.color.opaqGray,15);
        archidniMap.addMarker(pathPolyline.get(0),R.drawable.ic_marker_blue_24dp);
        archidniMap.addMarker(pathPolyline.get(pathPolyline.size()-1),R.drawable.ic_marker_red_24dp);
        archidniMap.addPreparedAnnotations();
    }

    @Override
    public void showPathOnMap(Path path) {
        ArrayList<Coordinate> pathPolyline = path.getPolyline();
        showInstructionsAnnotations(path);
        archidniMap.animateCameraToBounds(pathPolyline,100,500);
    }

    @Override
    public void startPathNavigationActivity(Path path) {
        Intent intent = new Intent(this, PathNavigationActivity.class);
        intent.putExtra(IntentUtils.PATH,path);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        //mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        //mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //mapView.onSaveInstanceState(outState);
    }
}
