package com.archidni.archidni.Ui.PathNavigation;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.archidni.archidni.IntentUtils;
import com.archidni.archidni.Model.Coordinate;
import com.archidni.archidni.Model.Path.Path;
import com.archidni.archidni.Model.Path.PathStep;
import com.archidni.archidni.R;
import com.archidni.archidni.UiUtils.ArchidniGoogleMap;
import com.archidni.archidni.UiUtils.ArchidniMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;


import butterknife.BindView;
import butterknife.ButterKnife;

public class PathNavigationActivity extends AppCompatActivity implements PathNavigationContract.View {

    @BindView(R.id.image_step)
    ImageView mStepImage;
    @BindView(R.id.text_step)
    TextView mStepText;
    @BindView(R.id.text_step_details_1)
    TextView mStepDetailsText1;
    @BindView(R.id.text_step_details_2)
    TextView mStepDetailsText2;
    @BindView(R.id.image_next)
    View mNextButton;
    @BindView(R.id.image_previous)
    View mPreviousButton;
    @BindView(R.id.text_step_count)
    TextView mStepCountText;
    @BindView(R.id.text_my_position)
    TextView mMyPositionText;
    PathNavigationContract.Presenter mPresenter;

    ArchidniGoogleMap mUiMapUtils;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        Path path = (Path)bundle.getSerializable(IntentUtils.PATH);
        mPresenter = new PathNavigationPresenter(this,path);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_navigation);
        initViews(savedInstanceState);
        mPresenter.onCreate();
    }

    private void initViews (Bundle sis)
    {
        ButterKnife.bind(this);
        MapFragment mapView = (MapFragment) getFragmentManager().findFragmentById(R.id.mapView);
        mUiMapUtils = new ArchidniGoogleMap(this, mapView, new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mPresenter.onMapReady();
            }
        });
        mMyPositionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUiMapUtils.getUserLocation()!=null)
                {
                    mUiMapUtils.moveCamera(mUiMapUtils.getUserLocation());
                }
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.goToNextStep();
            }
        });
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.goToPreviousStep();
            }
        });
    }

    @Override
    public void showStepOnActivity(PathStep pathStep, int selectedStep) {
        Drawable drawable = ContextCompat.getDrawable(this,pathStep.getIconDrawableId());
        mStepImage.setImageDrawable(drawable);
        mStepText.setText(pathStep.getStepInstruction());
        if (pathStep.getStepDetails1()!=null)
        {   mStepDetailsText1.setVisibility(View.VISIBLE);
            mStepDetailsText1.setText(pathStep.getStepDetails1());
            if (!pathStep.getStepDetails1().equals(""))
                mStepDetailsText1.setText(pathStep.getStepDetails1());
            else
                mStepDetailsText1.setVisibility(View.GONE);
        }
        else
            mStepDetailsText1.setVisibility(View.GONE);
        if (pathStep.getStepDetails2()!=null)
        {
            mStepDetailsText2.setVisibility(View.VISIBLE);
            if (!pathStep.getStepDetails2().equals(""))
                mStepDetailsText2.setText(pathStep.getStepDetails2());
            else
                mStepDetailsText2.setVisibility(View.GONE);
        }
        else
            mStepDetailsText2.setVisibility(View.GONE);
        mStepCountText.setText("Etape "+selectedStep+"");
    }

    @Override
    public void showStepOnMap(PathStep pathStep, Path path) {
        if (pathStep.getCoordinate()!=null)
        {
            mUiMapUtils.clearMap();
            mUiMapUtils.animateCamera(pathStep.getCoordinate(),16,500);
            mUiMapUtils.addMarker(pathStep.getCoordinate(),R.drawable.ic_marker_green_24dp);
            //mUiMapUtils.populateMap();
        }
        else
        {
            mUiMapUtils.clearMap();
            mUiMapUtils.preparePolyline(this,pathStep.getPolyline(),R.color.colorGreen,8);
            /*int i =0;
            for (Coordinate coordinate:pathStep.getPolyline())
            {
                mUiMapUtils.addMarker(coordinate,R.drawable.marker_selected);
            }*/
            mUiMapUtils.addMarker(pathStep.getPolyline().get(0),R.drawable.ic_marker_green_24dp);
            mUiMapUtils.addMarker(pathStep.getPolyline().get(pathStep.getPolyline().size()-1),
                    R.drawable.ic_marker_red_24dp);
            mUiMapUtils.animateCameraToBounds(pathStep.getPolyline(),
                    50,250);
            mUiMapUtils.addPreparedAnnotations();
        }
    }

    @Override
    public void showPathOnMap(Path path) {

    }




}
