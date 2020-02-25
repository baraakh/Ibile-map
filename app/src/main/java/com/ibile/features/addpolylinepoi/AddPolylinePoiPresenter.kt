package com.ibile.features.addpolylinepoi

import androidx.lifecycle.LifecycleOwner
import com.airbnb.epoxy.EpoxyController
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.LatLng
import com.ibile.data.database.entities.Marker

class AddPolylinePoiPresenter(private val viewModel: AddPolylinePoiViewModel) {
    private val databindingViewDataProvider by lazy { DatabindingViewDataProvider() }

    val databindingViewData
        get() = databindingViewDataProvider.data

    fun init(lifecycleOwner: LifecycleOwner, map: GoogleMap) {
        databindingViewDataProvider.init(viewModel, lifecycleOwner)

        val cameraPosition = map.cameraPosition.target
        viewModel.updateState { copy(cameraPosition = cameraPosition) }
    }

    fun reset() {
        viewModel.updateState { AddPolylinePoiViewModel.State() }
    }

    fun onCreateMarkerSuccess(marker: Marker) {
        if (viewModel.state.addMarkerAsync() == null || viewModel.state.addMarkerAsync() != marker.id)
            return
        reset()
    }

    fun onClickAddBtn(map: GoogleMap) {
        val (points, activePointIndex) = viewModel.state

        val newPointIndex = if (activePointIndex == -1) 0 else activePointIndex
        val newActivePointIndex =
            if (points.isEmpty()) 1 else if (activePointIndex == points.size) points.size + 1 else activePointIndex

        val updatedPoints =
            points.toMutableList().apply { add(newPointIndex, map.cameraPosition.target) }

        viewModel.updateState {
            copy(points = updatedPoints, activePointIndex = newActivePointIndex)
        }
    }

    fun onClickPrevBtn(map: GoogleMap) {
        val (_, activePointIndex) = viewModel.state
        if (activePointIndex == -1) return

        val newActivePointIndex = activePointIndex - 1
        val cameraPosition = map.cameraPosition.target
        viewModel.updateState {
            copy(activePointIndex = newActivePointIndex, cameraPosition = cameraPosition)
        }
    }

    fun onClickNextBtn(map: GoogleMap) {
        val (points, activePointIndex) = viewModel.state
        if (activePointIndex == points.size) return

        val newActivePointIndex = activePointIndex + 1
        val cameraPosition = map.cameraPosition.target
        viewModel.updateState {
            copy(activePointIndex = newActivePointIndex, cameraPosition = cameraPosition)
        }
    }

    fun onClickRemoveBtn(map: GoogleMap) {
        val (points, activePointIndex) = viewModel.state
        if (activePointIndex == -1 || activePointIndex == points.size) return

        val updatedPoints = points.toMutableList().apply { removeAt(activePointIndex) }
        val newActivePointIndex = when {
            updatedPoints.size == 0 -> -1
            activePointIndex == 0 -> 0
            else -> activePointIndex - 1
        }

        val cameraPosition = map.cameraPosition.target
        viewModel.updateState {
            copy(
                points = updatedPoints,
                activePointIndex = newActivePointIndex,
                cameraPosition = cameraPosition
            )
        }
    }

    fun onClickSaveBtn() {
        val marker = Marker.createPolyline(viewModel.state.points)
        viewModel.addMarker(marker)
    }

    fun onMapMove(cameraPosition: LatLng) {
        val (points, activePointIndex) = viewModel.state

        val updatedPoints = if (activePointIndex !in points.indices) points
        else points.toMutableList().apply { this[activePointIndex] = cameraPosition }

        viewModel.updateState { copy(points = updatedPoints, cameraPosition = cameraPosition) }
    }

    fun buildModels(map: GoogleMap, controller: EpoxyController) = with(viewModel.state) {
        points.forEachIndexed { index, point ->
            controller.addPolylinePoiPointView {
                // TODO: using a multiple of the index to avoid clash with ids of views from the
                //  main marker lists. Replace with UUIDs maybe or something else not based on indices
                id(index * 100000)
                map(map)
                position(point)
                isActive(index == activePointIndex)
                this.onUnbind { _, view -> view.remove() }
            }
        }
        controller.addPolylinePoiPolylineView {
            id("AddPolylinePoiPolylineView")
            map(map)
            points(getUpdatedPathPoints())
            this.onUnbind { _, view -> view.remove() }
        }
    }

    private fun getUpdatedPathPoints(): List<LatLng> {
        val (points, activePointIndex, cameraPosition) = viewModel.state

        return if (activePointIndex in points.indices) points
        else points.toMutableList().apply {
            val newPointTargetPosition = if (activePointIndex == -1) 0 else points.size
            add(newPointTargetPosition, cameraPosition)
        }
    }
}
