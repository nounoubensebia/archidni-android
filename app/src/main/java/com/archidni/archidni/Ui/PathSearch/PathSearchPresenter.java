package com.archidni.archidni.Ui.PathSearch;

import android.content.Context;

import com.archidni.archidni.Data.Paths.PathRepository;
import com.archidni.archidni.IntentUtils;
import com.archidni.archidni.Model.Path.PathPreferences;
import com.archidni.archidni.Model.Path.PathSettings;
import com.archidni.archidni.Model.Path.Path;
import com.archidni.archidni.Model.Places.PathPlace;
import com.archidni.archidni.Model.TransportMean;
import com.archidni.archidni.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by noure on 03/02/2018.
 */

public class PathSearchPresenter implements PathSearchContract.Presenter {

    private PathSearchContract.View view;
    private PathSettings pathSettings;
    private PathRepository pathRepository;
    private ArrayList<Path> paths;

    public PathSearchPresenter(PathSearchContract.View view, PathPlace origin, PathPlace destination) {
        this.view = view;
        pathSettings = new PathSettings(origin,destination,
                TimeUtils.getSecondsFromMidnight(),TimeUtils.getCurrentTimeInSeconds(), PathPreferences.DEFAULT);
        String originString = (origin!=null) ? origin.getTitle():"";
        String destinationString = (destination!=null) ? destination.getTitle():"";
        view.showOriginAndDestinationLabels(originString,destinationString);
        pathRepository = new PathRepository();
    }

    private void updateOriginDestination ()
    {
        String originString = (pathSettings.getOrigin()!=null) ? pathSettings.getOrigin().getTitle():"";
        String destinationString = (pathSettings.getDestination().getTitle()!=null) ?
                pathSettings.getDestination().getTitle():"";
        view.showOriginAndDestinationLabels(originString,destinationString);
        view.showOriginAndDestinationOnMap(pathSettings.getOrigin(), pathSettings.getDestination());
    }

    @Override
    public void onMapReady() {
        view.moveCameraToCoordinate(pathSettings.getDestination().getCoordinate());
        view.showOriginAndDestinationOnMap(pathSettings.getOrigin(), pathSettings.getDestination());
    }

    @Override
    public void onMapLoaded() {
        view.hideMapLoadingLayout();
    }

    @Override
    public void onSearchPathsClick(Context context) {
        if (pathSettings.getOrigin()!=null&&pathSettings.getDestination()!=null) {
            view.showLoadingBar();
            pathRepository.getPaths(context, pathSettings, new PathRepository.OnSearchCompleted() {
                @Override
                public void onResultsFound(ArrayList<Path> foundPaths) {
                    paths = foundPaths;
                    ArrayList<Path> pathsToShow = getSortedAndFilteredPaths();
                    view.showPathSuggestions(pathsToShow,pathSettings);
                }

                @Override
                public void onError() {
                    view.hidePathsLayout();
                    view.showErrorMessage();
                }
            });
        }
        else
        {
            view.showOriginNotSet();
        }
    }

    @Override
    public void lookForLocation(int requestType) {
        view.startSearchActivity(requestType);
    }

    @Override
    public void onActivityResult(int requestType, PathPlace newPlace) {
        if (requestType == IntentUtils.SearchIntents.TYPE_LOOK_FOR_OR)
        {
            if (pathSettings.getOrigin()==null||!newPlace.getCoordinate().equals(pathSettings.getOrigin().getCoordinate()))
            {
               view.hidePathsLayout();
            }
            pathSettings.setOrigin(newPlace);
        }
        else
        {
            if (!newPlace.getCoordinate().equals(pathSettings.getDestination().getCoordinate()))
            {
                view.hidePathsLayout();
            }
            pathSettings.setDestination(newPlace);
        }
        updateOriginDestination();
    }

    private ArrayList<Path> getSortedAndFilteredPaths()
    {
        Collections.sort(paths, new Comparator<Path>() {
            @Override
            public int compare(Path path, Path t1) {
                PathPreferences pathPreferences = pathSettings.getPathPreferences();

                switch (pathPreferences.getSortPreference())
                {
                    case PathPreferences.SORT_BY_MINIMUM_TIME :
                        return (int) (path.getDuration()-t1.getDuration());
                    case PathPreferences.SORT_BY_MINIMUM_WALKING_TIME :
                        return (int) (path.getWalkingTime()-t1.getWalkingTime());
                    case PathPreferences.SORT_BY_MINIMUM_TRANSFERS :
                        return (path.getTransferNumber()-t1.getTransferNumber());
                }
                return -1;
            }
        });
        ArrayList<Path> sortedPaths = new ArrayList<>();
        for (Path path:paths)
        {
            boolean found = false;
            ArrayList<TransportMean> blackListedTransports = pathSettings.getPathPreferences()
                    .getTransportModesNotUsed();
            ArrayList<TransportMean> pathTransportMeans = path.getTransportMeans();
            for (TransportMean transportMean:blackListedTransports)
            {
                if (pathTransportMeans.contains(transportMean))
                {
                    found = true;
                }
            }
            if (!found)
            {
                sortedPaths.add(path);
            }
        }
        return sortedPaths;
    }

    @Override
    public void onDepartureTimeClick() {
        view.showSetTimeDialog(pathSettings.getDepartureTime());
    }

    @Override
    public void onDepartureDateClick() {
        view.showSetDateDialog(pathSettings.getDepartureDate());
    }

    @Override
    public void updateTime(long departureTime) {
        view.updateTime(departureTime);
        if (departureTime!=pathSettings.getDepartureTime())
        {
            view.hidePathsLayout();
        }
        pathSettings.setDepartureTime(departureTime);
    }

    @Override
    public void updateDate(long departureDate) {
        view.updateDate(departureDate);
        if (departureDate!=pathSettings.getDepartureDate())
        {
            view.hidePathsLayout();
        }
        pathSettings.setDepartureDate(departureDate);
    }

    @Override
    public void onPathItemClick(Path path) {
        view.startPathDetailsActivity(path);
    }

    @Override
    public void onStop(Context context) {
        pathRepository.cancelRequests(context);
    }

    @Override
    public void onOptionsLayoutClicked() {
        view.showOptionsDialog(pathSettings.getPathPreferences());
    }

    @Override
    public void onOptionsUpdated(PathPreferences pathPreferences) {
        this.pathSettings.setPathPreferences(pathPreferences);
        if (paths!=null)
            view.showPathSuggestions(getSortedAndFilteredPaths(),pathSettings);
    }
}
